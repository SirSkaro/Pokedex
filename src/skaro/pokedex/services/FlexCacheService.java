package skaro.pokedex.services;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.core.ICachedData;
import skaro.pokeflex.api.IFlexObject;

public class FlexCacheService implements IService
{
	private Map<CachedResource, ICachedData> cache;
	
	public FlexCacheService()
	{
		cache = new HashMap<>();
	}
	
	@Override
	public ServiceType getServiceType()
	{
		return ServiceType.CACHE;
	}
	
	public IFlexObject getCachedData(CachedResource whichResource, String whichData)
	{
		return cache.get(whichResource).getByName(whichData);
	}
	
	public ICachedData getCachedData(CachedResource whichResource)
	{
		return cache.get(whichResource);
	}
	
	public void addCachedResource(CachedResource resource, ICachedData cachedData)
	{
		cache.put(resource, cachedData);
	}

	public enum CachedResource
	{
		LEARN_METHOD,
		TYPE,
	}
}
