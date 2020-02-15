package skaro.pokedex.core;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.patreon.PatreonAPI;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.presence.Presence;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import skaro.pokedex.communicator.Publisher;
import skaro.pokedex.communicator.publish_recipients.BotsDiscordRecipient;
import skaro.pokedex.communicator.publish_recipients.CarbonitexRecipient;
import skaro.pokedex.communicator.publish_recipients.DiscordBotsRecipient;
import skaro.pokedex.data_processor.ChannelRateLimiter;
import skaro.pokedex.data_processor.LearnMethodData;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.commands.AbilityCommand;
import skaro.pokedex.data_processor.commands.AboutCommand;
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
import skaro.pokedex.services.ConfigurationType;
import skaro.pokedex.services.DiscordService;
import skaro.pokedex.services.EmojiService;
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
import skaro.pokedex.services.TextToSpeechService;
import skaro.pokedex.services.TypeService;

public class Pokedex 
{
	public static void main(String[] args) throws Exception
	{
		if(args.length != 2)
		{
			System.out.println("Usage: <shard ID> <total shards>");
			System.exit(1);
		}
		
		int shardToManage = -1;
		int totalShards = -1;
		try
		{ 
			shardToManage = Integer.parseInt(args[0]);
			totalShards = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e)
		{
			System.out.println("[Pokedex main] Error parsing command line arguments.");
			System.exit(1);
		}
		
		System.out.println("[Pokedex main] Loading configurations...");
		ConfigurationService configurationService = ConfigurationService.initialize(ConfigurationType.PRODUCTION);
		Scheduler scheduler = Schedulers.newParallel("pokedex_pool", Runtime.getRuntime().availableProcessors() * 6);
		CommandService commandMap = new CommandService();
		PerkService perkService = createPatreonService(configurationService);
		PokeFlexService pokeFlexService = createPokeFlexService(configurationService, scheduler);
		FlexCacheService flexCacheService = createCacheService(pokeFlexService);
		TypeService typeService = new TypeService();
		
		PokedexApplicationManager manager = PokedexApplicationManager.PokedexConfigurator.newInstance()
								.withService(configurationService)
								.withService(commandMap)
								.withService(createDiscordService(configurationService, scheduler, shardToManage, totalShards))
								.withService(perkService)
								.withService(new ColorService())
								.withService(new EmojiService())
								.withService(pokeFlexService)
								.withService(new TextToSpeechService())
								.withService(flexCacheService)
								.withService(typeService)
								.configure();
		
		populateCommandMap(manager, commandMap);
		perkService.setServiceManager(ServiceManager.ServiceManagerBuilder.newInstance(manager).addService(ServiceType.DISCORD).build());
		typeService.setServiceManager(ServiceManager.ServiceManagerBuilder.newInstance(manager).addService(ServiceType.CACHE).build());
		
		System.out.println("[Pokedex main] Done");
		System.out.println("[Pokedex main] Setting up publisher...");
		Publisher publisher = setUpPublisher(manager, shardToManage, totalShards);
		publisher.schedulePublicationFrequency(1, TimeUnit.HOURS);
		System.out.println("[Pokedex main] Done");
		
		System.out.println("[Pokedex main] Logging into Discord...");
		DiscordService service = (DiscordService)manager.getService(ServiceType.DISCORD);
		DiscordClient client = service.getV3Client();
		InputProcessor inputProcessor = new InputProcessor(commandMap, 206147275775279104L);
		ChannelRateLimiter rateLimiter = new ChannelRateLimiter(2, Duration.ofSeconds(10));
		DiscordMessageEventHandler messageHandler = new DiscordMessageEventHandler(inputProcessor, rateLimiter);
		
		client.getEventDispatcher().on(MessageCreateEvent.class)
			.flatMap(event -> messageHandler.onMessageCreateEvent(event))
	        .subscribe(inputOfServedRequest -> System.out.println(inputOfServedRequest), error -> error.printStackTrace());
		
		client.getEventDispatcher().on(MessageUpdateEvent.class)
			.flatMap(event -> messageHandler.onMessageEditEvent(event))
			.subscribe(inputOfServedRequest -> System.out.println(inputOfServedRequest), error -> error.printStackTrace());

		client.login().block(); 
	}
	
	private static FlexCacheService createCacheService(PokeFlexService factory)
	{
		FlexCacheService result = new FlexCacheService();
		result.addCachedResource(CachedResource.LEARN_METHOD, new LearnMethodData(factory));
		result.addCachedResource(CachedResource.TYPE, new TypeData(factory));
		
		return result;
	}
	
	private static DiscordService createDiscordService(ConfigurationService configService, Scheduler scheduler, int shardID, int shardCount)
	{
		Optional<String> discordToken = configService.getAuthToken("discord");
		DiscordClient discordClient = new DiscordClientBuilder(discordToken.get())
				.setEventScheduler(scheduler)
				.setShardCount(shardCount)
				.setShardIndex(shardID)
				.setInitialPresence(Presence.online())
				.build();
		return new DiscordService(discordClient);
	}
	
	private static PerkService createPatreonService(ConfigurationService configService)
	{
		Optional<String> patreonAccessToken = configService.getConfigData("access_token", "patreon");
		PatreonAPI patreonClient = new PatreonAPI(patreonAccessToken.get());
		return new PerkService(patreonClient, new PerkTierManager());
	}
	
	private static PokeFlexService createPokeFlexService(ConfigurationService configService, Scheduler scheduler)
	{
		return new PokeFlexService(configService.getPokeFlexURL(), scheduler);
	}
	
	private static void populateCommandMap(PokedexApplicationManager manager, CommandService commandService) throws ServiceException, ServiceConsumerException
	{
		ServiceManagerBuilder commandServiceBuilder = ServiceManager.ServiceManagerBuilder.newInstance(manager)
				.addService(ServiceType.COLOR);
		ServiceManagerBuilder serviceBuilderColor = ServiceManager.ServiceManagerBuilder.newInstance(manager)
				.addService(ServiceType.COLOR);
		ServiceManagerBuilder serviceBuilderEmoji = ServiceManager.ServiceManagerBuilder.newInstance(manager)
				.addService(ServiceType.COLOR)
				.addService(ServiceType.EMOJI);
		
		//ColorService
		commandService.addCommand(new PatreonCommand(commandServiceBuilder.build()));
		commandService.addCommand(new InviteCommand(commandServiceBuilder.build()));
		
		//ColorService, CommandService
		commandServiceBuilder.addService(ServiceType.COMMAND);
		commandService.addCommand(new HelpCommand(commandServiceBuilder.build()));
		commandService.addCommand(new CommandsCommand(commandServiceBuilder.build()));
		
		//ColorService, ConfigService
		commandServiceBuilder.removeService(ServiceType.COMMAND);
		commandServiceBuilder.addService(ServiceType.CONFIG);
		commandService.addCommand(new AboutCommand(commandServiceBuilder.build()));
		
		//ColorService, PokeFlexService, PerkService
		commandServiceBuilder.removeService(ServiceType.CONFIG);
		commandServiceBuilder.addService(ServiceType.POKE_FLEX);
		commandServiceBuilder.addService(ServiceType.PERK);
		commandService.addCommand(new AbilityCommand(commandServiceBuilder.build(), new AbilityResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new RandpokeCommand(commandServiceBuilder.build(), new RandpokeResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new SetCommand(commandServiceBuilder.build(), new SetResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new ShinyCommand(commandServiceBuilder.build(), new ShinyResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new StatsCommand(commandServiceBuilder.build(), new StatsResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new NatureCommand(commandServiceBuilder.build(), new NatureResponseFormatter(serviceBuilderColor.build())));
		
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
		
		//ColorService, PokeFlexService, PerkService, TTSService
		serviceBuilderColor.addService(ServiceType.TTS);
		commandService.addCommand(new DexCommand(commandServiceBuilder.build(), new DexResponseFormatter(serviceBuilderColor.build())));
	}
	
	private static Publisher setUpPublisher(PokedexApplicationManager manager, int shardToManage, int totalShards) throws ServiceException, ServiceConsumerException
	{
		ServiceManager discordServiceManager = ServiceManager.ServiceManagerBuilder.newInstance(manager).addService(ServiceType.DISCORD).build();
		ServiceManager configServiceManager = ServiceManager.ServiceManagerBuilder.newInstance(manager).addService(ServiceType.CONFIG).build();
		
		Publisher publisher = Publisher.newBuilder()
				.addServices(discordServiceManager)
				.setShard(shardToManage)
				.setTotalShards(totalShards)
				.setExecutor(Executors.newSingleThreadScheduledExecutor())
				.addRecipient(new CarbonitexRecipient(configServiceManager))
				.addRecipient(new DiscordBotsRecipient(configServiceManager))
				.addRecipient(new BotsDiscordRecipient(configServiceManager))
				.build();
		
		return publisher;
	}
	
}
