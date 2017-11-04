package skaro.pokedex.core;

import java.util.HashSet;

import skaro.pokedex.data_processor.ICommand;

public class CommandLibrary 
{
	private HashSet<ICommand> library;
	
	public CommandLibrary()
	{
		library = new HashSet<ICommand>();
	}
	
	public void addToLibrary(ICommand cmd)
	{
		if(library != null)
			library.add(cmd);
	}
	
	public HashSet<ICommand> getLibrary()
	{
		return library;
	}
}
