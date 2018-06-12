package skaro.pokedex.input_processor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import skaro.pokedex.input_processor.arguments.AbstractArgument;

public class Input 
{
	private List<AbstractArgument> args;
	private String function;
	private InputErrorStatus errorStatus;	//0 = no error, 1 = mismatch argument number, 2 = some invalid argument
	
	public Input(String func)
	{
		args = new ArrayList<AbstractArgument>();
		function = func;
		errorStatus = InputErrorStatus.NO_ERROR;
	}
	
	//Get and Set methods
	public List<AbstractArgument> getArgs() { return args; }
	public String getFunction() { return function; }
	public boolean isValid() { return errorStatus == InputErrorStatus.NO_ERROR; }
	public InputErrorStatus getError() { return errorStatus; }
	
	public void setFunction(String function) { this.function = function; }
	public void setErrorStatus(InputErrorStatus status) {this.errorStatus = status; }
	
	//Utility methods	
	public void addArgs(List<AbstractArgument> list)
	{
		args.addAll(list);
	}
	
	public AbstractArgument getArg(int index)
	{
		return args.get(index);
	}
	
	public LinkedList<String> argsAsList()
	{
		LinkedList<String> list = new LinkedList<String>();
		for(AbstractArgument arg : args)
			list.add(arg.getFlexForm());
		
		return list;
	}
}
