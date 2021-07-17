package skaro.pokedex.communicator.publish_recipients;

import org.discordbots.api.client.DiscordBotListAPI;

import skaro.pokedex.services.ServiceConsumerException;

public class DiscordBotsRecipient extends PublicationRecipient 
{
	public DiscordBotsRecipient(RecipientConfig config) throws ServiceConsumerException {
		super(config);
	}
	
	@Override
	public boolean sendPublication(int connectedGuilds, long botId, int shardIndex) {
		DiscordBotListAPI dblClient = createDiscordBotListClient(botId);
		
		dblClient.setStats(shardIndex, config.totalShards, connectedGuilds);
		return true;
	}
	
	private DiscordBotListAPI createDiscordBotListClient(long botId) {
		return new DiscordBotListAPI.Builder()
                .token(config.token)
                .botId(Long.toString(botId))
                .build();
	}
}
