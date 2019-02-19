package skaro.pokedex.communicator;

import java.util.Optional;

import skaro.pokedex.services.ConfigurationService;

public abstract class PublicationRecipient 
{
	protected int designatedShardID;
	protected String authToken, configID;
	
	public abstract boolean sendPublication(int shardID, int totalShards, int connectedGuilds, long botId);
		
	public String getConfigID() 
	{
		return configID; 
	}
	
	public boolean configure(ConfigurationService configurator)
	{
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
	
	protected boolean isDesignatedShard(int shardID)
	{
		return shardID == designatedShardID || designatedShardID == -1;
	}
}
