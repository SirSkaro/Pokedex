package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.Set;
import skaro.pokedex.database_resources.SetGroup;
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Argument;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class SetCommand implements ICommand 
{

	private static SetCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private SetCommand()
	{
		commandName = "set".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.META);
		argCats.add(ArgumentCategory.GEN);
		expectedArgRange = new Integer[]{3,3};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new SetCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name], [meta game], [generation] (not updated for gen 7)";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("You must specify a Pokemon, a Meta, and a Generation as input for this command "
							+ "(seperated by commas).");
				break;
				case 2:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(Argument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRaw()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: Only Smogon and VGC metas are supported, and not updated for gen 7. "
							+ "Try an official tier or gens 1-6?");
				break;
				default:
					reply.addToReply("A technical error occured (code 109)");
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
		
		//Utility variables
		String temp;
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		SetGroup sets = dbi.extractSetsFromDB(input.getArg(0).getDB(),
					input.getArg(1).getDB(), Integer.parseInt(input.getArg(2).getDB()));
		SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
		EmbedBuilder eBuilder = new EmbedBuilder();	
		StringBuilder sBuilder;
		eBuilder.setLenient(true);
		
		
		if(sets.getSets().isEmpty())
		{
			reply.addToReply(sets.getSpecies()+" does not have a standard moveset in "
					+ input.getArg(1).getRaw()+" in Gen " +input.getArg(2).getRaw());
			return reply;
		}
		
		//Populate reply
		reply.addToReply("__**"+sets.getTier()+"** sets for **"+sets.getSpecies()+"** "
				+ "in Generation **"+sets.getGen()+"**__");
	
		for(Set currSet : sets.getSets())
		{
			sBuilder = new StringBuilder();
			sBuilder.append(sets.getSpecies() 					//Name and Item
						+ (currSet.getItem() != null ? " @ "+ currSet.getItem() : "" )
						+ "\n");
			if(currSet.getAbility() != null)					//Ability
				sBuilder.append("Ability: "+currSet.getAbility() + "\n"); 
			
			if((temp = currSet.evsToString()) != null)			//EVs
				sBuilder.append("EVs: "+temp + "\n");
			
			if(currSet.getNature() != null)						//Nature
				sBuilder.append(currSet.getNature()+" Nature\n");
			
			if((temp = currSet.ivsToString()) != null)			//IVs
				sBuilder.append("IVs: "+temp+"\n");
			
			sBuilder.append("- "+currSet.getMove1()+"\n");			//Moves
			if(currSet.getMove2() != null)
				sBuilder.append("- "+currSet.getMove2()+"\n");
			if(currSet.getMove3() != null)
				sBuilder.append("- "+currSet.getMove3()+"\n");
			if(currSet.getMove4() != null)
				sBuilder.append("- "+currSet.getMove4()+"\n");
			
			eBuilder.appendField("\"*"+currSet.getTitle()+"*\"", sBuilder.toString(), true);
			
		}
		
		eBuilder.withColor(ColorTracker.getColorFromType(poke.getType1()));
		eBuilder.withFooterText("You can learn more about these sets at Smogon's competitive Pokedex:\n"+sets.getURL());
		reply.setEmbededReply(eBuilder.build());
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		return null;
	}
}
