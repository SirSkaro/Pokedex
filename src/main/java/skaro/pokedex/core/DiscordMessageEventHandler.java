package skaro.pokedex.core;

import java.util.Optional;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
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

	public Mono<Input> onMessageCreateEvent(MessageCreateEvent event)
	{
		Message newlyReceivedMessage = event.getMessage();
		Optional<String> possibleContent = Optional.ofNullable(newlyReceivedMessage.getContent());

		if(!possibleContent.isPresent())
			return Mono.empty();

		return processMessageEvent(newlyReceivedMessage, possibleContent.get())
				.map(reply -> reply.input)
				.onErrorResume(error -> {error.printStackTrace(); return Mono.empty();});
	}

	public Mono<Input> onMessageEditEvent(MessageUpdateEvent event)
	{
		Mono<Message> newlyReceivedMessage = event.getMessage();
		Optional<String> possibleContent = event.getCurrentContent();

		if(!possibleContent.isPresent())
			return Mono.empty();

		return newlyReceivedMessage
				.flatMap(message -> processMessageEvent(message, possibleContent.get()))
				.map(reply -> reply.input)
				.onErrorResume(error -> Mono.empty());
	}
	
	private Mono<ReplyStructure> processMessageEvent(Message messageReceived, String messageContent)
	{
		return prepareReply(messageReceived, messageContent)
				.flatMap(reply -> sendAckMessageIfNeeded(reply))
				.flatMap(reply -> executeCommandAndAddResponseToStructure(reply))
				.flatMap(reply -> deleteAckMessageIfNeeded(reply))
				.flatMap(reply -> sendReply(reply));
	}
	
	private Mono<ReplyStructure> prepareReply(Message receivedMessage, String messageContent)
	{
		return Mono.just(new ReplyStructure())
				.filter(struct -> !receivedMessage.mentionsEveryone())
				.flatMap(struct -> parseAndAddInputToStructure(struct, messageContent))
				.flatMap(struct -> addAuthorToStructure(struct, receivedMessage))
				.filter(struct -> !struct.author.isBot())
				.flatMap(struct -> addChannelOfMessageToStructure(struct, receivedMessage))
				.filterWhen(struct -> botHasPermissionsForThisChannel(struct, PermissionSet.of(Permission.SEND_MESSAGES)))
				.filter(struct -> !rateLimiter.channelIsRateLimited(struct.channel.getId()))
				.flatMap(struct -> addPrivateChannelToStructure(struct, struct.author))
				.flatMap(struct -> addGuildToStructure(struct, receivedMessage));
	}
	
	private Mono<Boolean> botHasPermissionsForThisChannel(ReplyStructure struct, PermissionSet neededPermissions)
	{
		Snowflake botId = struct.channel.getClient().getSelfId();
		
		if(botId.asLong() == 0)
			return Mono.just(false);
		
		if(struct.channel instanceof GuildChannel)
		{
			GuildChannel channel = (GuildChannel)struct.channel;
			
			return channel.getEffectivePermissions(botId)
					.map(permissions -> permissions.containsAll(neededPermissions));
		}
		
		return Mono.just(true);
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
	
	private Mono<ReplyStructure> sendReply(ReplyStructure struct)
	{
		Response response = struct.response;

		if(response.isPrivateMessage())
		{
			return response.getAsSpec()
					.flatMap(spec -> struct.privateChannel.createMessage(spec))
					.flatMap(directMessage -> struct.channel.createMessage("Sent to your inbox!"))
					.map(sentMessage -> struct);
		}
		
		return response.getAsSpec()
				.flatMap(spec -> struct.channel.createMessage(spec))
				.map(sentMessage -> struct);
	}
	
	private Mono<ReplyStructure> addAuthorToStructure(ReplyStructure struct, Message message)
	{
		User author = message.getAuthor().get();
		struct.author = author;
		
		return Mono.just(struct);
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
	
	private Mono<ReplyStructure> addGuildToStructure(ReplyStructure struct, Message message)
	{
		return message.getGuild()
				.doOnNext(guild -> struct.guild = guild)
				.map(channel -> struct)
				.switchIfEmpty(Mono.just(struct));
	}

	private Mono<Response> getResponseFromCommand(ReplyStructure struct)
	{
		Input input = struct.input;
		PokedexCommand command = input.getCommand();

		try
		{
			User author = struct.author;
			Guild guild = struct.guild;
			return command.respondTo(input, author, guild);
		}
		catch(Exception e)
		{
			return Mono.just(command.createErrorResponse(input, e));
		}
	}
	
	private boolean shouldHaveAckMessage(Input input)
	{
		return input.getCommand().makesWebRequest() && input.allArgumentValid();
	}
	
	private class ReplyStructure
	{
		Guild guild;
		Message ackMessage;
		MessageChannel channel;
		PrivateChannel privateChannel;
		User author;
		Input input;
		Response response;
	}
}
