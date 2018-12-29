package skaro.pokedex.core;

import java.util.concurrent.ExecutorService;

import skaro.pokeflex.api.PokeFlexFactory;

public class PokeFlexService extends PokeFlexFactory implements IService 
{
	public PokeFlexService(String base, ExecutorService customExecutor) 
	{
		super(base, customExecutor);
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.POKE_FLEX;
	}
}
