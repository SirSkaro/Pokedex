package skaro.pokedex.data_processor;

import skaro.pokedex.core.FlexCache;
import skaro.pokedex.core.FlexCache.CachedResource;
import skaro.pokedex.core.IService;
import skaro.pokedex.core.IServiceConsumer;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.TypeEfficacyWrapper.EfficacyInteractionBuilder;
import skaro.pokedex.data_processor.TypeEfficacyWrapper.EfficacyOption;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.type.Type;

public class TypeService implements IService, IServiceConsumer
{	 		
	IServiceManager services;
	
	public TypeService(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
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
	
    public String getNameInLanguage(String typeName, Language lang)
    {
    	FlexCache cache = (FlexCache)services.getService(ServiceType.CACHE);
    	Type type = (Type)cache.getCachedData(CachedResource.TYPE, typeName);
    	return TextFormatter.flexFormToProper(type.getNameInLanguage(lang.getFlexKey()));
    }
    
    public TypeEfficacyWrapper getEfficacyOnDefense(String primaryTypeName)
    {
    	return buildEfficacyWrapper(EfficacyOption.DEFENSE, primaryTypeName);
    }
    
    public TypeEfficacyWrapper getEfficacyOnDefense(String primaryTypeName, String secondaryTypeName)
    {
    	return buildEfficacyWrapper(EfficacyOption.DEFENSE, primaryTypeName, secondaryTypeName);
    }
    
    public TypeEfficacyWrapper getEfficacyOnOffense(String... typeNames)
    {
    	return buildEfficacyWrapper(EfficacyOption.OFFENSE, typeNames);
    }
    
    private TypeEfficacyWrapper buildEfficacyWrapper(EfficacyOption whichEfficacy, String... typeNames)
    {
    	FlexCache cache = (FlexCache)services.getService(ServiceType.CACHE);
    	TypeData cachedTypeData = (TypeData)cache.getCachedData(CachedResource.TYPE);
    	EfficacyInteractionBuilder builder = TypeEfficacyWrapper
    			.EfficacyInteractionBuilder
    			.newInstance(EfficacyOption.DEFENSE)
    			.addTypesToCheckAgainst(cachedTypeData.getAllTypes());
    	
    	for(String typeName : typeNames)
    		builder = builder.addType(cachedTypeData.getByName(typeName));
    	
    	return builder.build();
    }
}