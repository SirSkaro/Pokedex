package skaro.pokedex.data_processor.commands;

import skaro.pokedex.core.ColorService;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class PatreonCommand extends AbstractCommand 
{
	private Response staticDiscordReply;
	
	public PatreonCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "patreon".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		aliases.put("donate", Language.ENGLISH);
		
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(colorService.getColorForPatreon());
		
		builder.appendField("Become a Patron!", "Pledging is the best way to maximize your experience (*perks*) and involvement (*support*) with Pokedex!", false);
		builder.appendField("Patreon Link", "[Pokedex's Patreon](https://www.patreon.com/sirskaro)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
		this.createHelpMessage("https://i.imgur.com/Z7U2qkt.gif");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "none"; }
	public Response discordReply(Input input, IUser requester){ return staticDiscordReply; }

	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COLOR);
	}
	
}
