package skaro.pokedex.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import skaro.pokedex.data_processor.DiscordCommandMap;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.InputProcessor;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.IShard;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.shard.ShardReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;
import sx.blah.discord.util.audio.events.TrackFinishEvent;

public class DiscordEventHandler
{
	private IDiscordClient discordClient; 		//access to DiscordClient object
	private DiscordCommandMap commandMap;		//Contains the 'cache' of commands
	private Timer statusTimer;					//Timers for special purposes
	private TimerTask statusTask;				//tasks for timers
	private int statusIndex;				//count for booting tracking, statusIndex to iterate through status messages
	private ArrayList<String> statusMessages;	//all status messages to be displayed
	private InputProcessor processor;

	public DiscordEventHandler(IDiscordClient dc, DiscordCommandMap cm, InputProcessor ip)
	{
		discordClient = dc;
		commandMap = cm;
		statusIndex = 0;
		processor = ip;
		
		statusMessages = new ArrayList<String>();
		statusMessages.add("!commands/!help");
		statusMessages.add("%commands/%help");
		statusMessages.add("commands()/help()");
		statusMessages.add("%invite");
        
        statusTimer = new Timer(true);
		statusTask = new TimerTask() {
            @Override
            public void run() 
            {
            	discordClient.changePresence(StatusType.ONLINE, ActivityType.PLAYING, statusMessages.get(statusIndex % statusMessages.size()));
            	statusIndex++;
            }
        };	        
	}
	
	@EventSubscriber
    public void onShardReadyEvent(ShardReadyEvent event)
    {	    	    	
		IShard shard = event.getShard();
		System.out.println("[DiscordEventHandler] Shard "+shard.getInfo()[0]+" finished connecting "
				+ "with "+shard.getGuilds().size()+" guilds and "+ shard.getUsers().size()+" users.");
    }
	
    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) 
    {	    	    	
    	statusTimer.scheduleAtFixedRate(statusTask, 1000, 1 * 60 * 1000); //1 minute
    	System.out.println("[DiscordEventHandler] Finished logging into Discord");
    }
	
    @EventSubscriber
    public void onTextMessageEvent(MessageReceivedEvent event) 
    {
		try
		{
			responseHandler(event);
		} 
		catch(Exception e) 
		{
			System.out.println("[DiscordEventHandler] text event error: "+e);
		}
    }
    
    @EventSubscriber
    public void onTextMessageUpdateEvent(MessageUpdateEvent event)
    {
    	try 
    	{
			responseHandler(event);
		}
    	catch(Exception e) 
		{
			System.out.println("[DiscordEventHandler] update text event error: "+e);
		}
    }
    
    @EventSubscriber
    public void onTrackFinishEvent(TrackFinishEvent event)
    {
    	event.getPlayer().getGuild().getConnectedVoiceChannel().leave();
    }
    
    public void responseHandler(Event event)
    {
    	//Initial utility variable
		IMessage input;
	
		//check the type of event
		if(event instanceof MessageReceivedEvent)
			input = ((MessageReceivedEvent) event).getMessage();
		else if(event instanceof MessageUpdateEvent)
			input = ((MessageUpdateEvent) event).getNewMessage();
		else
		{
			System.out.println("[DiscordEventHandler] Event not supported.");
			return;
		}
        
		//Declare variables
		Response response;
		ICommand command;
		long channelID;
		Input userInput;
		
        userInput = processor.processInput(input.getContent());
        
        if(userInput == null) //if the command doesn't exist, return
        	return;
        
        //If the message follows the syntax, find it in the command map
        command = commandMap.get(userInput.getFunction());
        
        if(command == null) //if the command isn't supported, return
        	return;
       
        //Get the reply of the command.
        response = command.discordReply(userInput);
        
        System.out.println("[DiscordEventHandler][DISCORD "+input.getShard().getInfo()[0]+"] "
        					+input.getAuthor().getName() + ": " + input.getContent());
        
        //Send the textual reply to the user
        channelID = input.getChannel().getLongID();
    	
    	sendResponse(input, response);
        
        //If there is an image portion, send it
        if(response.getImageReply() != null)
        {
        	sendImages(channelID, response.getImageReply());
        }
        
        //If there is an audio portion, send it
        if(response.getAudioReply() != null)
        {
        	//Send the audio to the voice channel a user is in. If they are not in a voice channel,
        	//then tell user to join an accessible voice channel
        	if(input.getAuthor().getVoiceStateForGuild(input.getGuild()).getChannel() == null)
        	{
        		sendMessage(channelID, input.getAuthor().mention() +
        				", please connect to a voice channel to listen to this Pokedex entry!");
        		return;
        	}
        	
        	//If dex is already in a voice channel in the guild where the request is from, drop this request
        	List<IVoiceChannel> guildChannels = input.getGuild().getVoiceChannels();
        	for(IVoiceChannel vc : guildChannels)
            	if(discordClient.getConnectedVoiceChannels().contains(vc))
            	{
            		sendMessage(channelID, input.getAuthor().mention() +
            				", I am currently speaking a dex entry in this server."
            				+ " If you want to hear your entry spoken then please try again.");
            		return;
            	}
        	
        	playDexEntry(input.getAuthor().getVoiceStateForGuild(input.getGuild()).getChannel(), AudioPlayer.getAudioPlayerForGuild(input.getGuild()), new AudioPlayer.Track(response.getAudioReply()),
        			channelID, input.getAuthor().mention());
        }
    }
    
    private void playDexEntry(IVoiceChannel channel, AudioPlayer player, Track audioTrack, long channelID, String user)
    {        	
    	if(!channel.isConnected())
    	{
    		try
    		{
    			channel.join();
        		discordClient.getDispatcher().waitFor(UserVoiceChannelJoinEvent.class);
        		player.queue(audioTrack);
        		sendMessage(channelID, "Now playing Pokedex entry requested by "+ user);
        		System.out.println("\t[DiscordEventHandler] Audio response sent.");
    		}
    		catch(MissingPermissionsException | InterruptedException e)
    		{
    			sendMessage(channelID, user
        				+", I do not have permission to join the voice channel you are in. "
        				+ "Please connect to another channel if you wish to hear this Pokedex entry." );
    		}
    	}
    	else
    	{
    		player.queue(audioTrack);
    		sendMessage(channelID, "Now playing Pokedex entry requested by "+user);
    		System.out.println("\t[DiscordEventHandler] Audio response sent.");
    	}
    }
    
    private void sendResponse(IMessage userMsg, Response response)
    {
    	//Utility variables
    	MessageBuilder reply = new MessageBuilder(discordClient);
    	Optional<EmbedObject> eo = response.getEmbedObject();
    	
    	//Set up basic reply
    	reply.withContent(response.getDiscordTextReply());
    	if(eo.isPresent())
    		reply.withEmbed(eo.get());
    	
    	//Buffer the reply
    	RequestBuffer.request(() -> 
    	{
    		try
    		{
	    		if(response.isPrivateMessage())
	    		{
	    			reply.withChannel(discordClient.getOrCreatePMChannel(userMsg.getAuthor()));
	    			reply.appendContent("**Join the Pokedex's Home Server!**\n"
	            			+ "https://discord.gg/D5CfFkN".intern());
	    			reply.send();
	    			userMsg.getChannel().sendMessage("Sent to your inbox!".intern());
	    			System.out.println("\t[DiscordEventHandler] PM sent.");
	    		}
	    		else
	    		{
	    			reply.withChannel(userMsg.getChannel());
	    			reply.send();
	    			System.out.println("\t[DiscordEventHandler] Response sent.");
	    		}
    		}
    		catch (Exception e)
    		{
    			System.err.println("[DiscordEventHandler] Message (queued) could not be sent with error: "+ e.getClass().getSimpleName());
                throw e;	//Sends the message to the request buffer
    		}
    	});
    }
    
    private void sendMessage(long ChannelID, String msg)
    {
    	RequestBuffer.request(() -> 
    	{
            try
            {
            	discordClient.getChannelByID(ChannelID).sendMessage(msg);
            	System.out.println("\t[DiscordEventHandler] Text response sent.");
            } 
            catch (Exception e)
            {
                System.err.println("[DiscordEventHandler] Text (queued) could not be sent with error: "+ e.getClass().getSimpleName());
                throw e;	//Sends the message to the request buffer
            }
        });
    }
    
    private void sendImages(long ChannelID, ArrayList<InputStream> imgs)
    {
    	RequestBuffer.request(() -> 
    	{
            try
            {
            	for(InputStream img : imgs)
            		discordClient.getChannelByID(ChannelID).sendFile("", false, img, "model.gif");
            	System.out.println("\t[DiscordEventHandler] Image response sent");
            } 
            catch (Exception e)
            {
            	System.err.println("[DiscordEventHandler] Images could not be sent with error: "+ e.getClass().getSimpleName());
            	throw e;
            }
        });
    }
    
}
