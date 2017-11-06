package skaro.pokedex.input_processor;

import java.util.ArrayList;

public class Input 
{
	private ArrayList<Argument> args;
	private String function;
	private boolean valid;
	private int errorCode;	//0 = no error, 1 = mismatch argument number, 2 = some invalid argument
	
	public Input(String func)
	{
		args = new ArrayList<Argument>();
		valid = true;		//assumed input is valid until proven false
		function = func;
		errorCode = 0;
	}
	
	//Get and Set methods
	public ArrayList<Argument> getArgs() { return args; }
	public String getFunction() { return function; }
	public boolean isValid() { return valid; }
	public int getError() { return errorCode; }
	
	public void setFunction(String function) { this.function = function; }
	public void setValid(boolean b) { this.valid = b; }
	public void setError(int i) {this.errorCode = i; }
	
	//Utility methods
	public void addArg(Argument arg)
	{
		args.add(arg);
	}
	
	public Argument getArg(int index)
	{
		if(index > -1 && index < args.size())
			return args.get(index);
		return null;
	}
	
	@Override
	public String toString() {
		return "Input: [Function=" + function + ", valid=" + valid + ",errorCode=" + errorCode + "args=" + args.toString() + "]";
	}
}
