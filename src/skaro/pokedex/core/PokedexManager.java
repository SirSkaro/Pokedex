package skaro.pokedex.core;

import java.util.ArrayList;
import java.util.List;
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
import skaro.pokeflex.api.PokeFlexFactory;

public enum PokedexManager 
{
	INSTANCE;
	
	private ColorService colorService;
	private EmojiService emojiService;
	private PokeFlexFactory pokeFlexService;
	private PerkChecker perkService;
	private DiscordClient discordService;
	private ConfigurationService configurationService;
	private CommandMap commandService;
	
	private boolean initialized = false;
	
	private void build(PokedexConfigurator builder)
	{
		if(initialized)
			throw new IllegalStateException("Pokedex application already configued!");
		
		pokeFlexService = builder.factory;
		perkService = builder.perkService;
		discordService = builder.discordClient;
		configurationService = builder.configurationService;
		commandService = builder.commandMap;
		colorService.initialize();
		emojiService.initialize();
		initialized = true;
	}
	
	public PokeFlexFactory PokeFlexService() { return pokeFlexService; }
	public ConfigurationService ConfigurationService() { return configurationService; }
	public DiscordClient DiscordService() { return discordService; }
	public PerkChecker PerkService() { return perkService; }
	public ColorService ColorService() { return colorService; }
	public EmojiService EmojiService() { return emojiService; }
	public CommandMap CommandService() { return commandService; }
	
	public static class PokedexConfigurator 
	{
		private ConfigurationService configurationService;
		private DiscordClient discordClient;
		private PerkChecker perkService;
		private CommandMap commandMap;
		private ScheduledExecutorService threadPool;
		private PokeFlexFactory factory;
		
		public static PokedexConfigurator newInstance(ConfigurationType configType, ScheduledExecutorService threadPool) 
		{ 
			PokedexConfigurator builder = new PokedexConfigurator(); 
			builder.threadPool = threadPool;
			builder.configurationService = ConfigurationService.initialize(configType);
			
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
			
			this.commandMap = new CommandMap(commands, this.threadPool);
			return this;
		}
		
		public PokedexConfigurator buildDiscordClient(int shardCount, int shardID)
		{
			Optional<String> discordToken = configurationService.getAuthToken("discord");
			discordClient = new DiscordClientBuilder(discordToken.get())
								.setShardCount(shardCount)
								.setShardIndex(shardID)
								.setInitialPresence(Presence.online())
								.build();
			
			return this;
		}
		
		public PokedexConfigurator buildPatreonClient()
		{
			Optional<String> patreonAccessToken = configurationService.getConfigData("access_token", "patreon");
			PatreonAPI patreonClient = new PatreonAPI(patreonAccessToken.get());
			perkService = new PerkChecker(patreonClient, this.threadPool);
			
			return this;
		}
		
		public PokedexConfigurator initPokeFlexFactory()
		{
			factory = new PokeFlexFactory(configurationService.getPokeFlexURL(), this.threadPool);
			return this;
		}
	}
}
