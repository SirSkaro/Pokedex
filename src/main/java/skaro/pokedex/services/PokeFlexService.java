package skaro.pokedex.services;

import reactor.core.scheduler.Scheduler;
import skaro.pokeflex.api.PokeFlexFactory;

public class PokeFlexService extends PokeFlexFactory implements PokedexService 
{
	private Scheduler threadPool;
	
	public PokeFlexService(String base, Scheduler scheduler) 
	{
		super(base);
		threadPool = scheduler;
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.POKE_FLEX;
	}
	
	public Scheduler getScheduler()
	{
		return threadPool;
	}
}
