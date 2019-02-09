package skaro.pokedex.core;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.services.IService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceType;

public class PokedexApplicationManager implements IServiceManager
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
	
	private PokedexApplicationManager(PokedexConfigurator builder)
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
		
		public PokedexApplicationManager configure()
		{
			return new PokedexApplicationManager(this);
		}
		
		public PokedexConfigurator withService(IService service)
		{
			services.put(service.getServiceType(), service);
			return this;
		}
	}
	
}
