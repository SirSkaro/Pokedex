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
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Argument;
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
					reply.addToReply("You must specify 1 Pokemon or between 1 and 2 Types (seperated by commas) "
							+ "as input for this command.");
				break;
				case 2:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(Argument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRaw()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: did you include commas between inputs?");
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
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		EmbedBuilder builder = new EmbedBuilder();
		Type type1, type2 = null;
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
			
			type1 = Type.getByName(poke.getType1());
			if(poke.getType2() != null)
				type2 = Type.getByName(poke.getType2());
		}
		else //argument is a list of Types
		{
			if(input.getArgs().size() == 1) // argument is one type
			{
				type1 = Type.getByName(input.getArg(0).getDB());
			}
			else //argument is two types
			{
				type1 = Type.getByName(input.getArg(0).getDB());
				type2 = Type.getByName(input.getArg(1).getDB());
			}
		}
		
		wrapper = TypeTracker.onDefense(type1, type2);
		StringBuilder header = new StringBuilder();
		header.append("**__"+type1.toProperName());
		header.append(type2 != null ? "/"+type2.toProperName()+"__**" : "__**");
		reply.addToReply(header.toString());
		
		//Format reply
		builder.appendField("Weak:", combineLists(wrapper, 2.0, 4.0), false);
		
		builder.appendField("Neutral", getList(wrapper, 1.0), false);
		builder.appendField("Resist", combineLists(wrapper, 0.5, 0.25), false);
		builder.appendField("Immune", getList(wrapper, 0.0), false);
	
		builder.withColor(ColorTracker.getColorForWrapper(wrapper));
		reply.setEmbededReply(builder.build());
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		return null;
	}
	
	private String combineLists(TypeInteractionWrapper wrapper, double mult1, double mult2)
	{
		Optional<String> strCheck;
		String inter1, intern2;
		StringBuilder builder = new StringBuilder();
		
		strCheck = wrapper.interactionToString(mult1);
		inter1 = strCheck.isPresent() ? strCheck.get() : null;
		
		strCheck = wrapper.interactionToString(mult2);
		intern2 = strCheck.isPresent() ? strCheck.get() : null;
		
		if(inter1 == null && intern2 == null)
			return null;
		
		if(inter1 != null)
			builder.append(inter1);
		
		if(inter1 != null && intern2 != null)
			builder.append(", **"+intern2+"**");
		else if(intern2 != null)
			builder.append("**"+intern2+"**");
		
		return builder.toString();
	}
	
	private String getList(TypeInteractionWrapper wrapper, double mult)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
}