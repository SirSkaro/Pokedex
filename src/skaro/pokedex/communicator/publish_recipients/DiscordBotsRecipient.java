package skaro.pokedex.communicator.publish_recipients;

import org.discordbots.api.client.DiscordBotListAPI;

import skaro.pokedex.communicator.PublicationRecipient;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceManager;

public class DiscordBotsRecipient extends PublicationRecipient 
{
	public DiscordBotsRecipient(ServiceManager services) throws ServiceConsumerException
	{
		super(services);
		configID = "discord_bots";
	}
	
	@Override
	public boolean sendPublication(int shardID, int totalShards, int connectedGuilds, long botId) 
	{
		DiscordBotListAPI dblClient = createDiscordBotListClient(botId);
		
		dblClient.setStats(shardID, totalShards, connectedGuilds);
		return true;
	}
	
	private DiscordBotListAPI createDiscordBotListClient(long botId)
	{
		return new DiscordBotListAPI.Builder()
                .token(authToken)
                .botId(Long.toString(botId))
                .build();
	}
}
