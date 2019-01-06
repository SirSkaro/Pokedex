package skaro.pokedex.data_processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

public class Response
{
	private StringBuilder text;
	private AudioInputStream audio;	
	private Optional<File> image;				
	private EmbedCreateSpec embed;	
	private boolean privateMessage;
	
	public Response()
	{
		text = new StringBuilder();
		audio = null;
		image = Optional.empty();
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
		image = Optional.of(file);
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
	
	public Mono<MessageCreateSpec> getAsSpec()
	{
		MessageCreateSpec resultSpec = new MessageCreateSpec()
				.setContent(text.toString())
				.setEmbed(embed);
		
		Mono<MessageCreateSpec> result = Mono.just(resultSpec);
		
		if(image.isPresent())
		{
			result = result.doOnNext(spec -> {
				try { spec.setFile(image.get().getName(), new FileInputStream(image.get())); } 
				catch (FileNotFoundException e) { throw Exceptions.propagate(e); }
			});
		}

		return result;
	}
	
}
