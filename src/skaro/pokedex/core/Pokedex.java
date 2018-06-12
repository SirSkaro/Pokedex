package skaro.pokedex.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import skaro.pokedex.data_processor.DiscordCommandMap;
import skaro.pokedex.data_processor.commands.AbilityCommand;
import skaro.pokedex.data_processor.commands.AboutCommand;
import skaro.pokedex.data_processor.commands.CommandsCommand;
import skaro.pokedex.data_processor.commands.CoverageCommand;
import skaro.pokedex.data_processor.commands.DataCommand;
import skaro.pokedex.data_processor.commands.DexCommand;
import skaro.pokedex.data_processor.commands.DonateCommand;
import skaro.pokedex.data_processor.commands.HelpCommand;
import skaro.pokedex.data_processor.commands.InviteCommand;
import skaro.pokedex.data_processor.commands.ItemCommand;
import skaro.pokedex.data_processor.commands.LearnCommand;
import skaro.pokedex.data_processor.commands.LocationCommand;
import skaro.pokedex.data_processor.commands.MoveCommand;
import skaro.pokedex.data_processor.commands.RandpokeCommand;
import skaro.pokedex.data_processor.commands.SetCommand;
import skaro.pokedex.data_processor.commands.StatsCommand;
import skaro.pokedex.data_processor.commands.WeakCommand;
import skaro.pokedex.input_processor.InputProcessor;
import skaro.pokeflex.api.PokeFlexFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class Pokedex 
{	
	public static void main(String[] args) throws Exception
	{
		int shardIDToManage = -1;
		int totalShards = -1;
		CommandLibrary library;
		
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
		Configurator configurator = Configurator.initializeConfigurator(false);
		
		//Initialize CommandMaps
		System.out.println("[Pokedex main] Initializing resources...");
		library = getCompleteLibrary(new PokeFlexFactory("http://127.0.0.1:5000"));
		InputProcessor ip = new InputProcessor(library);
		
		/**
		 * DISCORD SETUP
		 */
		//Log into Discord and establish a listener
		System.out.println("[Pokedex main] Logging into Discord");
		Optional<String> discordToken = configurator.getAuthToken("discord");
		Optional<IDiscordClient> discordClient = discordLogin(discordToken, library, ip, shardIDToManage, totalShards);
		
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
	
	private static Optional<IDiscordClient> discordLogin(Optional<String> discordToken, CommandLibrary library, InputProcessor ip, int shardID, int totalShards)
	{
		if(!discordToken.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Discord application.");
			return Optional.empty();
		}
		
		IDiscordClient discordClient = getClient(discordToken.get(), shardID, totalShards);
		DiscordCommandMap dcm = new DiscordCommandMap(library);
		DiscordEventHandler deh = new DiscordEventHandler(discordClient, dcm, ip);
		discordClient.getDispatcher().registerListener(deh);
		discordClient.login();
		
		return Optional.of(discordClient);
	}
	
	private static IDiscordClient getClient(String token, int shardID, int totalShards) throws DiscordException
    {
		IDiscordClient idc = new ClientBuilder()
				.setMaxMessageCacheCount(5)
				.setMaxReconnectAttempts(5)
				.withToken(token)
				.setShard(shardID, totalShards)
				.build();
		
        return idc;
    }
	
	private static void carbonitexLogin(Optional<String> carbonToken, Optional<IDiscordClient> discordClient)
	{
		if(!carbonToken.isPresent() || !discordClient.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Carbonitex Communication.");
			return;
		}
		
		//Create timer and task
    	Timer carbonTimer = new Timer(true);
		TimerTask carbonTask = createCarbonTask(carbonToken.get(), discordClient.get());
        
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
            		params.add(new BasicNameValuePair("servercount", Integer.toString(discordClient.getGuilds().size())));
            		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            		response = httpclient.execute(httppost);
					params.remove(1);
					
					entity = response.getEntity();

					if(entity != null) 
					{
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
					else
					{
						System.out.println("[Pokedex main] No response recieved.");
					}
					
					System.out.println("[Pokedex main] done");
				}
            	catch(ClientProtocolException e)
            	{
            		System.err.println("[Pokedex main] Some HTTP error occured.");
					e.printStackTrace();
				}
            	catch (IOException e)
            	{
            		System.err.println("[Pokedex main] Some I/O HTTP error occured.");
					e.printStackTrace();
				}
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
	private static CommandLibrary getCompleteLibrary(PokeFlexFactory factory)
	{
		CommandLibrary lib = new CommandLibrary();
		
		lib.addToLibrary(RandpokeCommand.getInstance(factory));
		lib.addToLibrary(StatsCommand.getInstance(factory));
		lib.addToLibrary(DataCommand.getInstance(factory));
		lib.addToLibrary(AbilityCommand.getInstance(factory));
		lib.addToLibrary(ItemCommand.getInstance(factory));
		lib.addToLibrary(MoveCommand.getInstance(factory));
		lib.addToLibrary(LearnCommand.getInstance(factory));
		lib.addToLibrary(WeakCommand.getInstance(factory));
		lib.addToLibrary(CoverageCommand.getInstance(factory));
		lib.addToLibrary(DexCommand.getInstance(factory));
		lib.addToLibrary(SetCommand.getInstance(factory));
		lib.addToLibrary(LocationCommand.getInstance(factory));
		lib.addToLibrary(AboutCommand.getInstance());
		lib.addToLibrary(HelpCommand.getInstance());
		lib.addToLibrary(DonateCommand.getInstance());
		lib.addToLibrary(InviteCommand.getInstance());
		//lib.addToLibrary(ShinyCommand.getInstance(factory));
		
		lib.addToLibrary(CommandsCommand.getInstance(lib.getLibrary()));
		
		return lib;
	}
}
