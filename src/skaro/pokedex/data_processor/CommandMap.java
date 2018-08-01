package skaro.pokedex.data_processor;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import skaro.pokedex.core.CommandLibrary;

public class CommandMap 
{
	private Cache<String, AbstractCommand> discordCommandCache;
	
	public CommandMap(CommandLibrary lib)
	{
		CacheManager discordCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("discordCommandCache",
						CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, AbstractCommand.class,
								ResourcePoolsBuilder.heap(50))
						.build())
				.build(true);

		discordCommandCache = discordCacheManager.getCache("discordCommandCache", String.class, AbstractCommand.class);
		
		initializeCache(lib);
	}
	
	public AbstractCommand get(String key)
	{
		return discordCommandCache.get(key);
	}
	
	private void initializeCache(CommandLibrary lib)
	{		
		for(AbstractCommand icmd : lib.getLibrary().values())
			discordCommandCache.put(icmd.getCommandName(), icmd);
	}
}