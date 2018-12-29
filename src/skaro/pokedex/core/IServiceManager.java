package skaro.pokedex.core;

public interface IServiceManager 
{
	public IService getService(ServiceType type);
	public boolean hasServices(ServiceType... serviceTypes);
}
