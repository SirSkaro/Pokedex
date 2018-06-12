package skaro.pokedex.input_processor.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParsedText 
{
	private String function;
	private List<String> arguments;
	
	public ParsedText()
	{
		arguments = new ArrayList<String>();
		function = null;
	}
	
	public void setFunction(String func) { function = func.trim().intern(); }
	public void addArg(String arg) { arguments.add(arg); }
	
	public String getFunction() { return function; }
	public List<String> getArguments() { return arguments; }
	
	public void setArgs(String argsList)
	{
		if(argsList.isEmpty())
			return;
			
    	String[] components = argsList.split(",");
    	
    	for(int i = 0; i < components.length; i++)
    		arguments.add(components[i].trim());
	}
	
	public boolean hasArguments() { return arguments.isEmpty(); }
	public int getNumberOfArguments() { return arguments.size(); }
	public Iterator<String> getArgumentIterator() { return arguments.iterator(); }
	
}
