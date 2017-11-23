package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.SimpleMove;
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Argument;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class LearnCommand implements ICommand 
{
	private static LearnCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private LearnCommand()
	{
		commandName = "learn".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.MOVE_LIST);
		expectedArgRange = new Integer[]{2,21};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new LearnCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name], [move,move,...,move]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("You must specify between 1 Pokemon and 1 to 20 Moves as input for this command "
							+ "(seperated by commas).");
				return false;	
			}
			
			//Because inputs that are not valid (case 2) are allowed this far, it is necessary to check if
			//the Pokemon is valid but allow other arguments to go unchecked
			if(!input.getArg(0).isValid())
			{
				reply.addToReply("\""+input.getArg(0).getRaw()+"\" is not a recognized Pokemon.");
				return false;
			}
		}
		
		return true;
	}
	
	public Response discordReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
		
		//If data is null, then an error occurred
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1007). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Utility variables
		Argument moveArg;
		String dbMove;
		SimpleMove sMove;
		
		//Format reply
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		reply.addToReply(("**__"+poke.getSpecies()+"__**").intern());
		for(int i = 1; i < input.getArgs().size(); i++)
		{
			moveArg = input.getArg(i);
			if(moveArg.isValid())
			{
				dbMove = moveArg.getDB()+"-m";
				sMove = dbi.extractSimpleMoveFromDB(dbMove);
				builder.appendField(sMove.getName().intern(), 
						(dbi.inMoveSet(dbMove, input.getArg(0).getDB()) ? "able" : "not able"), true);
			}
			else
			{
				builder.appendField(moveArg.getRaw(), "not recognized", true);
			}
		}
		
		//Set embed color
		builder.withColor(ColorTracker.getColorFromType(poke.getType1()));
		
		reply.setEmbededReply(builder.build());
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
		
		//If data is null, then an error occurred
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1007). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		Argument moveArg;
		String dbMove;
		SimpleMove sMove;
		
		reply.addToReply("*"+poke.getSpecies()+"*");
		for(int i = 1; i < input.getArgs().size(); i++)
		{
			moveArg = input.getArg(i);
			if(moveArg.isValid())
			{
				dbMove = moveArg.getDB()+"-m";
				sMove = dbi.extractSimpleMoveFromDB(dbMove);
				reply.addToReply(sMove.getName()+":"
						+(dbi.inMoveSet(dbMove, input.getArg(0).getDB()) ? "able" : "not able"));
			}
			else
			{
				reply.addToReply(moveArg.getRaw()+ ":not recognized");
			}
		}
		
		return reply;
	}
}