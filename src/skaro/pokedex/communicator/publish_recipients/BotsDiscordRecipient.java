package skaro.pokedex.communicator.publish_recipients;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import skaro.pokedex.communicator.AbstractPublicationRecipient;
import sx.blah.discord.api.IDiscordClient;

public class BotsDiscordRecipient extends AbstractPublicationRecipient 
{
	String endpoint;
			
	public BotsDiscordRecipient(IDiscordClient client, int shardCount) 
	{
		super(client, shardCount);
		configID = "bots_discord";
	}
	
	@Override
	public boolean configure() 
	{
		if(!super.configure())
			return false;
		
		endpoint = "https://bots.discord.pw/api/"+discordClient.getOurUser().getLongID()+"/stats";
		return true;
	}

	@Override
	public boolean sendPublication(int shardID) 
	{
		HttpClient hClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(endpoint);
		JSONObject object = new JSONObject();

		try 
		{
			object.put("shard_id", shardID);
			object.put("shard_count", totalShards);
			object.put("server_count", discordClient.getGuilds().size());
			
			post.setEntity(new StringEntity(object.toString(), "UTF-8"));
			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", authToken);
			hClient.execute(post);
		} 
		catch (Exception e) { return false; }

		return true;
	}

}
