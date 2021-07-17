package skaro.pokedex.core;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.patreon.PatreonAPI;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.Role;
import discord4j.core.object.presence.Presence;
import discord4j.core.shard.ShardingStrategy;
import discord4j.gateway.GatewayReactorResources;
import discord4j.store.api.mapping.MappingStoreService;
import discord4j.store.api.noop.NoOpStoreService;
import discord4j.store.api.service.StoreService;
import discord4j.store.jdk.JdkStoreService;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import skaro.pokedex.communicator.Publisher;
import skaro.pokedex.communicator.publish_recipients.BotsDiscordRecipient;
import skaro.pokedex.communicator.publish_recipients.CarbonitexRecipient;
import skaro.pokedex.communicator.publish_recipients.DiscordBotsRecipient;
import skaro.pokedex.communicator.publish_recipients.PublicationRecipient;
import skaro.pokedex.communicator.publish_recipients.RecipientConfig;
import skaro.pokedex.communicator.publish_recipients.Recipients;
import skaro.pokedex.data_processor.ChannelRateLimiter;
import skaro.pokedex.data_processor.LearnMethodData;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.commands.AbilityCommand;
import skaro.pokedex.data_processor.commands.AboutCommand;
import skaro.pokedex.data_processor.commands.CardCommand;
import skaro.pokedex.data_processor.commands.CommandsCommand;
import skaro.pokedex.data_processor.commands.CoverageCommand;
import skaro.pokedex.data_processor.commands.DataCommand;
import skaro.pokedex.data_processor.commands.DexCommand;
import skaro.pokedex.data_processor.commands.HelpCommand;
import skaro.pokedex.data_processor.commands.InviteCommand;
import skaro.pokedex.data_processor.commands.ItemCommand;
import skaro.pokedex.data_processor.commands.LearnCommand;
import skaro.pokedex.data_processor.commands.MoveCommand;
import skaro.pokedex.data_processor.commands.NatureCommand;
import skaro.pokedex.data_processor.commands.PatreonCommand;
import skaro.pokedex.data_processor.commands.RandpokeCommand;
import skaro.pokedex.data_processor.commands.SearchCommand;
import skaro.pokedex.data_processor.commands.SetCommand;
import skaro.pokedex.data_processor.commands.ShinyCommand;
import skaro.pokedex.data_processor.commands.StatsCommand;
import skaro.pokedex.data_processor.commands.WeakCommand;
import skaro.pokedex.data_processor.commands.ZMoveCommand;
import skaro.pokedex.data_processor.formatters.AbilityResponseFormatter;
import skaro.pokedex.data_processor.formatters.CardResponseFormatter;
import skaro.pokedex.data_processor.formatters.CoverageResponseFormatter;
import skaro.pokedex.data_processor.formatters.DataResponseFormatter;
import skaro.pokedex.data_processor.formatters.DexResponseFormatter;
import skaro.pokedex.data_processor.formatters.ItemResponseFormatter;
import skaro.pokedex.data_processor.formatters.LearnResponseFormatter;
import skaro.pokedex.data_processor.formatters.MoveResponseFormatter;
import skaro.pokedex.data_processor.formatters.NatureResponseFormatter;
import skaro.pokedex.data_processor.formatters.RandpokeResponseFormatter;
import skaro.pokedex.data_processor.formatters.SearchResponseFormatter;
import skaro.pokedex.data_processor.formatters.SetResponseFormatter;
import skaro.pokedex.data_processor.formatters.ShinyResponseFormatter;
import skaro.pokedex.data_processor.formatters.StatsResponseFormatter;
import skaro.pokedex.data_processor.formatters.WeakResponseFormatter;
import skaro.pokedex.data_processor.formatters.ZMoveResponseFormatter;
import skaro.pokedex.input_processor.InputProcessor;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.CommandService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.DiscordService;
import skaro.pokedex.services.EmojiService;
import skaro.pokedex.services.EnvironmentConfigurationService;
import skaro.pokedex.services.FlexCacheService;
import skaro.pokedex.services.FlexCacheService.CachedResource;
import skaro.pokedex.services.PerkService;
import skaro.pokedex.services.PerkTierManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceException;
import skaro.pokedex.services.ServiceManager;
import skaro.pokedex.services.ServiceManager.ServiceManagerBuilder;
import skaro.pokedex.services.ServiceType;
import skaro.pokedex.services.TypeService;

public class Pokedex 
{
	public static void main(String[] args) throws Exception
	{
		EnvironmentConfigurationService configurationService = new EnvironmentConfigurationService();		
		int[] shardsToManage = configurationService.getShardIndexes();
		int totalShards = configurationService.getShardTotal();
		
		System.out.println("[Pokedex main] Loading configurations...");
		
		Scheduler scheduler = Schedulers.newParallel("pokedex_pool", Runtime.getRuntime().availableProcessors() * 2);
		CommandService commandMap = new CommandService();
		PerkService perkService = createPatreonService(configurationService);
		PokeFlexService pokeFlexService = createPokeFlexService(configurationService, scheduler);
		FlexCacheService flexCacheService = createCacheService(pokeFlexService);
		TypeService typeService = new TypeService();
		
		PokedexApplicationManager manager = PokedexApplicationManager.PokedexConfigurator.newInstance()
								.withService(configurationService)
								.withService(commandMap)
								.withService(createDiscordService(configurationService, scheduler, shardsToManage, totalShards))
								.withService(perkService)
								.withService(new ColorService())
								.withService(new EmojiService())
								.withService(pokeFlexService)
								.withService(flexCacheService)
								.withService(typeService)
								.configure();
		
		populateCommandMap(manager, commandMap);
		perkService.setServiceManager(ServiceManager.ServiceManagerBuilder.newInstance(manager).addService(ServiceType.DISCORD).build());
		typeService.setServiceManager(ServiceManager.ServiceManagerBuilder.newInstance(manager).addService(ServiceType.CACHE).build());
		
		System.out.println("[Pokedex main] Done");
		System.out.println("[Pokedex main] Setting up publisher...");
		Publisher publisher = setUpPublisher(manager, shardsToManage, totalShards);
		publisher.schedulePublicationFrequency(1, TimeUnit.HOURS);
		System.out.println("[Pokedex main] Done");
		
		DiscordService service = (DiscordService)manager.getService(ServiceType.DISCORD);
		GatewayDiscordClient gatewayClient = service.getV3Client();
		InputProcessor inputProcessor = new InputProcessor(commandMap, 206147275775279104L);
		ChannelRateLimiter rateLimiter = new ChannelRateLimiter(2, Duration.ofSeconds(10));
		DiscordMessageEventHandler messageHandler = new DiscordMessageEventHandler(inputProcessor, rateLimiter);
		
		gatewayClient.getEventDispatcher().on(new ReactiveEventAdapter() {
			@Override
			public org.reactivestreams.Publisher<?> onMessageCreate(MessageCreateEvent event) {
				return messageHandler.onMessageCreateEvent(event);
			}
			
			@Override
			public org.reactivestreams.Publisher<?> onMessageUpdate(MessageUpdateEvent event) {
				return messageHandler.onMessageEditEvent(event);
			}
		}).subscribe(inputOfServedRequest -> {}, error -> error.printStackTrace());
	}
	
	private static FlexCacheService createCacheService(PokeFlexService factory)
	{
		FlexCacheService result = new FlexCacheService();
		result.addCachedResource(CachedResource.LEARN_METHOD, new LearnMethodData(factory));
		result.addCachedResource(CachedResource.TYPE, new TypeData(factory));
		
		return result;
	}
	
	private static DiscordService createDiscordService(EnvironmentConfigurationService configService, Scheduler scheduler, int[] shardsToManage, int shardCount)
	{
		String discordToken = configService.getDiscordAuthToken();
		StoreService storeService = MappingStoreService.create()
				.setMappings(new NoOpStoreService(), Role.class, GuildEmoji.class, Presence.class, VoiceState.class, Guild.class)
				.setFallback(new JdkStoreService());
		ShardingStrategy strategy = ShardingStrategy.builder()
				.count(shardCount)
				.indices(shardsToManage)
				.build();
		
		GatewayDiscordClient discordClient = DiscordClient.create(discordToken)
			.gateway()
			.setSharding(strategy)
			.setStoreService(storeService)
			.setGatewayReactorResources(reactorResources -> new GatewayReactorResources(reactorResources, scheduler))
			.login()
			.block();
		
		return new DiscordService(discordClient);
	}
	
	private static PerkService createPatreonService(EnvironmentConfigurationService configService) {
		String patreonAccessToken = configService.getPatreonAuthToken();
		PatreonAPI patreonClient = new PatreonAPI(patreonAccessToken);
		return new PerkService(patreonClient, new PerkTierManager());
	}
	
	private static PokeFlexService createPokeFlexService(EnvironmentConfigurationService configService, Scheduler scheduler)
	{
		return new PokeFlexService(configService.getPokeFlexURL(), scheduler);
	}
	
	private static void populateCommandMap(PokedexApplicationManager manager, CommandService commandService) throws ServiceException, ServiceConsumerException
	{
		ServiceManagerBuilder commandServiceBuilder = ServiceManager.ServiceManagerBuilder.newInstance(manager)
				.addService(ServiceType.COLOR)
				.addService(ServiceType.CONFIG);
		ServiceManagerBuilder serviceBuilderColor = ServiceManager.ServiceManagerBuilder.newInstance(manager)
				.addService(ServiceType.COLOR)
				.addService(ServiceType.CONFIG);
		ServiceManagerBuilder serviceBuilderEmoji = ServiceManager.ServiceManagerBuilder.newInstance(manager)
				.addService(ServiceType.COLOR)
				.addService(ServiceType.EMOJI)
				.addService(ServiceType.CONFIG);
		
		//ColorService
		commandService.addCommand(new PatreonCommand(commandServiceBuilder.build()));
		commandService.addCommand(new InviteCommand(commandServiceBuilder.build()));
		
		//ColorService, CommandService
		commandServiceBuilder.addService(ServiceType.COMMAND);
		commandService.addCommand(new HelpCommand(commandServiceBuilder.build()));
		commandService.addCommand(new CommandsCommand(commandServiceBuilder.build()));
		
		//ColorService, ConfigService
		commandServiceBuilder.removeService(ServiceType.COMMAND);
		commandService.addCommand(new AboutCommand(commandServiceBuilder.build()));
		
		//ColorService, PokeFlexService, PerkService
		commandServiceBuilder.addService(ServiceType.POKE_FLEX);
		commandServiceBuilder.addService(ServiceType.PERK);
		commandService.addCommand(new AbilityCommand(commandServiceBuilder.build(), new AbilityResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new RandpokeCommand(commandServiceBuilder.build(), new RandpokeResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new SetCommand(commandServiceBuilder.build(), new SetResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new ShinyCommand(commandServiceBuilder.build(), new ShinyResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new StatsCommand(commandServiceBuilder.build(), new StatsResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new NatureCommand(commandServiceBuilder.build(), new NatureResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new CardCommand(commandServiceBuilder.build(), new CardResponseFormatter(serviceBuilderEmoji.build())));
		
		//ColorService, PokeFlexService, PerkService, CacheService
		commandServiceBuilder.addService(ServiceType.CACHE);
		commandService.addCommand(new ItemCommand(commandServiceBuilder.build(), new ItemResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new LearnCommand(commandServiceBuilder.build(), new LearnResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new MoveCommand(commandServiceBuilder.build(), new MoveResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new DataCommand(commandServiceBuilder.build(), new DataResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new ZMoveCommand(commandServiceBuilder.build(), new ZMoveResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new SearchCommand(commandServiceBuilder.build(), new SearchResponseFormatter(serviceBuilderEmoji.build(), 10), 10));
		
		//ColorService, PokeFlexService, PerkService, TypeService
		commandServiceBuilder.removeService(ServiceType.CACHE);
		commandServiceBuilder.addService(ServiceType.TYPE);
		commandService.addCommand(new WeakCommand(commandServiceBuilder.build(), new WeakResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new CoverageCommand(commandServiceBuilder.build(), new CoverageResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new DexCommand(commandServiceBuilder.build(), new DexResponseFormatter(serviceBuilderColor.build())));
	}
	
	private static Publisher setUpPublisher(PokedexApplicationManager manager, int[] shardsToManage, int totalShards) throws ServiceException, ServiceConsumerException
	{
		ConfigurationService configService = (ConfigurationService)manager.getService(ServiceType.CONFIG);
		ServiceManager serviceManager = ServiceManager.ServiceManagerBuilder.newInstance(manager)
				.addService(ServiceType.DISCORD)
				.addService(ServiceType.CONFIG)
				.build();
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		List<PublicationRecipient> recipients = new ArrayList<>();
		
		RecipientConfig botsDiscordConfig = configService.getPublishRecipientConfig(Recipients.BOTS_DISCORD);
		recipients.add(new BotsDiscordRecipient(botsDiscordConfig));
		
		RecipientConfig discordBotConfig = configService.getPublishRecipientConfig(Recipients.DISCORD_BOTS);
		recipients.add(new DiscordBotsRecipient(discordBotConfig));
		
		if(shardsToManage[0] == 3) { //arbitrary
			RecipientConfig carbonitexConfig = configService.getPublishRecipientConfig(Recipients.CARBONITEX);
			recipients.add(new CarbonitexRecipient(carbonitexConfig));
		}
		
		return new Publisher(serviceManager, recipients, executor);
	}
	
}
