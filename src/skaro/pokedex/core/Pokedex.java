package skaro.pokedex.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.patreon.PatreonAPI;

import skaro.pokedex.communicator.Publisher;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorService;
import skaro.pokedex.data_processor.CommandMap;
import skaro.pokedex.data_processor.EmojiService;
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
import skaro.pokedex.data_processor.commands.PatreonCommand;
import skaro.pokedex.data_processor.commands.RandpokeCommand;
import skaro.pokedex.data_processor.commands.SetCommand;
import skaro.pokedex.data_processor.commands.ShinyCommand;
import skaro.pokedex.data_processor.commands.StatsCommand;
import skaro.pokedex.data_processor.commands.WeakCommand;
import skaro.pokeflex.api.PokeFlexFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class Pokedex 
{	
	public static void main(String[] args) throws Exception
	{
		int shardIDToManage = -1;
		int totalShards = -1;
		
		Optional<String> discordToken, patreonAccessToken;
		ConfigurationService configurator;
		Publisher publisher;
		
		CommandMap library;
		PreLoginEventHandler pleh;
		PerkChecker checker;
		
		IDiscordClient discordClient;
		PatreonAPI patreonClient;
		
		ScheduledExecutorService pokedexThreadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 6);
		PokeFlexFactory factory;
		
		//Parse command line arguments
		if(args.length != 2)
		{
			System.out.println("Usage: <shard ID> <total shards>");
			System.exit(1);
		}
		
		//Record on command line arguments
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
		configurator = ConfigurationService.initializeConfigurator(true);
		
		//Set logging level
		Logger logger4j = org.apache.log4j.Logger.getRootLogger();
		logger4j.setLevel(Level.toLevel(configurator.getDebugLevel()));
			
		/**
		 * Patreon SETUP
		 */
		System.out.println("[Pokedex main] Establishing Patreon client");
		patreonAccessToken = configurator.getConfigData("access_token", "patreon");
		
		if(!patreonAccessToken.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Patreon authentication.");
			return;
		}
		
		patreonClient = new PatreonAPI(patreonAccessToken.get());
		checker = new PerkChecker(patreonClient, pokedexThreadPool);
		
		/**
		 * PUBLISHER SETUP
		 */
		System.out.println("[Pokedex main] Setting up Publisher");
		publisher = new Publisher(shardIDToManage, totalShards, pokedexThreadPool);
		
		/**
		 * DISCORD SETUP
		 */
		//Initialize resources
		System.out.println("[Pokedex main] Establishing Discord client");
		factory = new PokeFlexFactory(configurator.getPokeFlexURL(), pokedexThreadPool);
		library = initCompleteLibrary(factory, checker, pokedexThreadPool);
		pleh = new PreLoginEventHandler(library, publisher, pokedexThreadPool);
		discordToken = configurator.getAuthToken("discord");
		discordClient = initClient(discordToken, shardIDToManage, totalShards);
		checker.setDiscordClient(discordClient);
		
		/**
		 * INTERNAL SETUP
		 */
		TypeData.initialize(factory);
		LearnMethodData.initialize(factory);
		ColorService.initialize();
		EmojiService.initialize();
		
		//Login to Discord
		System.out.println("[Pokedex main] Logging into Discord");
		discordClient.getDispatcher().registerListener(pokedexThreadPool, pleh);
		discordClient.login();
	}
	
	private static IDiscordClient initClient(Optional<String> discordToken, int shardID, int totalShards)
	{
		if(!discordToken.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Discord application.");
			System.exit(1);
		}
		
		IDiscordClient idc = new ClientBuilder()
				.setMaxMessageCacheCount(5)
				.setMaxReconnectAttempts(5)
				.withToken(discordToken.get())
				.setShard(shardID, totalShards)
				.build();
		
		return idc;
	}
	
	/**
	 * A helper function to initialize the command library with all commands.
	 * This is used for the InputProcessor. Any commands not included here will 
	 * not be recognized by the input processor, and therefore will not be
	 * recognized by any command map.
	 * @return a CommandLibrary of AbstractCommands that are supported for Discord
	 */
	private static CommandMap initCompleteLibrary(PokeFlexFactory factory, PerkChecker checker, ExecutorService service)
	{
		List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
		
		commands.add(new RandpokeCommand(factory, checker));
		commands.add(new StatsCommand(factory, checker));
		commands.add(new DataCommand(factory, checker));
		commands.add(new AbilityCommand(factory, checker));
		commands.add(new ItemCommand(factory, checker));
		commands.add(new MoveCommand(factory, checker));
		commands.add(new LearnCommand(factory, checker));
		commands.add(new WeakCommand(factory, checker));
		commands.add(new CoverageCommand(factory, checker));
		commands.add(new DexCommand(factory, checker));
		commands.add(new SetCommand(factory, checker));
		//commands.add(new LocationCommand(factory, checker)); //Not supported by personal deployment of PokeAPI anymore
		commands.add(new AboutCommand());
		commands.add(new PatreonCommand());
		commands.add(new InviteCommand());
		commands.add(new ShinyCommand(factory, checker));
		
		commands.add(new HelpCommand(commands));
		commands.add(new CommandsCommand(commands));
		
		CommandMap result = new CommandMap(commands, service);
		
		return result;
	}
}
