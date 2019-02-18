package skaro.pokedex.core;

import java.time.Duration;
import java.util.Optional;

import com.patreon.PatreonAPI;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.presence.Presence;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import skaro.pokedex.data_processor.ChannelRateLimiter;
import skaro.pokedex.data_processor.LearnMethodData;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.commands.*;
import skaro.pokedex.data_processor.formatters.*;
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
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceException;
import skaro.pokedex.services.ServiceManager;
import skaro.pokedex.services.ServiceManager.ServiceManagerBuilder;
import skaro.pokedex.services.ServiceType;
import skaro.pokedex.services.TextToSpeechService;
import skaro.pokedex.services.TypeService;

public class PokedexV3 
{
	public static void main(String[] args) throws Exception
	{
		//Parse command line arguments
		if(args.length != 2)
		{
			System.out.println("Usage: <shard ID> <total shards>");
			System.exit(1);
		}
		
		//Record on command line arguments
		int shardIDToManage = -1;
		int totalShards = -1;
		try
		{ 
			shardIDToManage = Integer.parseInt(args[0]);
			totalShards = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e)
		{
			System.out.println("[Pokedex main] Error parsing command line arguments.");
			System.exit(1);
		}
		
		
		//Load configurations
		System.out.println("[Pokedex main] Loading configurations...");
		
		ConfigurationService configurationService = ConfigurationService.initialize(ConfigurationType.DEVELOP);
		Scheduler scheduler = Schedulers.newParallel("pokedex_pool", Runtime.getRuntime().availableProcessors() * 6);
		CommandService commandMap = new CommandService();
		PerkService perkService = createPatreonService(configurationService);
		PokeFlexService pokeFlexService = createPokeFlexService(configurationService, scheduler);
		FlexCacheService flexCacheService = createCacheService(pokeFlexService);
		TypeService typeService = new TypeService();
		
		PokedexApplicationManager manager = PokedexApplicationManager.PokedexConfigurator.newInstance()
								.withService(configurationService)
								.withService(commandMap)
								.withService(createDiscordService(configurationService, scheduler, shardIDToManage, totalShards))
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
		DiscordService service = (DiscordService)manager.getService(ServiceType.DISCORD);
		DiscordClient client = service.getV3Client();
		InputProcessor inputProcessor = new InputProcessor(commandMap, 190670386239635456L);
		ChannelRateLimiter rateLimiter = new ChannelRateLimiter(2, Duration.ofSeconds(10));
		DiscordMessageEventHandler messageHandler = new DiscordMessageEventHandler(inputProcessor, rateLimiter);
		
		client.getEventDispatcher().on(MessageCreateEvent.class)
			.flatMap(event -> messageHandler.onMessageCreateEvent(event))
			.onErrorContinue((t,o) -> System.out.println("saved the message create event"))
	        .subscribe(value -> System.out.println("success"), error -> error.printStackTrace());
		
		client.getEventDispatcher().on(MessageUpdateEvent.class)
			.flatMap(event -> messageHandler.onMessageEditEvent(event))
			.onErrorContinue((t,o) -> System.out.println("saved the message update event"))
	        .subscribe(value -> System.out.println("success"), error -> error.printStackTrace());

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
		return new PerkService(patreonClient);
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
		commandService.addCommand(new SetCommand(commandServiceBuilder.build()));
		commandService.addCommand(new ShinyCommand(commandServiceBuilder.build(), new ShinyResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new StatsCommand(commandServiceBuilder.build(), new StatsResponseFormatter(serviceBuilderColor.build())));
		
		//ColorService, PokeFlexService, PerkService, CacheService
		commandServiceBuilder.addService(ServiceType.CACHE);
		commandService.addCommand(new ItemCommand(commandServiceBuilder.build(), new ItemResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new LearnCommand(commandServiceBuilder.build(), new LearnResponseFormatter(serviceBuilderColor.build())));
		commandService.addCommand(new MoveCommand(commandServiceBuilder.build(), new MoveResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new DataCommand(commandServiceBuilder.build(), new DataResponseFormatter(serviceBuilderEmoji.build())));
		
		//ColorService, PokeFlexService, PerkService, TypeService
		commandServiceBuilder.removeService(ServiceType.CACHE);
		commandServiceBuilder.addService(ServiceType.TYPE);
		commandService.addCommand(new WeakCommand(commandServiceBuilder.build(), new WeakResponseFormatter(serviceBuilderEmoji.build())));
		commandService.addCommand(new CoverageCommand(commandServiceBuilder.build(), new CoverageResponseFormatter(serviceBuilderEmoji.build())));
		
		//ColorService, PokeFlexService, PerkService, TTSService
		serviceBuilderColor.addService(ServiceType.TTS);
		commandService.addCommand(new DexCommand(commandServiceBuilder.build(), new DexResponseFormatter(serviceBuilderColor.build())));
	}
	
}
