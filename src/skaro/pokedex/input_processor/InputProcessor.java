package skaro.pokedex.input_processor;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.CommandService;
import skaro.pokedex.data_processor.commands.ArgumentRange;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.input_processor.arguments.ParsedText;

public class InputProcessor 
{
	private CommandService commandLibrary;
	private Pattern prefixPattern, postfixPattern, mentionPattern;
	private long botID;
	
	public InputProcessor(CommandService lib, Long id)
	{
		commandLibrary = lib;
		botID = id;
		
		String multilingualWord = "\u3131-\uD79Da-zA-Z\u3000-\u303f\u3040-\u309f\u30a0-\u30ff\uff00-\uff9f\u4e00-\u9faf\u3400-\u4dbf";
		
		prefixPattern = Pattern.compile("[!%]["+multilingualWord+"]+[\\s]*.*");
		postfixPattern = Pattern.compile("["+multilingualWord+"]+[\\s]*[(].*[)]");
		mentionPattern = Pattern.compile("<@[0-9]+>[\\s]*["+multilingualWord+"]+[\\s]*.*");
	}
	public Mono<Input> processInput(String input)
	{
		//Utility variables
		Optional<ParsedText> parseTest = parseTextMessage(input);
		ParsedText parsedText;
		AbstractCommand command;
		Input result;
		List<AbstractArgument> argsFromParse;
		Iterator<String> argItr;
		Language lang;
		
		//If args is null, then the input does not match the command format. Discard.
		if(!parseTest.isPresent())
			return Mono.empty();
		
		//If argument is not in the map, the command is not supported
		parsedText = parseTest.get();
		if(!commandLibrary.hasCommand(parsedText.getFunction()))
			return Mono.empty();
		
		command = commandLibrary.get(parsedText.getFunction());
		lang = command.getLanguageOfAlias(parsedText.getFunction());
		result = new Input(parsedText.getFunction(), command, lang);
		
		//Check for a legal number of arguments
		if(!hasExpectedNumberOfArguments(parsedText, command))
		{
			result.setErrorStatus(InputErrorStatus.ARGUMENT_NUMBER);
			return Mono.just(result);
		}
		
		//Parse each argument
		argItr = parsedText.getArgumentIterator();
		for(ArgumentCategory argCat : command.getArgumentCats())
		{
			argsFromParse = argCat.parse(argItr, lang);
			result.addArgs(argsFromParse);
		}
		
		for(AbstractArgument abstractArg : result.getArgs())
			if(!abstractArg.isValid())
			{
				result.setErrorStatus(InputErrorStatus.INVALID_ARGUMENT);
				break;
			}
		
		return Mono.just(result);
	}
	
	private boolean hasExpectedNumberOfArguments(ParsedText text, AbstractCommand cmd)
	{
		ArgumentRange range = cmd.getExpectedArgumentRange();
		int numArgs = text.getNumberOfArguments();
		
		return numArgs >= range.getMin() && numArgs <= range.getMax();
	}
	
	/**
     * A method to parse an incoming message. Tests if the message is a command that
     * the bot replies textually to. If it is a command
     * then the message is organized into a String array
     */
    private Optional<ParsedText> parseTextMessage(String msg)
    {
    	Matcher matcher;
    	
    	if(msg == null || msg.trim().equals(""))
    		return Optional.empty();
    	
		matcher = prefixPattern.matcher(msg);
		if(matcher.matches())
			return parsePrefix(msg);
    	
		matcher = postfixPattern.matcher(msg);
    	if(matcher.matches())
    		return parsePostfix(msg);
    	
    	matcher = mentionPattern.matcher(msg);
    	if(matcher.matches())
    		return parseMention(msg);
    	
		//Patterns do not match
		return Optional.empty();
    }
    
    private Optional<ParsedText> parsePrefix(String msg)
    {
    	ParsedText result = new ParsedText();
    	String unprasedArguments;
    	int index;
    	
    	index = msg.indexOf(" ");
		if(index != -1)
		{
			unprasedArguments = msg.substring(index + 1);
			result.setArgs(unprasedArguments);
			result.setFunction(msg.substring(1, index).toLowerCase());
		}
		else
			result.setFunction(msg.substring(1, msg.length()).toLowerCase());
		
		return Optional.of(result);
    }
    
    private Optional<ParsedText> parsePostfix(String msg)
    {
    	ParsedText result = new ParsedText();
    	String unprasedArguments;
    	
    	unprasedArguments = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
		result.setArgs(unprasedArguments);
		result.setFunction(msg.substring(0, msg.indexOf("(")).toLowerCase());
		
		return Optional.of(result);
    }
    
    private Optional<ParsedText> parseMention(String msg)
    {
    	ParsedText result = new ParsedText();
    	String id;
    	int indexFunc, indexArgs;
    	
    	id = msg.substring(msg.indexOf("@") + 1, msg.indexOf(">"));
    	if(Long.parseLong(id) != botID)
    		return Optional.empty();
		
    	indexFunc = msg.indexOf(" ");
    	if(indexFunc == -1)
    		return Optional.empty();
    	
    	indexArgs = msg.indexOf(" ", indexFunc + 1);
    	if(indexArgs == -1)
    	{
    		result.setFunction(msg.substring(msg.indexOf(">") + 1));
    		return Optional.of(result);
    	}
    	
    	result.setFunction(msg.substring(indexFunc + 1, indexArgs));
    	result.setArgs(msg.substring(indexArgs + 1));
    	
		return Optional.of(result);
    }
}