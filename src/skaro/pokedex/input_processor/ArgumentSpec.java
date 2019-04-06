package skaro.pokedex.input_processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import skaro.pokedex.input_processor.arguments.NoneArgument;

public class ArgumentSpec
{
	private List<Class<? extends CommandArgument>> argumentCategories;
	private boolean isOptional;
	
	@SafeVarargs
	public ArgumentSpec(boolean isOptional, Class<? extends CommandArgument>... argumentCategories)
	{
		this.isOptional = isOptional;
		this.argumentCategories = Arrays.asList(argumentCategories);
	}

	public CommandArgument createArgumentFromText(String rawArgument, Language lang)
	{
		try
		{
			List<CommandArgument> parsedArguments = new ArrayList<>();
			
			for(Class<? extends CommandArgument> argumentClass : argumentCategories)
			{
				CommandArgument possibleArgument = argumentClass.newInstance();
				possibleArgument.setUp(rawArgument, lang);
				parsedArguments.add(possibleArgument);
			}

			return chooseBestArgument(parsedArguments);
		}
		catch(Exception e)
		{
			return new InvalidArgument();
		}
	}
	
	public CommandArgument createArgumentFromNoText()
	{
		if(isOptional)
			return new NoneArgument();
		
		return new MissingArgument();
	}

	private CommandArgument chooseBestArgument(List<CommandArgument> arguments)
	{
		Optional<CommandArgument> possibleBestArgument = arguments.stream()
				.filter(CommandArgument::isValid)
				.filter(argument -> !argument.isSpellChecked())
				.findFirst();

		if(possibleBestArgument.isPresent())
			return possibleBestArgument.get();

		possibleBestArgument = arguments.stream()
				.filter(CommandArgument::isValid)
				.findFirst();

		if(possibleBestArgument.isPresent())
			return possibleBestArgument.get();

		return new InvalidArgument();
	}

}
