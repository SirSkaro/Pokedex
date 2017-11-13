package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.SimpleMove;
import skaro.pokedex.input_processor.Input;

public class CoverageCommand implements ICommand 
{
	private static CoverageCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private CoverageCommand()
	{
		commandName = "coverage".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.MOVE_TYPE_LIST);
		expectedArgRange = new Integer[]{1,4};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new CoverageCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[type/move,...,type/move]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("This command must have a list of Moves/Types as input.");
				break;
				case 2:
					reply.addToReply("Input is not a list of Moves and/or Types");
				break;
				default:
					reply.addToReply("A technical error occured (code 107)");
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
		
		//If argument is a move, get the typing
		SimpleMove move;
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		TypeInteractionWrapper wrapper;
		
		for(int i = 0; i < input.getArgs().size(); i++)
		{
			if(input.getArg(i).getCategory() == ArgumentCategory.MOVE)
			{
				move = dbi.extractSimpleMoveFromDB(input.getArg(i).getDB()+"-m");
				
				//If data is null, then an error occurred
				if(move.getName() == null)
				{
					reply.addToReply("A technical error occured (code 1009). Please report this (twitter.com/sirskaro))");
					return reply;
				}
				
				input.getArg(i).setDB(move.getType());
			}
		}
		
		wrapper = TypeTracker.coverage
				(input.getArg(0).getDB(), 
				input.getArgs().size() > 1 ? input.getArg(1).getDB() : null,
				input.getArgs().size() > 2 ? input.getArg(2).getDB() : null,
				input.getArgs().size() > 3 ? input.getArg(3).getDB() : null);
		
		//Build reply
		reply.addToReply("**"+wrapper.typesToString()+"**");
		reply.addToReply("\tSuper Effective | "+wrapper.listToString(2.0));
		reply.addToReply("\tNeutral | "+wrapper.listToString(1.0));
		reply.addToReply("\tResistant | "+wrapper.listToString(0.5));
		reply.addToReply("\tImmune | "+wrapper.listToString(0.0));
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//If argument is a move, get the typing
		SimpleMove move;
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		TypeInteractionWrapper wrapper;
		
		for(int i = 0; i < input.getArgs().size(); i++)
		{
			if(input.getArg(i).getCategory() == ArgumentCategory.MOVE)
			{
				move = dbi.extractSimpleMoveFromDB(input.getArg(i).getDB()+"-m");
				
				//If data is null, then an error occurred
				if(move.getName() == null)
				{
					reply.addToReply("A technical error occured (code 1009). Please report this (twitter.com/sirskaro))");
					return reply;
				}
				
				input.getArg(i).setDB(move.getType());
			}
		}
		
		wrapper = TypeTracker.coverage
				(input.getArg(0).getDB(), 
				input.getArgs().size() > 1 ? input.getArg(1).getDB() : null,
				input.getArgs().size() > 2 ? input.getArg(2).getDB() : null,
				input.getArgs().size() > 3 ? input.getArg(3).getDB() : null);
		
		//Build reply
		reply.addToReply("*"+wrapper.typesToString()+"*");
		reply.addToReply("Super Effective:"+wrapper.listToString(2.0));
		reply.addToReply("Neutral:"+wrapper.listToString(1.0));
		reply.addToReply("Resistant:"+wrapper.listToString(0.5));
		reply.addToReply("Immune:"+wrapper.listToString(0.0));
		
		return reply;
	}
}