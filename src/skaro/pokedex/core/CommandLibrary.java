package skaro.pokedex.core;

import java.util.HashMap;

import skaro.pokedex.data_processor.AbstractCommand;

public class CommandLibrary 
{
	private HashMap<String, AbstractCommand> library;
	
	public CommandLibrary()
	{
		library = new HashMap<String, AbstractCommand>();
	}
	
	public void addToLibrary(AbstractCommand cmd)
	{
		library.put(cmd.getCommandName(), cmd);
	}
	
	public boolean hasCommand(String cmd)
	{
		return library.containsKey(cmd);
	}
	
	public AbstractCommand getCommand(String cmd)
	{
		return library.get(cmd);
	}
	
	public HashMap<String, AbstractCommand> getLibrary()
	{
		return library;
	}
}
