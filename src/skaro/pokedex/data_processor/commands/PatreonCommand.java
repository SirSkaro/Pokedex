package skaro.pokedex.data_processor.commands;

import java.awt.Color;
import java.util.ArrayList;

import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class PatreonCommand implements ICommand 
{
	private ArgumentRange expectedArgRange;
	private String commandName;
	private ArrayList<ArgumentCategory> argCats;
	private Response staticDiscordReply;
	
	public PatreonCommand()
	{
		commandName = "patreon".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(new Color(0xF96854));
		
		builder.appendField("Become a Patron!", "Pledging is the best way to maximize your experience (*perks*) and involvement (*support*) with Pokedex!", false);
		builder.appendField("Patreon Link", "[Pokedex's Patreon](https://www.patreon.com/sirskaro)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	public boolean makesWebRequest() { return false; }
	
	public String getArguments()
	{
		return "none";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		return true;
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		return staticDiscordReply;
	}
}
