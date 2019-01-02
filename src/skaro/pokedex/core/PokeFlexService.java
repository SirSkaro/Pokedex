package skaro.pokedex.core;

import skaro.pokeflex.api.PokeFlexFactory;

public class PokeFlexService extends PokeFlexFactory implements IService 
{
	public PokeFlexService(String base) 
	{
		super(base);
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.POKE_FLEX;
	}
}
