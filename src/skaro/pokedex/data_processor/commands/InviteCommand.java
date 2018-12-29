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

public class InviteCommand extends AbstractCommand 
{
	private Response staticDiscordReply;
	
	public InviteCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "invite".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		aliases.put("inv", Language.ENGLISH);
		
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(colorService.getPokedexColor());
		
		builder.appendField("Invite Pokdex to your server!", "[Click to invite Pokedex](https://discordapp.com/oauth2/authorize?client_id=206147222746824704&scope=bot&permissions=37080128)", false);
		builder.appendField("Join Pokedex's home server!", "[Click to join Pokedex's server](https://discord.gg/D5CfFkN)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
		this.createHelpMessage("https://i.imgur.com/WoeK9qZ.gif");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "none"; }
	public Response discordReply(Input input, IUser requester) {  return staticDiscordReply; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COLOR);
	}
	
}
