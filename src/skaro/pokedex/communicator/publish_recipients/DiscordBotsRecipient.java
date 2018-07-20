package skaro.pokedex.communicator.publish_recipients;

import org.discordbots.api.client.DiscordBotListAPI;

import skaro.pokedex.communicator.AbstractPublicationRecipient;
import sx.blah.discord.api.IDiscordClient;

public class DiscordBotsRecipient extends AbstractPublicationRecipient 
{
	DiscordBotListAPI dblClient;
	
	public DiscordBotsRecipient(IDiscordClient client, int shardCount) 
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
                .botId(Long.toString(discordClient.getOurUser().getLongID()))
                .build();
		
		return true;
	}

	@Override
	public boolean sendPublication(int shardID) 
	{
		dblClient.setStats(shardID, totalShards, discordClient.getGuilds().size());
		return true;
	}
}
