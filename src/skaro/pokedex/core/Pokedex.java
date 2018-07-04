package skaro.pokedex.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.patreon.PatreonAPI;

import skaro.pokedex.data_processor.DiscordCommandMap;
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
import skaro.pokedex.input_processor.InputProcessor;
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
		
		CommandLibrary library;
		InputProcessor ip;
		DiscordCommandMap dcm;
		DiscordEventHandler deh;
		
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
		
		/**
		 * DISCORD SETUP
		 */
		//Log into Discord
		System.out.println("[Pokedex main] Establishing Discord client");
		discordToken = configurator.getAuthToken("discord");
		discordClient = initClient(discordToken, shardIDToManage, totalShards);
		
		//Initialize other resources
		library = initCompleteLibrary(new PokeFlexFactory(configurator.getPokeFlexURL()), patreonClient);
		ip = new InputProcessor(library);
		dcm = new DiscordCommandMap(library);
		deh = new DiscordEventHandler(discordClient, dcm, ip);
		discordClient.getDispatcher().registerListener(deh);
		
		//Login to Discord
		System.out.println("[Pokedex main] Logging into Discord");
		discordClient.login();
		
		/**
		 * CARBONITEX SETUP
		 * Only the process in charge of the 0th shard should send data to Carbonitex
		 */
		if(shardIDToManage == 0)
		{
			System.out.println("[Pokedex main] Setting up and scheduling Carbonitex timer");
			Optional<String> carbonToken = configurator.getAuthToken("carbonitex");
			carbonitexLogin(carbonToken, discordClient);
		}
		
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
	
	private static void carbonitexLogin(Optional<String> carbonToken, IDiscordClient discordClient)
	{
		if(!carbonToken.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Carbonitex Communication.");
			return;
		}
		
		//Create timer and task
    	Timer carbonTimer = new Timer(true);
		TimerTask carbonTask = createCarbonTask(carbonToken.get(), discordClient);
        
        //Schedule task for every hour, starting in one hour
        carbonTimer.scheduleAtFixedRate(carbonTask, 1 * 60 * 60 * 1000, 1 * 60 * 60 * 1000); //1 hour
	}
	
	private static TimerTask createCarbonTask(String carbonKey, IDiscordClient discordClient)
	{
		TimerTask task = new TimerTask() 
		{
            @Override
            public void run() 
            { 
            	//Utility variables
            	HttpClient httpclient = HttpClients.createDefault();
        		HttpPost httppost = new HttpPost("https://www.carbonitex.net/discord/data/botdata.php/");
        		HttpResponse response;
        		HttpEntity entity;
        		InputStream instream;
        		BufferedReader reader;
        		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        		
        		System.out.println("[Pokedex main] Sending POST request to Carbonitex...");
            	try 
            	{
            		// Request parameters and other properties.
            		params.add(new BasicNameValuePair("key", carbonKey));
            		params.add(new BasicNameValuePair("servercount", Integer.toString(12 * discordClient.getGuilds().size())));
            		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            		response = httpclient.execute(httppost);
					params.remove(1);
					
					entity = response.getEntity();

					if(entity == null) 
					{
						System.out.println("[Pokedex main] No response recieved.");
						return;
					}
					
					instream = entity.getContent();
				    reader = new BufferedReader(new InputStreamReader(instream));
				    
				    StringBuilder result = new StringBuilder();
				    String line;
				    while((line = reader.readLine()) != null) 
				    {
				        result.append(line);
				    }
				    reader.close();
				    instream.close();
				    
				    System.out.println("[Pokedex main] HTTP post:"+httppost.toString());
				    System.out.println("[Pokedex main] HTTP response:"+result.toString());
					
				}
            	catch(Exception e) { System.err.println("[Pokedex main] Some error occured."); }
            }
        };
        
        return task;
	}
	
	/**
	 * A helper function to initialize the command library with all commands.
	 * This is used for the InputProcessor. Any commands not included here will 
	 * not be recognized by the input processor, and therefore will not be
	 * recognized by any command map.
	 * @return a CommandLibrary of ICommands that are supported for Discord
	 */
	private static CommandLibrary initCompleteLibrary(PokeFlexFactory factory, PatreonAPI patreonClient)
	{
		CommandLibrary lib = new CommandLibrary();
		PrivilegeChecker checker = new PrivilegeChecker(patreonClient);
		
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
