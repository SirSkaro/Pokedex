package skaro.pokedex.communicator.publish_recipients;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import skaro.pokedex.services.ServiceConsumerException;

public class BotsDiscordRecipient extends PublicationRecipient 
{
	public BotsDiscordRecipient(RecipientConfig config) throws ServiceConsumerException {
		super(config);
	}
	
	@Override
	public boolean sendPublication(int connectedGuilds, long botId) 
	{
		String endpoint = "https://bots.discord.pw/api/"+botId+"/stats";
		HttpClient hClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(endpoint);
		JSONObject object = new JSONObject();
		
		try 
		{
			object.put("shard_id", config.designatedShard);
			object.put("shard_count", config.totalShards);
			object.put("server_count", connectedGuilds);
			
			post.setEntity(new StringEntity(object.toString(), "UTF-8"));
			post.addHeader("Content-type", "application/json");
			post.addHeader("Authorization", config.token);
			hClient.execute(post);
		} 
		catch (Exception e) { return false; }

		return true;
	}

}
