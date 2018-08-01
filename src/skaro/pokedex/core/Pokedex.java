package skaro.pokedex.core;

import java.util.Optional;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.patreon.PatreonAPI;

import skaro.pokedex.communicator.Publisher;
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
import skaro.pokedex.data_processor.commands.LocationCommand;
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
		Configurator configurator;
		Publisher publisher;
		
		CommandLibrary library;
		PreLoginEventHandler pleh;
		PrivilegeChecker checker;
		
		IDiscordClient discordClient;
		PatreonAPI patreonClient;
		
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
		configurator = Configurator.initializeConfigurator(true);
		
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
		checker = new PrivilegeChecker(patreonClient);
		
		/**
		 * PUBLISHER SETUP
		 */
		System.out.println("[Pokedex main] Setting up Publisher");
		publisher = new Publisher(shardIDToManage, totalShards);
		
		/**
		 * DISCORD SETUP
		 */
		//Initialize resources
		System.out.println("[Pokedex main] Establishing Discord client");
		library = initCompleteLibrary(new PokeFlexFactory(configurator.getPokeFlexURL()), checker);
		pleh = new PreLoginEventHandler(library, publisher);
		discordToken = configurator.getAuthToken("discord");
		discordClient = initClient(discordToken, shardIDToManage, totalShards);
		
		//Login to Discord
		System.out.println("[Pokedex main] Logging into Discord");
		discordClient.getDispatcher().registerListener(pleh);
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
	 * @return a CommandLibrary of ICommands that are supported for Discord
	 */
	private static CommandLibrary initCompleteLibrary(PokeFlexFactory factory, PrivilegeChecker checker)
	{
		CommandLibrary lib = new CommandLibrary();
		
		lib.addToLibrary(new RandpokeCommand(factory));
		lib.addToLibrary(new StatsCommand(factory));
		lib.addToLibrary(new DataCommand(factory));
		lib.addToLibrary(new AbilityCommand(factory));
		lib.addToLibrary(new ItemCommand(factory));
		lib.addToLibrary(new MoveCommand(factory));
		lib.addToLibrary(new LearnCommand(factory));
		lib.addToLibrary(new WeakCommand(factory));
		lib.addToLibrary(new CoverageCommand(factory));
		lib.addToLibrary(new DexCommand(factory));
		lib.addToLibrary(new SetCommand(factory));
		lib.addToLibrary(new LocationCommand(factory));
		lib.addToLibrary(new AboutCommand());
		lib.addToLibrary(new HelpCommand());
		lib.addToLibrary(new PatreonCommand());
		lib.addToLibrary(new InviteCommand());
		lib.addToLibrary(new ShinyCommand(factory, checker));
		
		lib.addToLibrary(new CommandsCommand(lib.getLibrary()));
		
		return lib;
	}
}
