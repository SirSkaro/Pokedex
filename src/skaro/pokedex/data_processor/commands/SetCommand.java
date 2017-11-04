package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.Set;
import skaro.pokedex.database_resources.SetGroup;
import skaro.pokedex.input_processor.Input;

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
					reply.addToReply("This command must have a Pokemon, Meta, and Generation as input.");
				break;
				case 2:
					reply.addToReply("Input was not recognized as a Pokemon, Meta, and Generation.");
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
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		SetGroup sets = dbi.extractSetsFromDB(input.getArg(0).getDB(),
					input.getArg(1).getDB(), Integer.parseInt(input.getArg(2).getDB()));
        
		//If "species" field is null, then some error occured
//		if(sets.getSpecies() == null)
//		{
//			reply.addToReply("A technical error occured (code 1011). Please report this (twitter.com/sirskaro))");
//			return reply;
//		}
		
		if(sets.getSets().isEmpty())
		{
			reply.addToReply(sets.getSpecies()+" does not have a standard moveset in "
					+ input.getArg(1).getRaw()+" in Gen " +input.getArg(2).getRaw());
			return reply;
		}
		
		//Populate reply
		reply.addToReply("**"+sets.getTier()+"** sets for **"+sets.getSpecies()+"** "
				+ "in Generation **"+sets.getGen()+"**");
		reply.addToReply("");
	
		String temp;
		for(Set currSet : sets.getSets())
		{
			reply.addToReply("\"*"+currSet.getTitle()+"*\"");	//title
			reply.addToReply(sets.getSpecies() 	//name and item
						+ (currSet.getItem() != null ? " @ "+ currSet.getItem() : "" ));
			
			if(currSet.getAbility() != null)			//ability
				reply.addToReply("Ability: "+currSet.getAbility()); 
			
			if((temp = currSet.evsToString()) != null)	//EVs
			reply.addToReply("EVs: "+temp);
			
			if(currSet.getNature() != null)			//Nature
				reply.addToReply(currSet.getNature()+" Nature");
			
			if((temp = currSet.ivsToString()) != null)			//IVs
				reply.addToReply("IVs: "+temp);
			
			reply.addToReply("- "+currSet.getMove1());					//moves
			if(currSet.getMove2() != null)
				reply.addToReply("- "+currSet.getMove2());
			if(currSet.getMove3() != null)
				reply.addToReply("- "+currSet.getMove3());
			if(currSet.getMove4() != null)
				reply.addToReply("- "+currSet.getMove4());
			
			reply.addToReply("");					//Empty space
		}
		
		reply.addToReply("You can learn more about these sets at Smogon's competitive Pokedex:");
		reply.addToReply(sets.getURL());
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		return null;
	}
}
