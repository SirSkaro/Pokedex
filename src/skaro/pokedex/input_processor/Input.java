package skaro.pokedex.input_processor;

import java.util.ArrayList;
import java.util.List;

import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.commands.ArgumentRange;

public class Input 
{
	private List<CommandArgument> arguments;
	private String function;
	private InputErrorStatus errorStatus;
	private Language language;
	private PokedexCommand command;
	
	private Input(InputBuilder inputBuilder)
	{
		this.arguments = inputBuilder.arguments;
		this.function = inputBuilder.function;
		this.language = inputBuilder.language;
		this.command = inputBuilder.command;
		
		if(!hasExpectedNumberOfArguments())
			this.errorStatus = InputErrorStatus.ARGUMENT_NUMBER;
		else
			this.errorStatus = inputBuilder.errorStatus;
	}

	public List<CommandArgument> getArguments() { return arguments; }
	public String getFunction() { return function; }
	public boolean isValid() { return errorStatus == InputErrorStatus.NO_ERROR; }
	public InputErrorStatus getError() { return errorStatus; }
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
	
	public String argsToString()
	{
		if(arguments.isEmpty())
			return "(no input)";
		
		StringBuilder builder = new StringBuilder();
		
		for(CommandArgument arg : arguments)
			builder.append(arg.toString() + ", ");
		
		return builder.substring(0, builder.length() - 2);
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(language.getName()).append("] ");
		builder.append(function).append(" ").append(argsToString());
		
		return builder.toString();
	}
	
	private boolean hasExpectedNumberOfArguments()
	{
		ArgumentRange argumentRange = command.getExpectedArgumentRange();
		int numberOfArguments = arguments.size();
		
		return argumentRange.numberInRange(numberOfArguments);
	}
	
	public static class InputBuilder
	{
		private List<CommandArgument> arguments;
		private String function;
		private InputErrorStatus errorStatus;
		private Language language;
		private PokedexCommand command;
		
		public InputBuilder()
		{
			arguments = new ArrayList<>();
			errorStatus = InputErrorStatus.NO_ERROR;
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
			
			if(anyArgumentsInvalid(newArguments))
				this.errorStatus = InputErrorStatus.INVALID_ARGUMENT;
			
			return this;
		}
		
		public Input build()
		{
			return new Input(this);
		}
		
		private boolean anyArgumentsInvalid(List<CommandArgument> argumentsToCheck)
		{
			return argumentsToCheck.stream()
					.anyMatch(argument -> !argument.isValid());
		}
		
	}
}
