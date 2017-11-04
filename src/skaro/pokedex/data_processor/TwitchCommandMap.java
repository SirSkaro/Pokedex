package skaro.pokedex.data_processor;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import skaro.pokedex.core.CommandLibrary;

public class TwitchCommandMap 
{
	private CacheManager twitchCacheManager;
	private Cache<String, ICommand> twitchCommandCache;
	
	public TwitchCommandMap(CommandLibrary lib)
	{	
		twitchCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("twitchCommandCache",
						CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ICommand.class,
								ResourcePoolsBuilder.heap(50))
						.build())
				.build(true);

		twitchCommandCache = twitchCacheManager.getCache("twitchCommandCache", String.class, ICommand.class);
		
		initializeCache(lib);
	}
	
	public ICommand get(String key)
	{
		return twitchCommandCache.get(key);
	}
	
	private void initializeCache(CommandLibrary lib)
	{		
		for(ICommand icmd : lib.getLibrary())
			twitchCommandCache.put(icmd.getCommandName(), icmd);
	}
}