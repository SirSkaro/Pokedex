package skaro.pokedex.data_processor.commands;

import java.awt.Color;
import java.util.ArrayList;

import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class DonateCommand implements ICommand 
{
	private static DonateCommand instance;
	private static ArgumentRange expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply;
	
	private DonateCommand()
	{
		commandName = "donate".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(new Color(255,255,255));
		
		builder.appendField("Call for Donations!", "Every little bit helps! _All donations go "
				+ "toward paying for the server space!_", false);
		builder.appendField("Donation link (PayPal)", "[donate here](https://goo.gl/HlrFrD)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new DonateCommand();
		return instance;
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
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
