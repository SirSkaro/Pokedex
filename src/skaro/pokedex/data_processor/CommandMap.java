package skaro.pokedex.data_processor;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;

public class CommandMap 
{
	private UserManagedCache<String, AbstractCommand> cache;
	
	public CommandMap(List<AbstractCommand> commands, ExecutorService threadPool)
	{
		int cacheSize = getCacheEntrySize(commands);
		
		cache = UserManagedCacheBuilder.newUserManagedCacheBuilder(String.class, AbstractCommand.class)
				.withEventExecutors(threadPool, threadPool)
				.withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(cacheSize, EntryUnit.ENTRIES))
				.build(true);
		
		initializeCache(commands);
	}
	
	public boolean hasCommand(String cmd)
	{
		return cache.containsKey(cmd);
	}
	
	public AbstractCommand get(String key)
	{
		return cache.get(key);
	}
	
	private int getCacheEntrySize(List<AbstractCommand> commands)
	{
		int result = 0;
		
		for(AbstractCommand command : commands)
		{
			result++;
			result += command.getAliases().size();
		}
		
		return result;
	}
	
	private void initializeCache(List<AbstractCommand> commands)
	{		
		for(AbstractCommand command : commands)
		{
			cache.put(command.getCommandName(), command);
			for(String alias : command.getAliases().keySet())
				cache.put(alias, command);
		}
	}
}