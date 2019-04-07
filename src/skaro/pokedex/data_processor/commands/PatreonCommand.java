package skaro.pokedex.data_processor.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;

public class PatreonCommand extends PokedexCommand 
{
	private Response staticDiscordReply;
	
	public PatreonCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "patreon".intern();
		staticDiscordReply = new Response();
		aliases.put("donate", Language.ENGLISH);
		
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		EmbedCreateSpec builder = new EmbedCreateSpec();	
		builder.setColor(colorService.getColorForPatreon());
		
		builder.addField("Become a Patron!", "Pledging is the best way to maximize your experience (*perks*) and involvement (*support*) with Pokedex!", false);
		builder.addField("Patreon Link", "[Pokedex's Patreon](https://www.patreon.com/sirskaro)", false);
		
		staticDiscordReply.setEmbed(builder);
		this.createHelpMessage("https://i.imgur.com/Z7U2qkt.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return false; }
	@Override
	public String getArguments() { return "none"; }
	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild){ return Mono.just(staticDiscordReply); }

	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COLOR);
	}
	
	@Override
	protected void createArgumentSpecifications()
	{

	}
	
}
