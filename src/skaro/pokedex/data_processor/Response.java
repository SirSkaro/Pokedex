package skaro.pokedex.data_processor;

import java.io.File;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;

import sx.blah.discord.api.internal.json.objects.EmbedObject;

public class Response
{
	private StringBuilder text;
	private AudioInputStream audio;	
	private File image;				
	private EmbedObject embed;			
	private boolean privateMessage;
	
	public Response()
	{
		text = new StringBuilder();
		audio = null;
		image = null;
		embed = null;
		privateMessage = false;
	}
	
	/**
	 * Set and Get Methods
	 */
	public void setPlayBack(AudioInputStream ais)
	{
		audio = ais;
	}
	
	public void setEmbededReply(EmbedObject eo)
	{
		embed = eo;
	}
	
	public void setPrivate(boolean b)
	{
		privateMessage = b;
	}
	
	public void addImage(File file)
	{
		image = file;
	}
	
	public void addToReply(String s)
	{
		text.append(s+"<>");
	}
	
	public String getDiscordTextReply()
	{	
		return text.toString().replace("<>", "\n");
	}
	
	public String getTwitchTextReply()
	{
		return text.toString().replace("<>", " | ");
	}
	
	public AudioInputStream getAudioReply()
	{
		return audio;
	}
	
	public Optional<File> getImage()
	{
		return Optional.ofNullable(image);
	}
	
	public Optional<EmbedObject> getEmbedObject()
	{
		return Optional.ofNullable(embed);
	}
	
	public boolean isPrivateMessage()
	{
		return privateMessage;
	}
}
