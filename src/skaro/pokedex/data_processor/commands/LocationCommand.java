package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.DatabaseService;
import skaro.pokedex.database_resources.Location;
import skaro.pokedex.database_resources.LocationGroup;
import skaro.pokedex.input_processor.Input;

public class LocationCommand implements ICommand 
{
	private static LocationCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private LocationCommand()
	{
		commandName = "location".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.VERSION);
		expectedArgRange = new Integer[]{2,2};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new LocationCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name], [version] (not updated for gen 7)";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("This command must have a Pokemon and Version as input.");
				break;
				case 2:
					reply.addToReply("Input was not recognized as a Pokemon and Version.");
				break;
				default:
					reply.addToReply("A technical error occured (code 110)");
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
		LocationGroup locations = dbi.getLocation(input.getArg(0).getDB(), input.getArg(1).getDB());
		
		if(locations.getLocations().isEmpty())
		{
			reply.addToReply(locations.getSpecies()+" cannot be found in the wild in "
					+ input.getArg(1).getRaw()+" version.");
			return reply;
		}
		
		//Build reply
		reply.addToReply("**"+locations.getSpecies()+"** can be found in **"+(locations.getLocations().size())+
				"** locations in **"+locations.getVersion()+"** version.");
		
		for(Location loc : locations.getLocations())
		{
			reply.addToReply(""); //add line break;
			reply.addToReply("*"+loc.getRoute()+"*");
			reply.addToReply("\tRegion: "+loc.getRegion());
			reply.addToReply("\tMethod: "+loc.getMethod());
			reply.addToReply("\tLevels: "+loc.getLevel());
			reply.addToReply("\tEncounter Rate: "+loc.getChance());
		}
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		return null;
	}
	
}
