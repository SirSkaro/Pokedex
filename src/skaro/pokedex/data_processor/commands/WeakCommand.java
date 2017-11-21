package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class WeakCommand implements ICommand 
{
	private static WeakCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private WeakCommand()
	{
		commandName = "weak".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKE_TYPE_LIST);
		expectedArgRange = new Integer[]{1,2};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new WeakCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name] or [type] or [type, type]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("This command must have a Pokemon name or Type combination as input.");
				break;
				case 2:
					reply.addToReply("Input is not a recognized Pokemon or Type combination.");
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
		
		//Declare utility variables
		TypeInteractionWrapper wrapper;
		String formattedList, temp1, temp2;
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		//Build reply according to the argument case
		if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON) //argument is a Pokemon
		{
			SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
			
			//If data is null, then an error occurred
			if(poke.getSpecies() == null)
			{
				reply.addToReply("A technical error occured (code 1008). Please report this (twitter.com/sirskaro))");
				return reply;
			}
				
			wrapper = TypeTracker.onDefense(poke.getType1(), poke.getType2()); 
			reply.addToReply(("**__"+poke.getSpecies()+"__**").intern());
			builder.withColor(TypeTracker.getColor(poke.getType1()));
		}
		else
		{
			if(input.getArgs().size() == 1) // argument is one type
			{
				wrapper = TypeTracker.onDefense(input.getArg(0).getDB(), null);
				reply.addToReply("**__"+wrapper.getType1()+"__**");
				builder.withColor(wrapper.getColor());
			}
			else //argument is two types
			{
				wrapper = TypeTracker.onDefense(input.getArg(0).getDB(), input.getArg(1).getDB());
				reply.addToReply("**__"+wrapper.getType1()+"/"+wrapper.getType2()+"__**");
				builder.withColor(wrapper.getColor());
			}
		}
		
		//Format reply
		temp1 = wrapper.listToString(2.0);
		temp2 = wrapper.listToString(4.0);
		
		if(temp2.equals(""))
			formattedList = temp1;
		else if(temp1.equals(""))
			formattedList = "**"+temp2+"**";
		else
			formattedList = temp1 +", **"+temp2+"**";
		
		builder.appendField("Weak", formattedList, false);
		
		//Format neutral exchanges into a list
		builder.appendField("Neurtral", wrapper.listToString(1.0), false);
		
		//Format resistances into a list
		temp1 = wrapper.listToString(0.5);
		temp2 = wrapper.listToString(0.25);
		
		if(temp2.equals(""))
			formattedList = temp1;
		else if(temp1.equals(""))
			formattedList = temp2;
		else
			formattedList = temp1 +", **"+temp2+"**";
		
		builder.appendField("Resist", formattedList, false);
		
		//Format immunities into a list
		builder.appendField("Immune", wrapper.listToString(0.0), false);
	
		reply.setEmbededReply(builder.build());
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Declare utility variables
		TypeInteractionWrapper wrapper;
		String formattedList, temp1, temp2;
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		
		//Build reply according to the argument case
		if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON) //argument is a Pokemon
		{
			SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
			
			//If data is null, then an error occurred
			if(poke.getSpecies() == null)
			{
				reply.addToReply("A technical error occured (code 1008). Please report this (twitter.com/sirskaro))");
				return reply;
			}
				
			wrapper = TypeTracker.onDefense(poke.getType1(), poke.getType2()); 
			reply.addToReply("*"+poke.getSpecies()+"*");
		}
		else
		{
			if(input.getArgs().size() == 1) // argument is one type
			{
				wrapper = TypeTracker.onDefense(input.getArg(0).getDB(), null);
				reply.addToReply("*"+wrapper.getType1()+"*");
			}
			else //argument is two types
			{
				wrapper = TypeTracker.onDefense(input.getArg(0).getDB(), input.getArg(1).getDB());
				reply.addToReply("*"+wrapper.getType1()+"/"+wrapper.getType2()+"*");
			}
		}
		
		//Format weaknesses into a list
		temp1 = wrapper.listToString(2.0);
		temp2 = wrapper.listToString(4.0);
		
		if(temp2.equals(""))
			formattedList = temp1;
		else if(temp1.equals(""))
			formattedList = "<"+temp2+">";
		else
			formattedList = temp1 +", <"+temp2+">";
		
		reply.addToReply("Weak:"+formattedList);
		
		//Format neutral exchanges into a list
		reply.addToReply("Neutral:"+wrapper.listToString(1.0));
		
		//Format resistances into a list
		temp1 = wrapper.listToString(0.5);
		temp2 = wrapper.listToString(0.25);
		
		if(temp2.equals(""))
			formattedList = temp1;
		else if(temp1.equals(""))
			formattedList = temp2;
		else
			formattedList = temp1 +", <"+temp2+">";
		
		reply.addToReply("Resist:"+formattedList);
		
		//Format immunities into a list
		reply.addToReply("Immune:"+wrapper.listToString(0.0));
		
		return reply;
	}
}