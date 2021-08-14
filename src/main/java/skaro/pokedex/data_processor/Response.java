package skaro.pokedex.data_processor;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

public class Response
{
	public StringBuilder text;
	public Optional<InputStream> image;
	public Optional<String> imageName;
	public Optional<EmbedCreateSpec> embed;	
	public boolean privateMessage;
	
	public Response() {
		text = new StringBuilder();
		image = Optional.empty();
		embed = Optional.empty();
		privateMessage = false;
	}
	
	public void setEmbed(EmbedCreateSpec espec) {
		embed = Optional.of(espec);
	}
	
	public void setPrivate(boolean b) {
		privateMessage = b;
	}
	
	public void addImage(String imageName, InputStream file) {
		this.image = Optional.of(file);
		this.imageName = Optional.of(imageName);
	}
	
	public void addToReply(String s) {
		text.append(s+"\n");
	}
	
	public boolean isPrivateMessage() {
		return privateMessage;
	}
	
	public Mono<Consumer<MessageCreateSpec>> getAsSpec() {
        Consumer<MessageCreateSpec> resultSpec = spec -> spec.setContent(text.toString());
        
        if(embed.isPresent()) {
        	resultSpec = resultSpec.andThen(spec -> setEmbedViaReflection(spec));
        }
        
        Mono<Consumer<MessageCreateSpec>> result = Mono.just(resultSpec);
        
        if(!image.isPresent())
            return result;
        
        return result.map(spec -> spec.andThen(s -> {
					try { s.addFile(imageName.get(), image.get()); } 
					catch (Exception e) { throw Exceptions.propagate(e); }
				}));
    }

	private void setEmbedViaReflection(MessageCreateSpec spec)
	{
		try
		{
			Field field = spec.getClass().getDeclaredField("embeds");
			EmbedCreateSpec mySpec = embed.get();
			field.setAccessible(true);
			field.set(spec, List.of(mySpec.asRequest()));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("no reflection for you");
		}
	}
	
}
