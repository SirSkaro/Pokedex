package skaro.pokedex.services;

import java.util.ArrayList;
import java.util.List;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import skaro.pokedex.data_processor.PokedexCommand;

public class CommandService implements IService
{
	private Cache<String, PokedexCommand> cache;
	private List<PokedexCommand> commands;
	
	public CommandService()
	{
		commands = new ArrayList<>();
		cache = Caffeine.newBuilder()
					.build();
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.COMMAND;
	}
	
	public void addCommand(PokedexCommand command)
	{
		commands.add(command);
		
		cache.put(command.getCommandName(), command);
		for(String alias : command.getAliases().keySet())
			cache.put(alias, command);
	}
	
	public boolean commandOrAliasExists(String cmd)
	{
		return cache.asMap().containsKey(cmd);
	}
	
	public PokedexCommand getByAnyAlias(String key)
	{
		return cache.getIfPresent(key);
	}

	public List<PokedexCommand> getAllCommands()
	{
		return commands;
	}
}