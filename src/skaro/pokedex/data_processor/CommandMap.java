package skaro.pokedex.data_processor;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CommandMap 
{
	private Cache<String, AbstractCommand> cache;
	
	public CommandMap(List<AbstractCommand> commands, ExecutorService threadPool)
	{
		int cacheSize = getCacheEntrySize(commands);
		
		cache = Caffeine.newBuilder()
					.maximumSize(cacheSize)
					.executor(threadPool)
					.build();
				
		initializeCache(commands);
	}
	
	public boolean hasCommand(String cmd)
	{
		return cache.asMap().containsKey(cmd);
	}
	
	public AbstractCommand get(String key)
	{
		return cache.getIfPresent(key);
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