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
import org.jibble.pircbot.IrcException;

import skaro.pokedex.data_processor.DiscordCommandMap;
import skaro.pokedex.data_processor.TwitchCommandMap;
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
		//Load configurations
		System.out.println("[Pokedex main] Loading configurations...");
		Configurator configurator = Configurator.initializeConfigurator(true);
		
		//Initialize CommandMaps
		System.out.println("[Pokedex main] Initializing resources...");
		InputProcessor ip = new InputProcessor(createCompleteLibrary(new PokeFlexFactory("http://localhost:5000")));
		
		/**
		 * TWITCH SETUP
		 */
		System.out.println("[Pokedex main] Logging to the Twitch Chat system.");
		Optional<String> twitchToken = configurator.getAuthToken("twitch");
		Optional<String> twitchUsername = configurator.getUsername("twitch");
		twitchLogin(twitchToken, twitchUsername, ip);
		
		/**
		 * DISCORD SETUP
		 */
		//Log into Discord and establish a listener
		System.out.println("[Pokedex main] Logging into Discord");
		Optional<String> discordToken = configurator.getAuthToken("discord");
		Optional<IDiscordClient> discordClient = discordLogin(discordToken, ip);
		
		/**
		 * CARBONITEX SETUP
		 */
		System.out.println("[Pokedex main] Setting up and scheduling Carbonitex timer");
		Optional<String> carbonToken = configurator.getAuthToken("carbonitex");
		carbonitexLogin(carbonToken, discordClient);
	}
	
	private static IDiscordClient getClient(String token) throws DiscordException
    {
		IDiscordClient idc = new ClientBuilder()
				.setMaxMessageCacheCount(50)
				.withToken(token)
				.withRecommendedShardCount()
				.login();
		
        return idc;
    }
	
	private static Optional<IDiscordClient> discordLogin(Optional<String> discordToken, InputProcessor ip)
	{
		if(!discordToken.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Discord application.");
			return Optional.empty();
		}
		
		
		IDiscordClient discordClient = getClient(discordToken.get());
		DiscordCommandMap dcm = new DiscordCommandMap(createDiscordLibrary());
		DiscordEventHandler deh = new DiscordEventHandler(discordClient, dcm, ip);
		discordClient.getDispatcher().registerListener(deh);
		
		return Optional.of(discordClient);
	}
	
	private static void twitchLogin(Optional<String> twitchToken, Optional<String> twitchUsername, InputProcessor ip)
	{
		if(!twitchToken.isPresent() || !twitchUsername.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Twitch application.");
			return;
		}
		
		TwitchClient twitchClient = new TwitchClient(twitchToken.get(), twitchUsername.get());
		twitchClient.assignCommandMap(new TwitchCommandMap(createTwitchLibrary()));
		twitchClient.assignInputProcessor(ip);
		try { twitchClient.connectToChannels(); } 
		catch (IOException | IrcException e) 
		{
			System.out.println("[Pokedex main] Could not log into Twitch with error: "+e.getMessage());
		}
	}
	
	private static void carbonitexLogin(Optional<String> carbonToken, Optional<IDiscordClient> discordClient)
	{
		if(!carbonToken.isPresent() || !discordClient.isPresent())
		{
			System.out.println("[Pokedex main] No configuration data found for Carbonitex Communication.");
			return;
		}
		
		//Create timer and task
    	Timer carbonTimer = new Timer();
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
	 * A helper function to initialize the command library for Discord services
	 * @return a CommandLibrary of ICommands that are supported for Discord
	 */
	private static CommandLibrary createDiscordLibrary()
	{
		CommandLibrary lib = new CommandLibrary();
		
		lib.addToLibrary(RandpokeCommand.getInstance());
		lib.addToLibrary(StatsCommand.getInstance(null));
		lib.addToLibrary(DataCommand.getInstance(null));
		lib.addToLibrary(AbilityCommand.getInstance(null));
		lib.addToLibrary(ItemCommand.getInstance());
		lib.addToLibrary(MoveCommand.getInstance());
		lib.addToLibrary(LearnCommand.getInstance());
		lib.addToLibrary(WeakCommand.getInstance(null));
		lib.addToLibrary(CoverageCommand.getInstance(null));
		lib.addToLibrary(DexCommand.getInstance(null));
		lib.addToLibrary(SetCommand.getInstance());
		lib.addToLibrary(LocationCommand.getInstance());
		lib.addToLibrary(AboutCommand.getInstance());
		lib.addToLibrary(HelpCommand.getInstance());
		lib.addToLibrary(DonateCommand.getInstance());
		lib.addToLibrary(InviteCommand.getInstance());
		
		lib.addToLibrary(CommandsCommand.getInstance(lib.getLibrary()));
		
		return lib;
	}
	
	/**
	 * A helper function to initialize the command library for Twitch services
	 * @return a CommandLibrary of ICommands that are supported for Twitch
	 */
	private static CommandLibrary createTwitchLibrary()
	{
		CommandLibrary lib = new CommandLibrary();
		
		lib.addToLibrary(RandpokeCommand.getInstance());
		lib.addToLibrary(StatsCommand.getInstance(null));
		lib.addToLibrary(DataCommand.getInstance(null));
		lib.addToLibrary(AbilityCommand.getInstance(null));
		lib.addToLibrary(ItemCommand.getInstance());
		lib.addToLibrary(MoveCommand.getInstance());
		lib.addToLibrary(LearnCommand.getInstance());
		lib.addToLibrary(WeakCommand.getInstance(null));
		lib.addToLibrary(CoverageCommand.getInstance(null));
		lib.addToLibrary(DexCommand.getInstance(null));
		lib.addToLibrary(AboutCommand.getInstance());
		lib.addToLibrary(HelpCommand.getInstance());
		
		lib.addToLibrary(CommandsCommand.getInstance(lib.getLibrary()));
		
		return lib;
	}
	
	/**
	 * A helper function to initialize the command library with all commands.
	 * This is used for the InputProcessor. Any commands not included here will 
	 * not be recognized by the input processor, and therefore will not be
	 * recognized by any command map.
	 * @return a CommandLibrary of ICommands that are supported for Discord
	 */
	private static CommandLibrary createCompleteLibrary(PokeFlexFactory factory)
	{
		CommandLibrary lib = new CommandLibrary();
		
		lib.addToLibrary(RandpokeCommand.getInstance());
		lib.addToLibrary(StatsCommand.getInstance(factory));
		lib.addToLibrary(DataCommand.getInstance(factory));
		lib.addToLibrary(AbilityCommand.getInstance(factory));
		lib.addToLibrary(ItemCommand.getInstance());
		lib.addToLibrary(MoveCommand.getInstance());
		lib.addToLibrary(LearnCommand.getInstance());
		lib.addToLibrary(WeakCommand.getInstance(factory));
		lib.addToLibrary(CoverageCommand.getInstance(factory));
		lib.addToLibrary(DexCommand.getInstance(factory));
		lib.addToLibrary(SetCommand.getInstance());
		lib.addToLibrary(LocationCommand.getInstance());
		lib.addToLibrary(AboutCommand.getInstance());
		lib.addToLibrary(HelpCommand.getInstance());
		lib.addToLibrary(DonateCommand.getInstance());
		lib.addToLibrary(InviteCommand.getInstance());
		
		lib.addToLibrary(CommandsCommand.getInstance(lib.getLibrary()));
		
		return lib;
	}
	
}
