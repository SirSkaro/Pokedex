package skaro.pokedex.data_processor;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import skaro.pokedex.core.IService;
import skaro.pokedex.core.ServiceType;

public class CommandService implements IService
{
	private Cache<String, AbstractCommand> cache;
	
	public CommandService(ExecutorService threadPool)
	{
		cache = Caffeine.newBuilder()
					.executor(threadPool)
					.build();
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.COMMAND;
	}
	
	public void addCommand(AbstractCommand command)
	{
		cache.put(command.getCommandName(), command);
		for(String alias : command.getAliases().keySet())
			cache.put(alias, command);
	}
	
	public boolean hasCommand(String cmd)
	{
		return cache.asMap().containsKey(cmd);
	}
	
	public AbstractCommand get(String key)
	{
		return cache.getIfPresent(key);
	}

	public Map<String, AbstractCommand> getCacheAsMap()
	{
		return cache.asMap();
	}
}