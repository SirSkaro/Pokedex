package skaro.pokedex.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import com.patreon.PatreonAPI;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.object.presence.Presence;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorService;
import skaro.pokedex.data_processor.CommandMap;
import skaro.pokedex.data_processor.EmojiService;
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
import skaro.pokedex.data_processor.commands.PatreonCommand;
import skaro.pokedex.data_processor.commands.RandpokeCommand;
import skaro.pokedex.data_processor.commands.SetCommand;
import skaro.pokedex.data_processor.commands.ShinyCommand;
import skaro.pokedex.data_processor.commands.StatsCommand;
import skaro.pokedex.data_processor.commands.WeakCommand;

public enum PokedexManager implements IServiceManager
{
	INSTANCE;
	
	private Map<ServiceType, IService> services;	
	private boolean initialized = false;
	
	public IService getService(ServiceType service) throws ServiceException
	{
		if(!services.containsKey(service))
			throw new ServiceException("Service not supported or included");
		
		return services.get(service);
	}
	
	private void build(PokedexConfigurator builder)
	{
		if(initialized)
			throw new IllegalStateException("Pokedex application already configued!");
		
		builder.services.put(ServiceType.CONFIG, builder.configurationService);
		this.services = builder.services;
		initialized = true;
	}
	
	public static class PokedexConfigurator 
	{
		private Map<ServiceType, IService> services;
		
		private ConfigurationService configurationService;
		private ScheduledExecutorService threadPool;
		
		public static PokedexConfigurator newInstance(ConfigurationType configType, ScheduledExecutorService threadPool) 
		{ 
			PokedexConfigurator builder = new PokedexConfigurator(); 
			builder.threadPool = threadPool;
			builder.configurationService = ConfigurationService.initialize(configType);
			builder.services = new HashMap<>();
			
			return builder;
		}
		
		public PokedexManager configure()
		{
			INSTANCE.build(this);
			return INSTANCE;
		}
		
		public PokedexConfigurator buildCommandMap()
		{
			List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
			
			commands.add(new RandpokeCommand());
			commands.add(new StatsCommand());
			commands.add(new DataCommand());
			commands.add(new AbilityCommand());
			commands.add(new ItemCommand());
			commands.add(new MoveCommand());
			commands.add(new LearnCommand());
			commands.add(new WeakCommand());
			commands.add(new CoverageCommand());
			commands.add(new DexCommand());
			commands.add(new SetCommand());
			commands.add(new AboutCommand());
			commands.add(new PatreonCommand());
			commands.add(new InviteCommand());
			commands.add(new ShinyCommand());
			
			commands.add(new HelpCommand(commands));
			commands.add(new CommandsCommand(commands));
			
			CommandMap commandMap = new CommandMap(commands, this.threadPool);
			services.put(ServiceType.COMMAND, commandMap);
			return this;
		}
		
		public PokedexConfigurator buildColorService()
		{
			ColorService colorService = new ColorService();
			services.put(ServiceType.COLOR, colorService);
			
			return this;
		}
		
		public PokedexConfigurator buildEmojiService()
		{
			EmojiService emojiService = new EmojiService();
			services.put(ServiceType.EMOJI, emojiService);
			
			return this;
		}
		
		public PokedexConfigurator buildDiscordClient(int shardCount, int shardID)
		{
			Optional<String> discordToken = configurationService.getAuthToken("discord");
			DiscordClient discordClient = new DiscordClientBuilder(discordToken.get())
								.setShardCount(shardCount)
								.setShardIndex(shardID)
								.setInitialPresence(Presence.online())
								.build();
			DiscordService service = new DiscordService(discordClient);
			services.put(ServiceType.DISCORD, service);
			
			return this;
		}
		
		public PokedexConfigurator buildPatreonClient()
		{
			Optional<String> patreonAccessToken = configurationService.getConfigData("access_token", "patreon");
			PatreonAPI patreonClient = new PatreonAPI(patreonAccessToken.get());
			PerkChecker perkService = new PerkChecker(patreonClient, this.threadPool);
			services.put(ServiceType.PERK, perkService);
			
			return this;
		}
		
		public PokedexConfigurator initPokeFlexFactory()
		{
			PokeFlexService factory = new PokeFlexService(configurationService.getPokeFlexURL(), this.threadPool);
			services.put(ServiceType.POKE_FLEX, factory);
			return this;
		}
	}
}
