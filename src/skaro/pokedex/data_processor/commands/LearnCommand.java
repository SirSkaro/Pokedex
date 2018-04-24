package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.SimpleMove;
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Argument;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Move;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.VersionGroupDetail;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class LearnCommand implements ICommand 
{
	private static LearnCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private LearnCommand(PokeFlexFactory pff)
	{
		commandName = "learn".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.MOVE_LIST);
		expectedArgRange = new Integer[]{2,21};
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new LearnCommand(pff);
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
					reply.addToReply("You must specify 1 Pokemon and 1 to 20 Moves as input for this command "
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
		
		//Organize input
		List<String> urlParams = new ArrayList<String>();
		urlParams.add(input.getArg(0).getDB());
		
		List<String> moves = new ArrayList<String>();
		moves.addAll(input.argsAsList());
		moves.remove(0);	//remove the name of the Pokemon
		
		//Obtain data
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, urlParams);
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			//Format reply
			reply.addToReply(("**__"+TextFormatter.flexFormToProper(pokemon.getName())+"__**").intern());
			reply.setEmbededReply(formatEmbed(pokemon, moves));
		} 
		catch (IOException | PokeFlexException e) { this.addErrorMessage(reply, "1007", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon, List<String> movesToCheckFor)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		List<Move> allLearnableMoves = pokemon.getMoves();
		
		for(String move : movesToCheckFor)
		{
			Optional<Move> moveCheck = getMove(allLearnableMoves, move);
			
			if(!moveCheck.isPresent())
			{
				builder.appendField(TextFormatter.flexFormToProper(move), 
						"*not able*", true);
			}
			else
			{
				builder.appendField(TextFormatter.flexFormToProper(moveCheck.get().getMove().getName()).intern(), 
						"*able* via:\n"+ formatLearnMethod(moveCheck.get()), true);
			}
		}
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		return builder.build();
	}
	
	private String formatLearnMethod(Move move) 
	{
		StringBuilder builder = new StringBuilder();
		List<String> methods = new ArrayList<String>();
		String methodName;
		
		for(VersionGroupDetail details : move.getVersionGroupDetails())
		{
			methodName = TextFormatter.flexFormToProper(details.getMoveLearnMethod().getName());
			
			//Add the method if there no duplicates. For some reason, List#contains won't work
			if(!(methods.contains(methodName)))
				methods.add(methodName);
		}
		
		for(String method : methods)
			builder.append("\t"+method+"\n");
		
		return builder.toString();
	}
	
	private Optional<Move> getMove(List<Move> allLearnableMoves, String moveToCheck)
	{
		for(Move move : allLearnableMoves)
			if(TextFormatter.flexToDBForm(move.getMove().getName()).equals(moveToCheck))
				return Optional.of(move);
		
		return Optional.empty();
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