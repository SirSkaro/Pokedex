package skaro.pokedex.core;

import java.util.HashMap;

import skaro.pokedex.data_processor.commands.ICommand;

public class CommandLibrary 
{
	private HashMap<String, ICommand> library;
	
	public CommandLibrary()
	{
		library = new HashMap<String, ICommand>();
	}
	
	public void addToLibrary(ICommand cmd)
	{
		library.put(cmd.getCommandName(), cmd);
	}
	
	public boolean hasCommand(String cmd)
	{
		return library.containsKey(cmd);
	}
	
	public ICommand getCommand(String cmd)
	{
		return library.get(cmd);
	}
	
	public HashMap<String, ICommand> getLibrary()
	{
		return library;
	}
}
