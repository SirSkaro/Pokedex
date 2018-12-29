package skaro.pokedex.core;

import java.util.HashMap;
import java.util.Map;

public class PokedexManager implements IServiceManager
{
	private Map<ServiceType, IService> services;	
	
	@Override
	public IService getService(ServiceType service) 
	{
		return services.get(service);
	}
	
	@Override
	public boolean hasServices(ServiceType... services)
	{
		for(ServiceType service: services)
			if(!this.services.containsKey(service))
				return false;
		return true;
	}
	
	private PokedexManager(PokedexConfigurator builder)
	{
		this.services = builder.services;
	}
	
	public static class PokedexConfigurator 
	{
		private Map<ServiceType, IService> services;
		
		public static PokedexConfigurator newInstance() 
		{ 
			PokedexConfigurator builder = new PokedexConfigurator(); 
			builder.services = new HashMap<>();
			
			return builder;
		}
		
		public PokedexManager configure()
		{
			return new PokedexManager(this);
		}
		
		public PokedexConfigurator withService(IService service)
		{
			services.put(service.getServiceType(), service);
			return this;
		}
	}
	
}
