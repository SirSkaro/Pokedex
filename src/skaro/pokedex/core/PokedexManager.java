package skaro.pokedex.core;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.data_processor.ColorService;
import skaro.pokedex.data_processor.CommandMap;
import skaro.pokedex.data_processor.EmojiService;

public enum PokedexManager implements IServiceManager
{
	INSTANCE;
	
	private Map<ServiceType, IService> services;	
	private boolean initialized = false;
	
	public IService getService(ServiceType service) throws ServiceException
	{
		if(!services.containsKey(service))
			throw new ServiceException("Service not set up or included");
		
		return services.get(service);
	}
	
	private void build(PokedexConfigurator builder)
	{
		if(initialized)
			throw new IllegalStateException("Pokedex application already configued!");
		
		this.services = builder.services;
		initialized = true;
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
			INSTANCE.build(this);
			return INSTANCE;
		}
		
		public PokedexConfigurator withConfigurationService(ConfigurationService service)
		{
			services.put(ServiceType.CONFIG, service);
			return this;
		}
		
		public PokedexConfigurator withCommandService(CommandMap service)
		{
			services.put(ServiceType.COMMAND, service);
			return this;
		}
		
		public PokedexConfigurator withColorService(ColorService service)
		{
			services.put(ServiceType.COLOR, service);
			return this;
		}
		
		public PokedexConfigurator withEmojiService(EmojiService service)
		{
			services.put(ServiceType.EMOJI, service);
			return this;
		}
		
		public PokedexConfigurator withDiscordService(DiscordService service)
		{
			services.put(ServiceType.DISCORD, service);
			return this;
		}
		
		public PokedexConfigurator buildPatreonClient(PerkChecker service)
		{
			services.put(ServiceType.PERK, service);
			return this;
		}
		
		public PokedexConfigurator withPokeFlexService(PokeFlexService service)
		{
			services.put(ServiceType.POKE_FLEX, service);
			return this;
		}
	}
}
