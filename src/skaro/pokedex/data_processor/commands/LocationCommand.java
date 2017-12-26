package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.Location;
import skaro.pokedex.database_resources.LocationGroup;
import skaro.pokedex.input_processor.Argument;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

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
					reply.addToReply("You must specify a Pokemon and a Version as input for this command "
							+ "(seperated by commas).");
				break;
				case 2:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(Argument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRaw()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: Not updated for gen7. Try versions from gens 1-6?");
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
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		LocationGroup locations = dbi.extractLocationFromDB(input.getArg(0).getDB(), input.getArg(1).getDB());
		
		if(locations.getLocations().isEmpty())
		{
			reply.addToReply(locations.getSpecies()+" cannot be found in the wild in "
					+ input.getArg(1).getRaw()+" version.");
			return reply;
		}
		
		//Build reply
		EmbedBuilder eBuilder = new EmbedBuilder();	
		StringBuilder sBuilder;	
		eBuilder.setLenient(true);
		reply.addToReply("**"+locations.getSpecies()+"** can be found in **"+(locations.getLocations().size())+
				"** location(s) in **"+locations.getVersion()+"** version");
		
		for(Location loc : locations.getLocations())
		{
			sBuilder = new StringBuilder();
			sBuilder.append("Region: "+loc.getRegion()+"\n");
			sBuilder.append("Method: "+loc.getMethod()+"\n");
			sBuilder.append("Levels: "+loc.getLevel()+"\n");
			sBuilder.append("Encounter Rate: "+ loc.getChance()+"\n");
			eBuilder.appendField(loc.getRoute(), sBuilder.toString(), true);
		}
		
		eBuilder.withColor(ColorTracker.getColorForVersion(locations.getVersion()));
		reply.setEmbededReply(eBuilder.build());
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		return null;
	}
	
}
