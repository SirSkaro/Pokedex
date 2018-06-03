package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.database_resources.ComplexMove;
import skaro.pokedex.database_resources.DatabaseResourcePool;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.move.Move;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class MoveCommand implements ICommand 
{
	private static MoveCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private MoveCommand(PokeFlexFactory pff)
	{
		commandName = "move".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.MOVE);
		expectedArgRange = new Integer[]{1,1};
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new MoveCommand(pff);
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
					reply.addToReply("A technical error occured (code 106)");
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
		
		try 
		{
			//Obtain data
			Object flexObj = factory.createFlexObject(Endpoint.MOVE, input.argsAsList());
			Move move = Move.class.cast(flexObj);
			
			//Format reply
			reply.addToReply(("**__"+TextFormatter.flexFormToProper(move.getName())+"__**").intern());
			reply.setEmbededReply(formatEmbed(move));
		} 
		catch (Exception e) { this.addErrorMessage(reply, "1006", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Move move)
	{
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		if(move.getPower() != 0)
			builder.appendField("Base Power", Integer.toString(move.getPower()), true);
		if(move.getZPower() != 0)
			builder.appendField("Z-Base Power", Integer.toString(move.getZPower()), true);
		if(move.getCrystal() != null)
			builder.appendField("Z-Crystal", move.getCrystal().toString(), true);
		builder.appendField("Accuracy", (move.getAccuracy() != 0 ? Integer.toString(move.getAccuracy()) : "-"), true);
		builder.appendField("Category", TextFormatter.flexFormToProper(move.getDamageClass().getName()), true);
		builder.appendField("Type", TextFormatter.flexFormToProper(move.getType().getName()), true);
		builder.appendField("Base PP", Integer.toString(move.getPp()), true);
		builder.appendField("Max PP", Integer.toString(move.getMaxPp()), true);
		if(move.getZBoost() != null)
			builder.appendField("Z-Boosts", move.getZBoost().toString(), true);
		if(move.getZEffect() != null)
			builder.appendField("Z-Effect", move.getZEffect().toString(), true);
		builder.appendField("Priority", Integer.toString(move.getPriority()), true);
		builder.appendField("Target", TextFormatter.flexFormToProper(move.getTarget().getName()), true);
		if(move.getContestType() != null)
			builder.appendField("Contest Category", TextFormatter.flexFormToProper(move.getContestType().getName()), true);
		builder.appendField("Game Description", move.getSdesc(), false);
		builder.appendField("Technical Description", move.getLdesc(), false);
		
		if(move.getFlags() != null)
		{
			StringBuilder flagBuilder = new StringBuilder();
			for(String flag : move.getFlags())
				flagBuilder.append(flag + " ");
			
			builder.appendField("Other Properties", flagBuilder.toString(), false);
		}
		
		//Set embed color
		builder.withColor(ColorTracker.getColorForType(move.getType().getName()));
		
		return builder.build();
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseResourcePool dbi = DatabaseResourcePool.getInstance();
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
