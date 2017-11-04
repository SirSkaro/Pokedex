package skaro.pokedex.input_processor;

import java.util.ArrayList;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import skaro.pokedex.core.CommandLibrary;
import skaro.pokedex.data_processor.ICommand;

@SuppressWarnings("rawtypes")
public class ArgumentMap 
{
	private CacheManager argumentCacheManager, expectedNumberCacheManager;
	private Cache<String, ArrayList> argumentCache;
	private Cache<String, Integer[]> numberCache;
	
	public ArgumentMap(CommandLibrary lib)
	{
		//Argument Cache
		//maps a string to an array list of enumerations. These enumerations described the type of argument(s)
		//to be expected with a command (the key)
		argumentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("argumentCache",
						CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ArrayList.class,
								ResourcePoolsBuilder.heap(50))
						.build())
				.build(true);
		argumentCache = argumentCacheManager.getCache("argumentCache", String.class, ArrayList.class);
		
		//Expected Number Cache
		//maps a string to an array of integers. Index 0 is the minimum number of arguments expected, Index 1
		//is the maximum number of arguments expected
		expectedNumberCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache("numberCache",
						CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Integer[].class,
								ResourcePoolsBuilder.heap(50))
						.build())
				.build(true);
		numberCache = expectedNumberCacheManager.getCache("numberCache", String.class, Integer[].class);
	
		initializeCache(lib);
	}
	
	public ArrayList get(String key)
	{
		return argumentCache.get(key);
	}
	
	public Integer[] getExpectedNumber(String key)
	{
		return numberCache.get(key);
	}
	
	private void initializeCache(CommandLibrary lib)
	{	
		for(ICommand icmd : lib.getLibrary())
		{
			argumentCache.put(icmd.getCommandName(), icmd.getArgumentCats());
			numberCache.put(icmd.getCommandName(), icmd.getExpectedArgNum());
		}
	}
}
