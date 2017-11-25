package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TTSConverter;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.PokedexEntry;
import skaro.pokedex.input_processor.Argument;
import skaro.pokedex.input_processor.Input;

public class DexCommand implements ICommand
{
	private static DexCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private TTSConverter tts;
	
	private DexCommand()
	{
		commandName = "dex".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.VERSION);
		expectedArgRange = new Integer[]{2,2};
		tts = new TTSConverter();
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new DexCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name], [game version] (not updated for gen 7)";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("You must specify a Pokemon and a Version as input for this command "
							+ "(seperated by commas).");
				break;
				case 2:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(Argument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRaw()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: Not updated for gen 7. Try gens 1-6?");
				break;
				default:
					reply.addToReply("A technical error occured (code 108)");
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
		PokedexEntry entry = dbi.extractDexEntryFromDB(input.getArg(0).getDB(), input.getArg(1).getDB());
		
		if(entry.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1010). Please report this (twitter.com/sirskaro))");
			return reply;
		}
				
		if(entry.getEntry() == null)
		{
			reply.addToReply(entry.getSpecies()+" does not have a pokedex entry in "
							+input.getArg(1).getRaw());
			return reply;
		}
		
		reply.addToReply("Pokedex entry for **"+entry.getSpecies()+"** from **" 
						+entry.getVersion()+"**:");
		reply.addToReply(entry.getEntry());
		
		//Add audio reply
		reply.setPlayBack(tts.convertToAudio(entry.getSpecies()+", the "+ entry.getCategory()+" Pokie-mon. "
				+ entry.getEntry().replace(" - ", ", ")));
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Utility variables
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		PokedexEntry entry = dbi.extractDexEntryFromDB(input.getArg(0).getDB(), input.getArg(1).getDB());
		
		if(entry.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1010). Please report this (twitter.com/sirskaro))");
			return reply;
		}
				
		if(entry.getEntry() == null)
		{
			reply.addToReply(entry.getSpecies()+" does not have a pokedex entry in "
							+input.getArg(1).getRaw());
			return reply;
		}
		
		reply.addToReply("Pokedex entry for "+entry.getSpecies()+" from " 
						+entry.getVersion()+":");
		reply.addToReply(entry.getEntry());
		
		return reply;
	}
}
