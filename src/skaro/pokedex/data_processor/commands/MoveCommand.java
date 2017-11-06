package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.ComplexMove;
import skaro.pokedex.database_resources.DatabaseService;
import skaro.pokedex.input_processor.Input;

public class MoveCommand implements ICommand 
{
	private static MoveCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private MoveCommand()
	{
		commandName = "move".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.MOVE);
		expectedArgRange = new Integer[]{1,1};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new MoveCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[move name]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("This command must have one Move as an argument.");
				break;
				case 2:
					reply.addToReply(input.getArg(0).getRaw() +" is not a recognized Move");
				break;
				default:
					reply.addToReply("A technical error occured (code 105)");
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
		ComplexMove move = dbi.getComplexMove(input.getArg(0).getDB()+"-m");
		
		//If data is null, then an error occurred
		if(move.getName() == null)
		{
			reply.addToReply("A technical error occured (code 1006). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Organize the data and add it to the reply
		String tempString;
		reply.addToReply(("**"+move.getName()+"**").intern());
		
		if(move.getPower() > 1)
			reply.addToReply("\tBase Power | "+move.getPower());
		if(move.getZPower() > 1)
			reply.addToReply("\tZ-Base Power | "+move.getZPower());
		if(move.getCrystal() != null)
			reply.addToReply("\tZ-Crystal | "+move.getCrystal());
		reply.addToReply("\tAccuracy | "+ (move.getAccuracy() != 0 ? move.getAccuracy() : "-"));
		reply.addToReply("\tCategory | "+move.getCategory());
		reply.addToReply("\tType | "+move.getType());
		reply.addToReply("\tBase PP | "+move.getBasePP());
		reply.addToReply("\tMax PP | "+move.getMaxPP());
		if(move.getZBoost() != null)
			reply.addToReply("\tZ-Boosts | "+move.getZBoost());
		if((tempString = move.getZEffect()) != null)
			reply.addToReply("\tZ-Effect | "+tempString);
		reply.addToReply("\tPriority | "+move.getPriority());
		reply.addToReply("\tDescription | "+move.getShortDesc());
		reply.addToReply("\tTechnical Description | "+move.getTechDesc());
		reply.addToReply("\tTarget | "+move.getTarget());
		reply.addToReply("\tContest Category | "+move.getContest());
		if((tempString = move.getFlags()) != null)
			reply.addToReply("\tOther Properties | "+tempString);
		
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
		ComplexMove move = dbi.getComplexMove(input.getArg(0).getDB()+"-m");
		
		//If data is null, then an error occurred
		if(move.getName() == null)
		{
			reply.addToReply("A technical error occured (code 1006). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Organize the data and add it to the reply
		String tempString;
		reply.addToReply("*"+move.getName()+"*");
		
		if(move.getPower() > 1)
			reply.addToReply("Power:"+move.getPower());
		if(move.getZPower() > 1)
			reply.addToReply("Z-Power:"+move.getZPower());
		if(move.getCrystal() != null)
			reply.addToReply("Z-Crystal:"+move.getCrystal());
		reply.addToReply("Accuracy:"+ (move.getAccuracy() != 0 ? move.getAccuracy() : "-"));
		reply.addToReply("Category:"+move.getCategory());
		reply.addToReply("Type:"+move.getType());
		reply.addToReply("Max PP:"+move.getMaxPP());
		if(move.getZBoost() != null)
			reply.addToReply("Z-Boosts:"+move.getZBoost());
		if((tempString = move.getZEffect()) != null)
			reply.addToReply("Z-Effect:"+tempString);
		reply.addToReply("Priority:"+move.getPriority());
		reply.addToReply("Description:"+move.getShortDesc());
		reply.addToReply("Target:"+move.getTarget());
		reply.addToReply("Contest:"+move.getContest());
		if((tempString = move.getFlags()) != null)
			reply.addToReply("Other Properties:"+tempString);
		
		return reply;
	}
}
