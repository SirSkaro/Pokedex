package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TTSConverter;
import skaro.pokedex.database_resources.DatabaseService;
import skaro.pokedex.database_resources.PokedexEntry;
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
					reply.addToReply("This command must have a list of Pokemon and a Version as input.");
				break;
				case 2:
					reply.addToReply("Input is not a Pokemon and Version");
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
		DatabaseService dbi = DatabaseService.getInstance();
		PokedexEntry entry = dbi.getDexEntry(input.getArg(0).getDB(), input.getArg(1).getDB());
		
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
		DatabaseService dbi = DatabaseService.getInstance();
		PokedexEntry entry = dbi.getDexEntry(input.getArg(0).getDB(), input.getArg(1).getDB());
		
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
