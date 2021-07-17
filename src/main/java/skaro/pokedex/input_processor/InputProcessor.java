package skaro.pokedex.input_processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.input_processor.Input.InputBuilder;
import skaro.pokedex.input_processor.arguments.ParsedText;
import skaro.pokedex.services.CommandService;

public class InputProcessor {
	private CommandService commandService;
	private Pattern prefixPattern, postfixPattern, mentionPattern;
	private long botID;
	
	public InputProcessor(CommandService lib, Long id) {
		commandService = lib;
		botID = id;
		
		String multilingualWord = "\u3131-\uD79Da-zA-Z\u3000-\u303f\u3040-\u309f\u30a0-\u30ff\uff00-\uff9f\u4e00-\u9faf\u3400-\u4dbf";
		
		prefixPattern = Pattern.compile("[!%]["+multilingualWord+"]+[\\s]*.*");
		postfixPattern = Pattern.compile("["+multilingualWord+"]+[\\s]*[(].*[)]");
		mentionPattern = Pattern.compile("<@[0-9]+>[\\s]*["+multilingualWord+"]+[\\s]*.*");
	}
	
	public Mono<Input> createInputFromRawString(String rawString) {
		Optional<ParsedText> parseTest = parseString(rawString);
		
		if(!parseTest.isPresent())
			return Mono.empty();
		
		ParsedText parsedText = parseTest.get();
		if(!commandService.commandOrAliasExists(parsedText.getFunction()))
			return Mono.empty();
		
		return Mono.fromCallable(() -> createInputFromParsedText(parsedText));
	}
	
    private Optional<ParsedText> parseString(String msg)
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
    	
		return Optional.empty();
    }
	
	private Input createInputFromParsedText(ParsedText parsedText) {
		PokedexCommand command = commandService.getByAnyAlias(parsedText.getFunction());
		Language lang = command.getLanguageOfAlias(parsedText.getFunction());
		InputBuilder builder = Input.newBuilder();
		List<CommandArgument> parsedArguments = parseArguments(parsedText, command.getArgumentSpecifications(), lang);
		
		builder.setLanguage(lang);
		builder.setCommand(command);
		builder.setFunction(parsedText.getFunction());
		builder.addArguments(parsedArguments);
		
		return builder.build();
	}
	
	private List<CommandArgument> parseArguments(ParsedText parsedText, List<ArgumentSpec> argumentSpecs, Language lang) {
		Iterator<String> argItr = parsedText.getArgumentIterator();
		List<CommandArgument> result = new ArrayList<>();
		
		for(ArgumentSpec spec : argumentSpecs) {
			if(argItr.hasNext()) {
				result.add(spec.createArgumentFromText(argItr.next(), lang));
			}
			else {
				result.add(spec.createArgumentFromNoText());
			}
		}
		
		return result;
	}
	
    private Optional<ParsedText> parsePrefix(String msg)
    {
    	ParsedText result = new ParsedText();
    	
    	int index = msg.indexOf(" ");
		if(index != -1)
		{
			String unprasedArguments = msg.substring(index + 1);
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
    	
    	String unprasedArguments = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")"));
		result.setArgs(unprasedArguments);
		result.setFunction(msg.substring(0, msg.indexOf("(")).toLowerCase());
		
		return Optional.of(result);
    }
    
    private Optional<ParsedText> parseMention(String msg)
    {
    	ParsedText result = new ParsedText();
    	
    	String id = msg.substring(msg.indexOf("@") + 1, msg.indexOf(">"));
    	if(Long.parseLong(id) != botID)
    		return Optional.empty();
		
    	int indexFunc = msg.indexOf(" ");
    	if(indexFunc == -1)
    		return Optional.empty();
    	
    	int indexArgs = msg.indexOf(" ", indexFunc + 1);
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