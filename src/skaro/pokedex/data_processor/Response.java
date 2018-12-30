package skaro.pokedex.data_processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioInputStream;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

public class Response
{
	private StringBuilder text;
	private AudioInputStream audio;	
	private File image;				
	private EmbedCreateSpec embed;			
	private boolean privateMessage;
	
	public Response()
	{
		text = new StringBuilder();
		audio = null;
		image = null;
		embed = null;
		privateMessage = false;
	}
	
	public void setPlayBack(AudioInputStream ais)
	{
		audio = ais;
	}
	
	public void setEmbed(EmbedCreateSpec espec)
	{
		embed = espec;
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
		text.append(s+"\n");
	}
	
	public boolean isPrivateMessage()
	{
		return privateMessage;
	}
	
	public AudioInputStream getPlayBack(AudioInputStream ais)
	{
		return audio;
	}
	
	public MessageCreateSpec getAsSpec() throws FileNotFoundException
	{
		return new MessageCreateSpec()
				.setContent(text.toString())
				.setEmbed(embed)
				.setFile(image.getName(), new FileInputStream(image));
	}
	
}
