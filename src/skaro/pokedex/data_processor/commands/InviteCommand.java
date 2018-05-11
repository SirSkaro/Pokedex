package skaro.pokedex.data_processor.commands;

import java.awt.Color;
import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class InviteCommand implements ICommand 
{
	private static InviteCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply;
	
	private InviteCommand()
	{
		commandName = "invite".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new Integer[]{0,0};
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(new Color(255,255,255));
		
		builder.appendField("Invite Pokdex to your server!", "[Click to invite Pokedex](https://discordapp.com/oauth2/authorize?client_id=206147222746824704&scope=bot&permissions=2165760)", false);
		builder.appendField("Join Pokedex's home server!", "[Click to join Pokedex's server](https://discord.gg/D5CfFkN)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new InviteCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
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
	
	public Response twitchReply(Input input)
	{ 
		return null;
	}
}
