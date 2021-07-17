package skaro.pokedex.communicator.publish_recipients;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import skaro.pokedex.services.ServiceConsumerException;

public class CarbonitexRecipient extends PublicationRecipient 
{
	public CarbonitexRecipient(RecipientConfig config) throws ServiceConsumerException {
		super(config);
	}
	
	@Override
	public boolean sendPublication(int connectedGuilds, long botId, int shardIndex) 
	{
    	HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://www.carbonitex.net/discord/data/botdata.php/");
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
		
    	try  {
    		params.add(new BasicNameValuePair("key", config.token));
    		params.add(new BasicNameValuePair("servercount", Integer.toString(config.totalShards * (connectedGuilds/config.designatedShards.length))));
    		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    		httpclient.execute(httppost);
					    
		    return true;
		} catch(Exception e) { 
			return false; 
		}
	}
}
