package skaro.pokedex.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.CommandService;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.InputProcessor;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;
import sx.blah.discord.util.audio.events.TrackFinishEvent;

public class PostLoginEventHandler 
{
	private InputProcessor processor;
	private Cache<Long, Bucket> bucketCache;
	
	public PostLoginEventHandler(CommandService lib, ScheduledExecutorService threadPool, Long botID)
	{
		processor = new InputProcessor(lib, botID);
		bucketCache = Caffeine.newBuilder()
				.executor(threadPool)
				.weakValues()
				.expireAfterAccess(10, TimeUnit.SECONDS)
				.maximumSize(50)
				.build();
	}
	
	 @EventSubscriber
    public void onTextMessageEvent(MessageReceivedEvent event) 
    {
    	if(event.getAuthor().isBot())
    		return;
    	
		try
		{ handleTextResponse(event.getMessage()); }
		catch(Exception e) 
		{ handleResponseError(event.getMessage(), e); }
    }
    
    @EventSubscriber
    public void onTextMessageUpdateEvent(MessageUpdateEvent event)
    {
    	if(event.getAuthor().isBot())
    		return;
    	
    	try 
    	{ handleTextResponse(event.getNewMessage()); }
    	catch(Exception e) 
		{ handleResponseError(event.getMessage(), e); }
    }
    
    @EventSubscriber
    public void onTrackFinishEvent(TrackFinishEvent event)
    {
    	event.getPlayer().getGuild().getConnectedVoiceChannel().leave();
    }
    
    private void handleResponseError(IMessage message, Exception e)
    {
    	if(e instanceof MissingPermissionsException)
    	{
    		MissingPermissionsException mpe = (MissingPermissionsException)e;
			StringBuilder builder = new StringBuilder();
			builder.append("I don't have required permissions to respond in `");
			builder.append(message.getGuild().getName() +"`'s ");
			builder.append("`#" + message.getChannel().getName() + "` channel. ");
			builder.append("Please inform an admin that I'm missing the following permissions:\n");
			
			for(Permissions perm: mpe.getMissingPermissions())
				builder.append(":diamonds:" + perm.name() + "\n");
			
    		message.getAuthor().getOrCreatePMChannel().sendMessage(builder.toString());
    	}
    	else
    	{
			System.out.println("[DiscordEventHandler] text event error: "+e.getClass().getName());
			e.printStackTrace();
			message.getChannel().sendMessage("Some error occured and I could not recover. Please report this in the support server: https://discord.gg/D5CfFkN");
		}
    }
    
    private void handleTextResponse(IMessage userMsg)
    {
    	//Utility variable
//		Response response;
//		AbstractCommand command;
//		Optional<Input> parseTest;
//		Input userInput;
//		Optional<IMessage> ackMsg = Optional.empty();
//		
//		parseTest = processor.processInput(userMsg.getContent());
//        if(!parseTest.isPresent()) //if the command doesn't exist, return
//        	return;
//        
//        //If the message follows the syntax, find it in the command map
//        userInput = parseTest.get();
//        command = userInput.getCommand();
//        if(command == null) //if the command isn't supported, return
//        	return;
//        
//        //Enfore rate limit
//        if(channelIsRateLimited(userMsg.getChannel().getLongID()))
//        {
//        	System.out.println("[DiscordEventHandler] Rate limit exceeded for user " + userMsg.getAuthor().getLongID());
//        	return;
//        }
//
//        //Send acknowledgement message to alert the user their response is being processed if a web request is being made
//        if(command.makesWebRequest())
//        	ackMsg = sendAcknowledgement(userMsg);
//        
//        System.out.println("[DiscordEventHandler] "
//				+userMsg.getAuthor().getName() + ": " + userMsg.getContent());
//        
//        //Get the reply of the command.
//        response = command.discordReply(userInput, userMsg.getAuthor());
//        
//        //Send the textual reply to the user
//        if(ackMsg.isPresent())
//        	ackMsg.get().delete();
//       	sendResponse(userMsg, response);
//        
//        //If there is an audio portion, send it
//        if(response.getAudioReply() != null)
//        {
//        	if(connectToVoiceChannel(userMsg))
//	        	playDexEntry(userMsg.getAuthor().getVoiceStateForGuild(userMsg.getGuild()).getChannel(), 
//	        			AudioPlayer.getAudioPlayerForGuild(userMsg.getGuild()), new AudioPlayer.Track(response.getAudioReply()),
//	        			userMsg.getChannel().getLongID(), userMsg.getAuthor().mention());
//        }
    }
    
    private boolean channelIsRateLimited(Long channelID)
    {
    	//Check if bucket exists for a guild
    	Bucket bucketForChannel = bucketCache.getIfPresent(channelID);
    	
    	//If the bucket does not exist, make one and take a token
    	if(bucketForChannel == null)
    	{
    		bucketForChannel = createBucketForChannel(channelID);
    		bucketCache.put(channelID, bucketForChannel);
    		bucketForChannel.tryConsume(1);
    		return false;
    	}
    	
    	//Otherwise, try to take a token
    	return !bucketForChannel.tryConsume(1);
    }
    
    private Bucket createBucketForChannel(Long channelID)
    {
    	Bandwidth limit = Bandwidth.simple(2, Duration.ofSeconds(10));
    	Bucket bucket = Bucket4j.builder()
    						.addLimit(limit)
    						.build();
    	
    	return bucket;
    }
    
    private Optional<IMessage> sendAcknowledgement(IMessage userMsg)
    {
    	MessageBuilder reply = new MessageBuilder(userMsg.getClient());
    	reply.withChannel(userMsg.getChannel());
    	reply.withContent(userMsg.getAuthor().getName() + ", gathering data for your request...");
    	return Optional.of(reply.send());
    }
    
    private boolean connectToVoiceChannel(IMessage userMsg)
    {
    	IDiscordClient discordClient = userMsg.getClient();
    	
    	//Send the audio to the voice channel a user is in. If they are not in a voice channel,
    	//then tell user to join an accessible voice channel
    	if(userMsg.getAuthor().getVoiceStateForGuild(userMsg.getGuild()).getChannel() == null)
    		return false;
    	
    	//If dex is already in a voice channel in the guild where the request is from, drop this request
    	List<IVoiceChannel> guildChannels = userMsg.getGuild().getVoiceChannels();
    	for(IVoiceChannel vc : guildChannels)
        	if(discordClient.getConnectedVoiceChannels().contains(vc))
        	{
        		sendMessage(discordClient, userMsg.getChannel().getLongID(), userMsg.getAuthor().mention() +
        				", I am currently speaking a dex entry in this server."
        				+ " If you want to hear your entry spoken then please wait and try again.");
        		return false;
        	}
    	
    	return true;
    }
    
    private void playDexEntry(IVoiceChannel channel, AudioPlayer player, Track audioTrack, long channelID, String user)
    {        	
    	IDiscordClient discordClient = channel.getClient();
    	
    	if(!channel.isConnected())
    	{
    		try
    		{
    			channel.join();
        		discordClient.getDispatcher().waitFor(UserVoiceChannelJoinEvent.class);
        		player.queue(audioTrack);
        		sendMessage(discordClient, channelID, "Now playing Pokedex entry requested by "+ user);
        		System.out.println("\t[DiscordEventHandler] Audio response sent.");
    		}
    		catch(MissingPermissionsException | InterruptedException e)
    		{
    			sendMessage(discordClient, channelID, user
        				+", I do not have permission to join the voice channel you are in. "
        				+ "Please connect to another channel if you wish to hear this Pokedex entry." );
    		}
    	}
    	else
    	{
    		player.queue(audioTrack);
    		sendMessage(discordClient, channelID, "Now playing Pokedex entry requested by "+user);
    		System.out.println("\t[DiscordEventHandler] Audio response sent.");
    	}
    }
    
    private void sendResponse(IMessage userMsg, Response response)
    {
//    	//Utility variables
//    	MessageBuilder reply = new MessageBuilder(userMsg.getClient());
//    	Optional<EmbedObject> embed = response.getEmbedObject();
//    	Optional<File> image = response.getImage();
//    	
//    	//Set up reply
//    	reply.withContent(response.getDiscordTextReply());
//    	if(embed.isPresent())
//    		reply.withEmbed(embed.get());
//    	if(image.isPresent())
//    	{
//    		try { reply.withFile(image.get()); } 
//    		catch (FileNotFoundException e1) { response.addToReply("Could not attach the image you requested!"); }
//    	}
//    	
//    	//Buffer the reply
//    	RequestBuffer.request(() -> 
//    	{
//    		try
//    		{
//	    		if(response.isPrivateMessage())
//	    		{
//	    			reply.withChannel(userMsg.getClient().getOrCreatePMChannel(userMsg.getAuthor()));
//	    			reply.send();
//	    			userMsg.getChannel().sendMessage("Sent to your inbox!".intern());
//	    			System.out.println("\t[DiscordEventHandler] PM sent.");
//	    		}
//	    		else
//	    		{
//	    			reply.withChannel(userMsg.getChannel());
//	    			reply.send();
//	    			System.out.println("\t[DiscordEventHandler] Response sent.");
//	    		}
//    		}
//    		catch(MissingPermissionsException mpe)
//    		{
//    			handleResponseError(userMsg, mpe);
//    		}
//    		catch (Exception e)
//    		{
//    			System.err.println("[DiscordEventHandler] Message (queued) could not be sent with error: "+ e.getClass().getSimpleName());
//                throw e;	//Sends the message to the request buffer
//    		}
//    	});
    }
    
    private void sendMessage(IDiscordClient discordClient, long ChannelID, String msg)
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
}
