package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;

public class DonateCommand implements ICommand 
{
	private static DonateCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply, staticTwitchReply;
	
	private DonateCommand()
	{
		commandName = "donate".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new Integer[]{0,0};
		staticDiscordReply = new Response();
		staticTwitchReply = new Response();
		
		staticDiscordReply.addToReply("```Call for Donations!```");
		staticDiscordReply.addToReply("Hello everyone, Sir Skaro here. I wish I could personally thank everyone who uses Pokedex individually for "
				+ "their support! I used to rent cheap server space to keep the bot running 24/7, however with the increase in popularity "
				+ "I have been forced to upgrade my server space to keep up with the massive amount of traffic Pokedex gets. It is now more "
				+ "expensive to keep the bot up and running smoothly. Every little bit helps! _All donations go toward paying for the server "
				+ "space._ Thank you for your continued support!");
		staticDiscordReply.addToReply("Donation link: https://goo.gl/HlrFrD");
		
		staticTwitchReply.addToReply("If you'd like to support the bot, please consider donating! "
				+ "All donations go toward renting server space for the bot to be up 24/7!");
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new DonateCommand();
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
