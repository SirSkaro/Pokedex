package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;

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
		
		staticDiscordReply.addToReply("To invite to bot to your own server, click here:");
		staticDiscordReply.addToReply("\thttps://goo.gl/Mm5pU7");
		staticDiscordReply.addToReply("");
		staticDiscordReply.addToReply("To join Pokedex's home server (report bugs, make requests, chill), click here:");
		staticDiscordReply.addToReply("\thttps://discord.gg/D5CfFkN");
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
