package skaro.pokedex.data_processor;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;

public class Response 
{
	private StringBuilder textReply;
	private AudioInputStream audioReply;
	private ArrayList<InputStream> imageReply;
	
	public Response()
	{
		textReply = new StringBuilder();
		audioReply = null;
		imageReply = null;
	}
	
	/**
	 * Set and Get Methods
	 */
	public void setPlayBack(AudioInputStream ais)
	{
		audioReply = ais;
	}
	
	public void setImage(String img)
	{
		URL url;
		HttpURLConnection httpCon;
		try 
		{
			url = new URL("http://play.pokemonshowdown.com/sprites/xyani/"+img+".gif");
			httpCon = (HttpURLConnection) url.openConnection(); 
			httpCon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
		    imageReply.add(httpCon.getInputStream());
		    
		    url = new URL("http://play.pokemonshowdown.com/sprites/xyani-shiny/"+img+".gif");
			httpCon = (HttpURLConnection) url.openConnection(); 
			httpCon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
		    imageReply.add(httpCon.getInputStream());
		}
		catch (Exception e) 
		{
			System.out.println(e);
			imageReply = null;
		}
	}
	
	public void addImage(String imagePath)
	{
		URL url;
		HttpURLConnection httpCon;
		
		if(imageReply == null)
			imageReply = new ArrayList<InputStream>();
		
		try 
		{
			url = new URL(imagePath);
			httpCon = (HttpURLConnection) url.openConnection(); 
			httpCon.addRequestProperty("User-Agent", "Mozilla/4.76"); 
		    imageReply.add(httpCon.getInputStream());
		}
		catch (Exception e) 
		{
			System.out.println("[Response] Error getting image:"+e);
			imageReply = null;
		}
	}
	
	public void addToReply(String s)
	{
		textReply.append(s+"<>");
	}
	
	public String getDiscordTextReply()
	{	
		return textReply.toString().replace("<>", "\n");
	}
	
	public String getTwitchTextReply()
	{
		return textReply.toString().replace("<>", " | ");
	}
	
	public AudioInputStream getAudioReply()
	{
		return audioReply;
	}
	
	public ArrayList<InputStream> getImageReply()
	{
		return imageReply;
	}
}
