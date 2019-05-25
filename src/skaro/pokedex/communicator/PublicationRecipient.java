package skaro.pokedex.communicator;

import java.util.Optional;

import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceManager;
import skaro.pokedex.services.ServiceType;

public abstract class PublicationRecipient implements PokedexServiceConsumer
{
	protected int designatedShardID;
	protected String authToken, configID;
	protected ServiceManager services;
	
	public PublicationRecipient(ServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	public abstract boolean sendPublication(int shardID, int totalShards, int connectedGuilds, long botId);
		
	public String getConfigID() 
	{
		return configID; 
	}
	
	public boolean configureIfSupported()
	{
		ConfigurationService configurator = (ConfigurationService)services.getService(ServiceType.CONFIG);
		Optional<String> token = configurator.getPublishAuthToken(configID);
		
		if(!token.isPresent())
		{
			System.out.println("[PublicationRecipient] No token for authentication");
			return false;
		}
		
		authToken = token.get();
		designatedShardID = configurator.getPublishDesignatedShard(configID);
		return true;
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services)
	{
		return services.hasServices(ServiceType.CONFIG);
	}
	
	protected boolean isDesignatedShard(int shardID)
	{
		return shardID == designatedShardID || designatedShardID == -1;
	}
}
