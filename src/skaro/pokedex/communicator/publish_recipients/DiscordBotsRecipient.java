package skaro.pokedex.communicator.publish_recipients;

import org.discordbots.api.client.DiscordBotListAPI;

import discord4j.core.DiscordClient;
import skaro.pokedex.communicator.AbstractPublicationRecipient;

public class DiscordBotsRecipient extends AbstractPublicationRecipient 
{
	DiscordBotListAPI dblClient;
	
	public DiscordBotsRecipient(DiscordClient client, int shardCount) 
	{
		super(client, shardCount);
		configID = "discord_bots";
	}
	
	@Override
	public boolean configure() 
	{
		if(!super.configure())
			return false;
		
		dblClient = new DiscordBotListAPI.Builder()
                .token(authToken)
                .botId(Long.toString(this.getBotId()))
                .build();
		
		return true;
	}

	@Override
	public boolean sendPublication(int shardID) 
	{
		dblClient.setStats(shardID, totalShards, this.getNumberOfConnectedGuilds());
		return true;
	}
}
