package skaro.pokedex.data_processor;

import java.util.List;

import skaro.pokedex.core.FlexCache;
import skaro.pokedex.core.FlexCache.CachedResource;
import skaro.pokedex.core.IService;
import skaro.pokedex.core.IServiceConsumer;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.TypeEfficacyWrapper.EfficacyCategory;
import skaro.pokedex.data_processor.TypeEfficacyWrapper.EfficacyInteractionBuilder;

public class TypeService implements IService, IServiceConsumer
{	 		
	IServiceManager services;
	
	@Override
	public boolean hasExpectedServices(IServiceManager services)
	{
		return services.hasServices(ServiceType.CACHE);
	}
	    			
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.TYPE;
	}
	
	public void setServiceManager(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
    public TypeEfficacyWrapper getEfficacyOnDefense(List<String> typeNames)
    {
    	return buildEfficacyWrapper(EfficacyCategory.DEFENSE, typeNames);
    }
    
    public TypeEfficacyWrapper getEfficacyOnOffense(List<String> typeNames)
    {
    	return buildEfficacyWrapper(EfficacyCategory.OFFENSE, typeNames);
    }
    
    private TypeEfficacyWrapper buildEfficacyWrapper(EfficacyCategory whichEfficacy, List<String> typeNames)
    {
    	FlexCache cache = (FlexCache)services.getService(ServiceType.CACHE);
    	TypeData cachedTypeData = (TypeData)cache.getCachedData(CachedResource.TYPE);
    	EfficacyInteractionBuilder builder = TypeEfficacyWrapper
    			.EfficacyInteractionBuilder
    			.newInstance()
    			.withEfficacyCategory(whichEfficacy)
    			.addTypesToCheckAgainst(cachedTypeData.getAllTypes());
    	
    	for(String typeName : typeNames)
    		builder = builder.addType(cachedTypeData.getByName(typeName));
    	
    	return builder.build();
    }
}