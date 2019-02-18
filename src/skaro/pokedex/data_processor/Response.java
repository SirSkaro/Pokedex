package skaro.pokedex.data_processor;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;

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
	private Optional<EmbedCreateSpec> embed;	
	private boolean privateMessage;
	
	public Response()
	{
		text = new StringBuilder();
		audio = null;
		image = Optional.empty();
		embed = Optional.empty();
		privateMessage = false;
	}
	
	public void setPlayBack(AudioInputStream ais)
	{
		audio = ais;
	}
	
	public void setEmbed(EmbedCreateSpec espec)
	{
		embed = Optional.of(espec);
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
	
	public boolean hasPlayback()
	{
		return audio != null;
	}
	
	public AudioInputStream getPlayback()
	{
		return audio;
	}
	
	public Mono<Consumer<MessageCreateSpec>> getAsSpec()
    {
        Consumer<MessageCreateSpec> resultSpec = spec -> spec.setContent(text.toString());
        
        if(embed.isPresent())
            //resultSpec = resultSpec.andThen(spec -> spec.setEmbed(unwrapEmbed()));
        	resultSpec = resultSpec.andThen(spec -> setEmbedViaReflection(spec));
        
        Mono<Consumer<MessageCreateSpec>> result = Mono.just(resultSpec);
        
        if(!image.isPresent())
            return result;
        
        return result.doOnNext(spec -> spec.andThen(s -> {
					try { s.addFile(image.get().getName(), new FileInputStream(image.get())); } 
					catch (Exception e) { throw Exceptions.propagate(e); }
				}));
    }

	private void setEmbedViaReflection(MessageCreateSpec spec)
	{
		try
		{
			Field field = spec.getClass().getDeclaredField("embed");
			EmbedCreateSpec mySpec = embed.get();
			field.setAccessible(true);
			field.set(spec, mySpec.asRequest());
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("no reflection for you");
		}
		
	}
	
}
