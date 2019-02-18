package skaro.pokedex.communicator.publish_recipients;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import discord4j.core.DiscordClient;
import skaro.pokedex.communicator.AbstractPublicationRecipient;

public class CarbonitexRecipient extends AbstractPublicationRecipient 
{
	public CarbonitexRecipient(DiscordClient client, int shardCount)
	{
		super(client, shardCount);
		configID = "carbonitex";
	}
	
	@Override
	public boolean sendPublication(int shardID) 
	{
		//Utility variables
    	HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://www.carbonitex.net/discord/data/botdata.php/");
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
		
    	try 
    	{
    		// Request parameters and other properties.
    		params.add(new BasicNameValuePair("key", authToken));
    		params.add(new BasicNameValuePair("servercount", Integer.toString(totalShards * this.getNumberOfConnectedGuilds())));
    		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    		httpclient.execute(httppost);
					    
		    return true;
		}
    	catch(Exception e) { return false; }
	}
}
