package skaro.pokedex.communicator;

import java.util.Optional;

import skaro.pokedex.core.ConfigurationService;
import sx.blah.discord.api.IDiscordClient;

public abstract class AbstractPublicationRecipient 
{
	protected IDiscordClient discordClient;
	protected int designatedShardID, totalShards;
	protected String authToken, configID;
	
	public AbstractPublicationRecipient(IDiscordClient client, int shardCount)
	{
		discordClient = client;
		totalShards = shardCount;
	}
	
	public abstract boolean sendPublication(int shardID);
		
	public String getConfigID() { return configID; }
	public boolean configure()
	{
		ConfigurationService configurator;
		Optional<ConfigurationService> configCheck = ConfigurationService.getInstance();
		Optional<String> token;
		
		if(!configCheck.isPresent())
		{
			System.out.println("[PublicationRecipient] Configurations have not been set");
			return false;
		}
		
		configurator = configCheck.get();
		token = configurator.getPublishAuthToken(configID);
		
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
