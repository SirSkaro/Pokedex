package skaro.pokedex.core;

import java.util.Optional;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.InputProcessor;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEditEvent;

public class DiscordMessageEventHandler
{
	private InputProcessor inputProcessor;

	public Mono<Message> onMessageCreateEvent(MessageCreateEvent event)
	{
		Message newlyReceivedMessage = event.getMessage();
		Optional<String> possibleContent = newlyReceivedMessage.getContent();

		if(!possibleContent.isPresent())
			return Mono.empty();

		return Mono.just(possibleContent.get())
				.flatMap(messageConent -> setUpReply(newlyReceivedMessage, messageConent))
				.flatMap(reply -> sendReply(reply))
				.onErrorContinue((t,o) -> System.out.println("What? Impossible!"));
	}

	public static void onMessageEditEvent(MessageEditEvent event)
	{

	}
	
	private Mono<ReplyStructure> setUpReply(Message receivedMessage, String messageContent)
	{
		return Mono.just(new ReplyStructure())
				.flatMap(replyStruct -> inputProcessor.processInput(messageContent)
						.doOnNext(input -> replyStruct.input = input)
						.then(receivedMessage.getAuthor())
						.doOnNext(author -> replyStruct.author = author)
						.then(receivedMessage.getChannel())
						.doOnNext(channel -> replyStruct.channel = channel)
						.then(getResponseFromCommand(replyStruct))
						.flatMap(response -> response.getAsSpec())
						.doOnNext(spec -> replyStruct.spec = spec)
						.then(Mono.just(replyStruct)));
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
	
	private Mono<Message> sendReply(ReplyStructure struct)
	{
		return struct.channel.createMessage(struct.spec);
	}

	private class ReplyStructure
	{
		MessageChannel channel;
		User author;
		Input input;
		MessageCreateSpec spec;
	}
}
