package skaro.pokedex.input_processor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.input_processor.arguments.InvalidCommandArgument;
import skaro.pokedex.input_processor.arguments.NoneArgument;

public class Input 
{
	private List<CommandArgument> arguments;
	private String function;
	private Language language;
	private PokedexCommand command;
	
	private Input(InputBuilder inputBuilder)
	{
		this.arguments = inputBuilder.arguments;
		this.function = inputBuilder.function;
		this.language = inputBuilder.language;
		this.command = inputBuilder.command;
	}

	public List<CommandArgument> getArguments() { return arguments; }
	public String getFunction() { return function; }
	public Language getLanguage() { return language; }
	public PokedexCommand getCommand() { return command; }
	
	public static InputBuilder newBuilder()
	{
		return new InputBuilder();
	}
	
	public CommandArgument getArgument(int index)
	{
		return arguments.get(index);
	}
	
	public List<CommandArgument> getNonEmptyArguments()
	{
		return arguments.stream()
				.filter(argument -> !(argument instanceof NoneArgument))
				.collect(Collectors.toList());
	}
	
	public String argsToString()
	{
		if(arguments.isEmpty())
			return "(no input)";
		
		StringBuilder builder = new StringBuilder();
		
		for(CommandArgument arg : arguments)
			builder.append(arg.toString() + ", ");
		
		return builder.substring(0, builder.length() - 2);
	}
	
	public boolean allArgumentValid()
	{
		for(CommandArgument argument : arguments)
			if(argument instanceof InvalidCommandArgument)
				return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(language.getName()).append("] ");
		builder.append(function).append(" ").append(argsToString());
		
		return builder.toString();
	}
	
	public static class InputBuilder
	{
		private List<CommandArgument> arguments;
		private String function;
		private Language language;
		private PokedexCommand command;
		
		public InputBuilder()
		{
			arguments = new ArrayList<>();
		}
		
		public InputBuilder setFunction(String function)
		{
			this.function = function;
			return this;
		}
		
		public InputBuilder setLanguage(Language language)
		{
			this.language = language;
			return this;
		}
		
		public InputBuilder setCommand(PokedexCommand command)
		{
			this.command = command;
			return this;
		}
		
		public InputBuilder addArguments(List<CommandArgument> newArguments)
		{
			arguments.addAll(newArguments);
			return this;
		}
		
		public Input build()
		{
			return new Input(this);
		}
	}
	
}
