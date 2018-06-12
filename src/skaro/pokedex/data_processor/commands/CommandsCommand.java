package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.HashMap;

import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.util.EmbedBuilder;

public class CommandsCommand implements ICommand 
{
	private static CommandsCommand instance;
	private static ArgumentRange expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply;
	
	private CommandsCommand(HashMap<String, ICommand> library)
	{
		commandName = "commands".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		staticDiscordReply.setPrivate(true);
		staticDiscordReply.addToReply("**__Pokedex Commands__**");
		builder.withColor(255, 255, 255);
		builder.appendField("Prefixes", "!command or %command", true);
		builder.appendField("Postfix", "command(input)", true);
		builder.appendField("Hints","Use `%help` for examples. __Don't forget your commas!__", false);
		
		for(ICommand entry : library.values())
			builder.appendField("`"+entry.getCommandName()+"`", ("input: "+ entry.getArguments()).intern(), false);
		
		builder.withFooterText("If you like the bot, please consider donating! Use the donate command for a link.");
		
		staticDiscordReply.setEmbededReply(builder.build());
	}
	
	public static ICommand getInstance(HashMap<String, ICommand> library)
	{
		if(instance != null)
			return instance;

		if(library == null || library.isEmpty())
			throw new IllegalArgumentException("CommandsCommand needs a set of commands!");
		
		instance = new CommandsCommand(library);
		return instance;
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
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
}