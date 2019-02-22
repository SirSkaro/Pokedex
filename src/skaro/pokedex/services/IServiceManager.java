package skaro.pokedex.services;

public interface IServiceManager 
{
	public IService getService(ServiceType type);
	public boolean hasServices(ServiceType... serviceTypes);
}
