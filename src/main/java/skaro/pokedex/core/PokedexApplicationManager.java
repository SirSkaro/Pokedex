package skaro.pokedex.core;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.services.PokedexService;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceType;

public class PokedexApplicationManager implements PokedexServiceManager
{
	private Map<ServiceType, PokedexService> services;	
	
	@Override
	public PokedexService getService(ServiceType service) 
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
		private Map<ServiceType, PokedexService> services;
		
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
		
		public PokedexConfigurator withService(PokedexService service)
		{
			services.put(service.getServiceType(), service);
			return this;
		}
	}
	
}
