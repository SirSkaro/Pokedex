package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.ComplexMove;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

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
					reply.addToReply("You must specify exactly one Move as input for this command.".intern());
				break;
				case 2:
					reply.addToReply("\""+input.getArg(0).getRaw() +"\" is not a recognized Move");
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
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		ComplexMove move = dbi.extractComplexMoveFromDB(input.getArg(0).getDB()+"-m");
		
		//If data is null, then an error occurred
		if(move.getName() == null)
		{
			reply.addToReply("A technical error occured (code 1006). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Format reply
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		//Organize the data and add it to the reply
		String tempString;
		reply.addToReply(("**__"+move.getName()+"__**").intern());
		
		if(move.getPower() > 1)
			builder.appendField("Base Power", Integer.toString(move.getPower()), true);
		if(move.getZPower() > 1)
			builder.appendField("Z-Base Power", Integer.toString(move.getZPower()), true);
		if(move.getCrystal() != null)
			builder.appendField("Z-Crystal", move.getCrystal(), true);
		builder.appendField("Accuracy", (move.getAccuracy() != 0 ? Integer.toString(move.getAccuracy()) : "-"), true);
		builder.appendField("Category", move.getCategory(), true);
		builder.appendField("Type", move.getType(), true);
		builder.appendField("Base PP", Integer.toString(move.getBasePP()), true);
		builder.appendField("Max PP", Integer.toString(move.getMaxPP()), true);
		if(move.getZBoost() != null)
			builder.appendField("Z-Boosts", move.getZBoost(), true);
		if((tempString = move.getZEffect()) != null)
			builder.appendField("Z-Effect", tempString, true);
		builder.appendField("Priority", Integer.toString(move.getPriority()), true);
		builder.appendField("Target", move.getTarget(), true);
		builder.appendField("Contest Category", move.getContest(), true);
		builder.appendField("Game Description", move.getShortDesc(), false);
		builder.appendField("Technical Description", move.getTechDesc(), false);
		if((tempString = move.getFlags()) != null)
			builder.appendField("Other Properties", tempString, false);
		
		//Set embed color
		builder.withColor(ColorTracker.getColorFromType(move.getType()));
		
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
		ComplexMove move = dbi.extractComplexMoveFromDB(input.getArg(0).getDB()+"-m");
		
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
