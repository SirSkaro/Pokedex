package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.Optional;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.Type;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.SimpleMove;
import skaro.pokedex.input_processor.Argument;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

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
					reply.addToReply("You must specify between 1 to 4 Types or Moves as input for this command "
							+ "(seperated by commas).");
				break;
				case 2:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(Argument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRaw()+"\" is not a recognized Type or Move.");
					reply.addToReply("\n*top suggestion*: did you include commas between inputs?");
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
		EmbedBuilder builder = new EmbedBuilder();	
		ArrayList<Type> typeList = new ArrayList<Type>();
		Type currType = null;
		
		for(int i = 0; i < input.getArgs().size(); i++)
		{
			if(input.getArg(i).getCategory() == ArgumentCategory.TYPE)
				currType = Type.getByName(input.getArg(i).getDB());
			else	//Category is ArgumentCategory.MOVE
			{
				move = dbi.extractSimpleMoveFromDB(input.getArg(i).getDB()+"-m");
				
				//If data is null, then an error occurred
				if(move.getName() == null)
				{
					reply.addToReply("A technical error occured (code 1009). Please report this (twitter.com/sirskaro))");
					return reply;
				}
				
				currType = Type.getByName(move.getType());
			}
			
			typeList.add(currType);
		}
		
		wrapper = TypeTracker.onOffense(typeList);
		
		//Build reply
		reply.addToReply("**__"+wrapper.typesToString()+"__**");
		builder.appendField("Super Effective", getList(wrapper, 2.0), false);
		builder.appendField("Neutral", getList(wrapper, 1.0), false);
		builder.appendField("Resistant", getList(wrapper, 0.25), false);
		builder.appendField("Immune", getList(wrapper, 0.0), false);
		
		//Set border color
		builder.withColor(ColorTracker.getColorForWrapper(wrapper));
		builder.setLenient(true);
		reply.setEmbededReply(builder.build());
		
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
		ArrayList<Type> typeList = new ArrayList<Type>();
		Type currType = null;
		
		for(int i = 0; i < input.getArgs().size(); i++)
		{
			if(input.getArg(i).getCategory() == ArgumentCategory.TYPE)
				currType = Type.getByName(input.getArg(i).getDB());
			else	//Category is ArgumentCategory.MOVE
			{
				move = dbi.extractSimpleMoveFromDB(input.getArg(i).getDB()+"-m");
				
				//If data is null, then an error occurred
				if(move.getName() == null)
				{
					reply.addToReply("A technical error occured (code 1009). Please report this (twitter.com/sirskaro))");
					return reply;
				}
				
				currType = Type.getByName(move.getType());
			}
			
			typeList.add(currType);
		}
		
		wrapper = TypeTracker.onOffense(typeList);
		
		//Build reply
		reply.addToReply("*"+wrapper.typesToString()+"*");
		reply.addToReply("Super Effective:"+getList(wrapper, 2.0));
		reply.addToReply("Neutral:"+getList(wrapper, 1.0));
		reply.addToReply("Resistant:"+getList(wrapper, 0.25));
		reply.addToReply("Immune:"+getList(wrapper, 0.0));
		
		return reply;
	}
	
	private String getList(TypeInteractionWrapper wrapper, double mult)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult);
		return (strCheck.isPresent() ? strCheck.get() : "");
	}
}