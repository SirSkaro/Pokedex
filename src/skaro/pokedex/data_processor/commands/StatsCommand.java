package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class StatsCommand implements ICommand 
{	
	private static StatsCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private StatsCommand()
	{
		commandName = "stats".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new Integer[]{1,1};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new StatsCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("This command must have a Pokemon as an argument.");
				break;
				case 2:
					reply.addToReply(input.getArg(0).getRaw() +" is not a recognized Pokemon");
				break;
				default:
					reply.addToReply("A technical error occured (code 101)");
			}
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
		
		//If data is null, then an error occurred
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1001). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Format reply
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		int stats[] = poke.getStats();
		
		//Organize the data and add it to the reply
		reply.addToReply(("**__"+poke.getSpecies()+"__**").intern());
		
		String names1 = String.format("%-12s%s", "HP", "Attack").intern();
		String names2 = String.format("%-12s%s", "Defense", "Sp. Attack").intern();
		String names3 = String.format("%-12s%s", "Sp. Defense", "Speed").intern();
		String stats1 = String.format("%-12d%d", stats[0], stats[1]);
		String stats2 = String.format("%-12d%d", stats[2], stats[3]);
		String stats3 = String.format("%-12d%d", stats[4], stats[5]);
		
		builder.withDescription("__`"+names1+"`__\n`"+stats1+"`"
								+ "\n\n__`"+ names2+"`__\n`"+stats2+"`"
								+ "\n\n__`"+ names3+"`__\n`"+stats3 +"`");
		
		//Set embed color
		builder.withColor(ColorTracker.getColorFromType(poke.getType1()));
		
		reply.setEmbededReply(builder.build());
				
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
		
		//If data is null, then an error occurred
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1001). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		int stats[] = poke.getStats();
		
		//Organize the data and add it to the reply
		reply.addToReply("*"+poke.getSpecies()+"*");
		
		reply.addToReply("HP:"+stats[1]);
		reply.addToReply("Atk:"+stats[2]);
		reply.addToReply("Def:"+stats[3]);
		reply.addToReply("SpAtk:"+stats[4]);
		reply.addToReply("SpDef:"+stats[5]);
		reply.addToReply("Speed:"+stats[6]);
		
		return reply;
	}
}