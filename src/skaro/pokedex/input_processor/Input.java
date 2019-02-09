package skaro.pokedex.input_processor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import skaro.pokedex.data_processor.PokedexCommand;

public class Input 
{
	private List<CommandArgument> args;
	private String function;
	private InputErrorStatus errorStatus;
	private Language lang;
	private PokedexCommand command;
	
	public Input(String func, PokedexCommand cmd, Language l)
	{
		args = new ArrayList<CommandArgument>();
		function = func;
		errorStatus = InputErrorStatus.NO_ERROR;
		lang = l;
		command = cmd;
	}
	
	//Get and Set methods
	public List<CommandArgument> getArgs() { return args; }
	public String getFunction() { return function; }
	public boolean isValid() { return errorStatus == InputErrorStatus.NO_ERROR; }
	public InputErrorStatus getError() { return errorStatus; }
	public Language getLanguage() { return lang; }
	public PokedexCommand getCommand() { return command; }
	
	public void setFunction(String function) { this.function = function; }
	public void setErrorStatus(InputErrorStatus status) {this.errorStatus = status; }
	
	//Utility methods	
	public void addArgs(List<CommandArgument> list)
	{
		args.addAll(list);
	}
	
	public CommandArgument getArg(int index)
	{
		return args.get(index);
	}
	
	public LinkedList<String> argsAsList()
	{
		LinkedList<String> list = new LinkedList<String>();
		for(CommandArgument arg : args)
			list.add(arg.getFlexForm());
		
		return list;
	}
	
	public String argsToString()
	{
		if(args.isEmpty())
			return "(no input)";
		
		StringBuilder builder = new StringBuilder();
		
		for(CommandArgument arg : args)
			builder.append(arg.toString() + ", ");
		
		return builder.substring(0, builder.length() - 2);
	}
}
