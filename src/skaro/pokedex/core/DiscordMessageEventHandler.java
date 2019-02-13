package skaro.pokedex.core;

import java.util.Optional;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.PrivateChannel;
import discord4j.core.object.entity.User;
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
				.flatMap(reply -> sendAckMessageIfMakingWebRequests(reply))
				.flatMap(reply -> executeCommandAndAddResponseToStructure(reply))
				.flatMap(reply -> deleteAckMessageIfMakingWebRequests(reply))
				.flatMap(reply -> sendReply(reply))
				.onErrorContinue((t,o) -> System.out.println("What? Impossible!"));
	}
	
	private Mono<ReplyStructure> prepareReply(Message receivedMessage, String messageContent)
	{
		return Mono.just(new ReplyStructure())
				.flatMap(struct -> addAuthorOfMessageToStructure(struct, receivedMessage))
				.filter(struct -> !struct.author.isBot())
				.flatMap(struct -> addChannelOfMessageToStructure(struct, receivedMessage))
				.filter(struct -> !rateLimiter.channelIsRateLimited(struct.channel.getId()))
				.flatMap(struct -> addPrivateChannelToStructure(struct, struct.author))
				.flatMap(struct -> parseAndAddInputToStructure(struct, messageContent));
	}
	
	private Mono<ReplyStructure> sendAckMessageIfMakingWebRequests(ReplyStructure struct)
	{
		if(!struct.input.getCommand().makesWebRequest())
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
	
	private Mono<ReplyStructure> deleteAckMessageIfMakingWebRequests(ReplyStructure struct)
	{
		if(!struct.input.getCommand().makesWebRequest())
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
	}
	
	private Mono<ReplyStructure> addAuthorOfMessageToStructure(ReplyStructure struct, Message message)
	{
		return message.getAuthor()
				.doOnNext(author -> struct.author = author)
				.map(user -> struct);
	}
	
	private Mono<ReplyStructure> parseAndAddInputToStructure(ReplyStructure struct, String messageContent)
	{
		return inputProcessor.processInput(messageContent)
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
	
	private class ReplyStructure
	{
		Message ackMessage;
		MessageChannel channel;
		PrivateChannel privateChannel;
		User author;
		Input input;
		Response response;
	}
}
