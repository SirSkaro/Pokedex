package skaro.pokedex.services;

import java.util.Map;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import skaro.pokedex.data_processor.PokedexCommand;

public class CommandService implements IService
{
	private Cache<String, PokedexCommand> cache;
	
	public CommandService()
	{
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
		cache.put(command.getCommandName(), command);
		for(String alias : command.getAliases().keySet())
			cache.put(alias, command);
	}
	
	public boolean hasCommand(String cmd)
	{
		return cache.asMap().containsKey(cmd);
	}
	
	public PokedexCommand get(String key)
	{
		return cache.getIfPresent(key);
	}

	public Map<String, PokedexCommand> getCacheAsMap()
	{
		return cache.asMap();
	}
}