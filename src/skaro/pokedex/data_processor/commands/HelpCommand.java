package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;

public class HelpCommand implements ICommand 
{
	private static HelpCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply, staticTwitchReply;
	
	private HelpCommand()
	{
		commandName = "help".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new Integer[]{0,0};
		staticDiscordReply = new Response();
		staticTwitchReply = new Response();
		
		staticDiscordReply.addToReply("A short description of every command, along with some examples"
				+ " can be found here: http://pastebin.com/cxhSeuew");
		staticDiscordReply.addToReply("");//line break
		staticDiscordReply.addToReply("***Call for Donations!***");
		staticDiscordReply.addToReply("\tCheck out the %donate command for more info!");
		
		staticTwitchReply.addToReply("A short description of every command, along with some examples"
				+ " can be found here: http://pastebin.com/cxhSeuew");
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new HelpCommand();
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
		return staticTwitchReply;
	}
}
