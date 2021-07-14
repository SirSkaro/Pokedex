package skaro.pokedex.services;

public interface PokedexServiceManager 
{
	public PokedexService getService(ServiceType type);
	public boolean hasServices(ServiceType... serviceTypes);
}
