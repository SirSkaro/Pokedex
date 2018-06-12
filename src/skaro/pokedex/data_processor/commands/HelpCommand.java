package skaro.pokedex.data_processor.commands;

import java.awt.Color;
import java.util.ArrayList;

import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommand implements ICommand 
{
	private static HelpCommand instance;
	private static ArgumentRange expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply;
	
	private HelpCommand()
	{
		commandName = "help".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(new Color(255,255,255));
		
		builder.appendField("Examples", "[click here for examples of every command]"
				+ "(http://pastebin.com/cxhSeuew)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new HelpCommand();
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
	
	public Response discordReply(Input input)
	{ 
		return staticDiscordReply;
	}
}
