package skaro.pokedex.input_processor;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import skaro.pokedex.core.CommandLibrary;
import skaro.pokedex.data_processor.commands.ArgumentRange;
import skaro.pokedex.data_processor.commands.ICommand;
import skaro.pokedex.input_processor.arguments.AbstractArgument;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.input_processor.arguments.ParsedText;

public class InputProcessor 
{
	private CommandLibrary commandLibrary;
	
	public InputProcessor(CommandLibrary lib)
	{
		commandLibrary = lib;
	}
	public Optional<Input> processInput(String input)
	{
		//Utility variables
		Optional<ParsedText> parseTest = parseTextMessage(input);
		ParsedText parsedText;
		ICommand command;
		Input result;
		List<AbstractArgument> argsFromParse;
		Iterator<String> argItr;
		
		//If args is null, then the input does not match the command format. Discard.
		if(!parseTest.isPresent())
			return Optional.empty();
		
		//If argument is not in the map, the command is not supported
		parsedText = parseTest.get();
		if(!commandLibrary.hasCommand(parsedText.getFunction()))
			return Optional.empty();
		
		command = commandLibrary.getCommand(parsedText.getFunction());
		result = new Input(parsedText.getFunction());
		
		//Check for a legal number of arguments
		if(!hasExpectedNumberOfArguments(parsedText, command))
		{
			result.setErrorStatus(InputErrorStatus.ARGUMENT_NUMBER);
			return Optional.of(result);
		}
		
		//Parse each argument
		argItr = parsedText.getArgumentIterator();
		for(ArgumentCategory argCat : command.getArgumentCats())
		{
			argsFromParse = argCat.parse(argItr);
			result.addArgs(argsFromParse);
		}
		
		for(AbstractArgument abstractArg : result.getArgs())
			if(!abstractArg.isValid())
			{
				result.setErrorStatus(InputErrorStatus.INVALID_ARGUMENT);
				break;
			}
		
		return Optional.of(result);
	}
	
	private boolean hasExpectedNumberOfArguments(ParsedText text, ICommand cmd)
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
    	if(msg == null || msg.trim().equals(""))
    		return Optional.empty();
    	
    	//Pattern matching variables
    	Pattern p1, p2;
    	Matcher matcher;
    	ParsedText result = new ParsedText();
    	String unprasedArguments;
    	int index;

		//format: [prefix] [command] [args]
		p1 = Pattern.compile("[!%][a-zA-Z]+[\\s]*.*");
		matcher = p1.matcher(msg);
		 
		//This is a command with an argument
		if(matcher.matches())
		{	        	 
			index = msg.indexOf(" ");
			if(index != -1)
			{
				unprasedArguments = msg.substring(index + 1).trim();
				result.setArgs(unprasedArguments);
				result.setFunction(msg.substring(1, index).toLowerCase());
			}
			else
				result.setFunction(msg.substring(1, msg.length()).toLowerCase());
			
			return Optional.of(result);
		}
    	
		p2 = Pattern.compile("[a-zA-Z]+[\\s]*[(].*[)]");
		matcher = p2.matcher(msg);
		
    	//Other formats: command(args) or command()
    	if(matcher.matches())
    	{
    		unprasedArguments = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")")).trim();
    		result.setArgs(unprasedArguments);
    		result.setFunction(msg.substring(0, msg.indexOf("(")).toLowerCase());
    		
    		return Optional.of(result);
    	}
 
		//Patterns do not match
		return Optional.empty();
    }
}