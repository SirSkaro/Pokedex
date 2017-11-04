package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.HashSet;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;

public class CommandsCommand implements ICommand 
{
	private static CommandsCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply, staticTwitchReply;
	
	private CommandsCommand(HashSet<ICommand> library)
	{
		commandName = "commands".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new Integer[]{0,0};
		staticDiscordReply = new Response();
		staticTwitchReply = new Response();
		
		staticDiscordReply.addToReply("```Prefixes: ! or %");
		staticDiscordReply.addToReply("Alternative: command(argument list)```");
		staticDiscordReply.addToReply("```List of Commands:");
		
		for(ICommand entry : library)
		    staticDiscordReply.addToReply("\t" + entry.getCommandName() +" | "+ entry.getArguments());
		
		staticDiscordReply.addToReply("```***NOTE***");
		staticDiscordReply.addToReply("\t __You must include__ commas");
		staticDiscordReply.addToReply("\t __Do not include__ '[', '}', or '|'");
		staticDiscordReply.addToReply("\t __Do not forget__ prefixes or alternative. For examples, use the help command.");
		
		staticDiscordReply.addToReply(""); //line break
		staticDiscordReply.addToReply("***Call for Donations!***");
		staticDiscordReply.addToReply("\tPlease read more in the %donate command!");
		
		staticTwitchReply.addToReply("*Commands* (Use !help for examples)");
		
		for(ICommand entry : library)
			staticTwitchReply.addToReply(entry.getCommandName()); //+" | "+ twitchCommandCache.get(entry).getArguments());
	}
	
	public static ICommand getInstance(HashSet<ICommand> library)
	{
		if(instance != null)
			return instance;

		if(library == null || library.isEmpty())
			throw new IllegalArgumentException("CommandsCommand needs a set of commands!");
		
		instance = new CommandsCommand(library);
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[none]";
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