package skaro.pokedex.communicator.publish_recipients;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import skaro.pokedex.communicator.AbstractPublicationRecipient;
import sx.blah.discord.api.IDiscordClient;

public class CarbonitexRecipient extends AbstractPublicationRecipient 
{
	public CarbonitexRecipient(IDiscordClient client, int shardCount)
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
		HttpResponse response;
		HttpEntity entity;
		InputStream instream;
		BufferedReader reader;
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
		StringBuilder result = new StringBuilder();
		String line;
		
    	try 
    	{
    		// Request parameters and other properties.
    		params.add(new BasicNameValuePair("key", authToken));
    		params.add(new BasicNameValuePair("servercount", Integer.toString(totalShards * discordClient.getGuilds().size())));
    		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    		response = httpclient.execute(httppost);
			params.remove(1);
			
			entity = response.getEntity();
			if(entity == null) 
				return false;
			
			instream = entity.getContent();
		    reader = new BufferedReader(new InputStreamReader(instream));
		    
		    while((line = reader.readLine()) != null) 
		        result.append(line);
		    
		    reader.close();
		    instream.close();
		    
		    return true;
		}
    	catch(Exception e) { return false; }
	}
}
