package skaro.pokedex.core;

import java.nio.ByteBuffer;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.PrivateChannel;
import discord4j.core.object.entity.User;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.ChannelRateLimiter;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.InputProcessor;

public class DiscordMessageEventHandler
{
	private InputProcessor inputProcessor;
	private ChannelRateLimiter rateLimiter;
	
	public DiscordMessageEventHandler(InputProcessor inputProcessor, ChannelRateLimiter rateLimiter)
	{
		this.inputProcessor = inputProcessor;
		this.rateLimiter = rateLimiter;
	}

	public Mono<Message> onMessageCreateEvent(MessageCreateEvent event)
	{
		Message newlyReceivedMessage = event.getMessage();
		Optional<String> possibleContent = newlyReceivedMessage.getContent();

		if(!possibleContent.isPresent())
			return Mono.empty();

		return processMessageEvent(newlyReceivedMessage, possibleContent.get());
	}

	public Mono<Message> onMessageEditEvent(MessageUpdateEvent event)
	{
		Mono<Message> newlyReceivedMessage = event.getMessage();
		Optional<String> possibleContent = event.getCurrentContent();

		if(!possibleContent.isPresent())
			return Mono.empty();

		return newlyReceivedMessage
				.flatMap(message -> processMessageEvent(message, possibleContent.get()));
	}
	
	private Mono<Message> processMessageEvent(Message messageReceived, String messageContent)
	{
		return prepareReply(messageReceived, messageContent)
				.flatMap(reply -> sendAckMessageIfNeeded(reply))
				.flatMap(reply -> executeCommandAndAddResponseToStructure(reply))
				.flatMap(reply -> deleteAckMessageIfNeeded(reply))
				.flatMap(reply -> sendReply(reply))
				.onErrorContinue((t,o) -> System.out.println("What? Impossible!"));
	}
	
	private Mono<ReplyStructure> prepareReply(Message receivedMessage, String messageContent)
	{
		return Mono.just(new ReplyStructure())
				.flatMap(struct -> addAuthorAndVoiceStateToStructure(struct, receivedMessage))
				.filter(struct -> !struct.author.isBot())
				.flatMap(struct -> addChannelOfMessageToStructure(struct, receivedMessage))
				.filter(struct -> !rateLimiter.channelIsRateLimited(struct.channel.getId()))
				.flatMap(struct -> addPrivateChannelToStructure(struct, struct.author))
				.flatMap(struct -> parseAndAddInputToStructure(struct, messageContent));
	}
	
	private Mono<ReplyStructure> sendAckMessageIfNeeded(ReplyStructure struct)
	{
		if(!shouldHaveAckMessage(struct.input))
			return Mono.just(struct);
		
		String ackContent = struct.author.getUsername() +", gathering data for your request...";
		return struct.channel.createMessage(ackContent)
				.doOnNext(ackMessage -> struct.ackMessage = ackMessage)
				.map(ackMessage -> struct);
	}
	
	private Mono<ReplyStructure> executeCommandAndAddResponseToStructure(ReplyStructure struct)
	{
		return getResponseFromCommand(struct)
				.doOnNext(response -> struct.response = response)
				.map(user -> struct);
	}
	
	private Mono<ReplyStructure> deleteAckMessageIfNeeded(ReplyStructure struct)
	{
		if(struct.ackMessage == null)
			return Mono.just(struct);
		
		return struct.ackMessage.delete()
				.thenReturn(struct);
	}
	
	private Mono<Message> sendReply(ReplyStructure struct)
	{
		Response response = struct.response;
		
		if(response.isPrivateMessage())
		{
			return response.getAsSpec()
					.flatMap(spec -> struct.privateChannel.createMessage(spec))
					.flatMap(directMessage -> struct.channel.createMessage("Sent to your inbox!"));
		}
		
		return response.getAsSpec()
				.flatMap(spec -> struct.channel.createMessage(spec));
				//.doOnNext(message -> playAudioIfApplicable(struct));
	}
	
	private Mono<ReplyStructure> playAudioIfApplicable(ReplyStructure struct)
	{
		if(!shouldSendAudioToVoiceChannel(struct))
			return Mono.just(struct);
		
		Optional<AudioProvider> provider = createAudioProvider(struct.response.getPlayback());
		
		if(!provider.isPresent())
			return Mono.just(struct);
		
		return struct.authorVoiceState.getChannel()
				.flatMap(channel -> channel.join(spec -> spec.setProvider(provider.get())))
				.doOnNext(VoiceConnection::disconnect)
				.map(obj -> struct);
	}
	
	private boolean shouldSendAudioToVoiceChannel(ReplyStructure struct)
	{
		return struct.response.hasPlayback() && struct.authorVoiceState.getChannelId().isPresent();
	}
	
	private Optional<AudioProvider> createAudioProvider(AudioInputStream audioStream)
	{
		try
		{
			AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
	        AudioSourceManagers.registerRemoteSources(playerManager);
	        AudioPlayer player = playerManager.createPlayer();
	        byte[] buffer = new byte[audioStream.available()];
	        audioStream.read(buffer);
	        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
	        return Optional.of(new MyAudioProvider(player, byteBuffer));
		}
		catch(Exception e)
		{
			return Optional.empty();
		}
	}
	
	private Mono<ReplyStructure> addAuthorAndVoiceStateToStructure(ReplyStructure struct, Message message)
	{
		User author = message.getAuthor().get();
		struct.author = author;
		
		return message.getGuild()
				.map(guild -> guild.getId())
				.flatMap(guildId -> author.asMember(guildId))
				.flatMap(Member::getVoiceState)
				.doOnNext(voiceState -> struct.authorVoiceState = voiceState)
				.map(voiceState -> struct)
				.defaultIfEmpty(struct);
	}
	
	private Mono<ReplyStructure> parseAndAddInputToStructure(ReplyStructure struct, String messageContent)
	{
		return inputProcessor.createInputFromRawString(messageContent)
				.doOnNext(input -> struct.input = input)
				.map(input -> struct);
	}
	
	private Mono<ReplyStructure> addChannelOfMessageToStructure(ReplyStructure struct, Message message)
	{
		return message.getChannel()
				.doOnNext(channel -> struct.channel = channel)
				.map(channel -> struct);
	}
	
	private Mono<ReplyStructure> addPrivateChannelToStructure(ReplyStructure struct, User author)
	{
		return author.getPrivateChannel()
				.doOnNext(channel -> struct.privateChannel = channel)
				.map(channel -> struct);
	}

	private Mono<Response> getResponseFromCommand(ReplyStructure struct)
	{
		Input input = struct.input;
		PokedexCommand command = input.getCommand();

		try
		{
			User author = struct.author;
			return command.discordReply(input, author);
		}
		catch(Exception e)
		{
			return Mono.just(command.createErrorResponse(input, e));
		}
	}
	
	private boolean shouldHaveAckMessage(Input input)
	{
		return input.getCommand().makesWebRequest() && input.isValid();
	}
	
	private class ReplyStructure
	{
		Message ackMessage;
		MessageChannel channel;
		PrivateChannel privateChannel;
		User author;
		VoiceState authorVoiceState;
		Input input;
		Response response;
	}
	
	private class MyAudioProvider extends AudioProvider
    {
    	private AudioPlayer player;
        private MutableAudioFrame frame;
        
        public MyAudioProvider(AudioPlayer player, ByteBuffer buffer)
        {
        	super(buffer);
        	this.player = player;
        	frame = new MutableAudioFrame();
        }
        
        @Override
        public boolean provide() 
        {
            boolean didProvide = player.provide(frame);
            if (didProvide) getBuffer().flip();
            return didProvide;
        }
    }
}
