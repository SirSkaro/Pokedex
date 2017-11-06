package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.DatabaseService;
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Input;

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
		DatabaseService dbi = DatabaseService.getInstance();
		SimplePokemon poke = dbi.getSimplePokemon(input.getArg(0).getDB());
		
		//If data is null, then an error occurred
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1001). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		int stats[] = poke.getStats();
		
		//Organize the data and add it to the reply
		reply.addToReply(("**"+poke.getSpecies()+"**").intern());
		
		reply.addToReply("\tHP | "+stats[0]);
		reply.addToReply("\tAtk | "+stats[1]);
		reply.addToReply("\tDef | "+stats[2]);
		reply.addToReply("\tSpAtk | "+stats[3]);
		reply.addToReply("\tSpDef | "+stats[4]);
		reply.addToReply("\tSpeed | "+stats[5]);
				
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseService dbi = DatabaseService.getInstance();
		SimplePokemon poke = dbi.getSimplePokemon(input.getArg(0).getDB());
		
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