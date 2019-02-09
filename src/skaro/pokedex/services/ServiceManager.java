package skaro.pokedex.services;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.core.PokedexManager;

public class ServiceManager implements IServiceManager 
{
	private Map<ServiceType, IService> services;
	
	private ServiceManager(ServiceManagerBuilder builder)
	{
		this.services = new HashMap<>(builder.services);
	}
	
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
	
	public static class ServiceManagerBuilder
	{
		private Map<ServiceType, IService> services;
		private PokedexManager availableServices;
		
		public static ServiceManagerBuilder newInstance(PokedexManager allServices) 
		{ 
			ServiceManagerBuilder builder = new ServiceManagerBuilder(); 
			builder.services = new HashMap<>();
			builder.availableServices = allServices;
			
			return builder;
		}
		
		public ServiceManagerBuilder addService(ServiceType type) throws ServiceException
		{
			if(!availableServices.hasServices(type))
				throw new ServiceException("Service " + type + " not supported");
			
			services.put(type, availableServices.getService(type));
			return this;
		}
		
		public ServiceManagerBuilder removeService(ServiceType type)
		{
			services.remove(type);
			return this;
		}
		
		public ServiceManager build() 
		{
			return new ServiceManager(this);
		}
	}

}
