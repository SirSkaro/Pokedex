package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.AbstractArgument;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Move;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.VersionGroupDetail;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class LearnCommand implements ICommand 
{
	private ArgumentRange expectedArgRange;
	private String commandName;
	private ArrayList<ArgumentCategory> argCats;
	private PokeFlexFactory factory;
	
	public LearnCommand(PokeFlexFactory pff)
	{
		commandName = "learn".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.MOVE_LIST);
		expectedArgRange = new ArgumentRange(2,21);
		factory = pff;
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	public boolean makesWebRequest() { return true; }
	
	public String getArguments()
	{
		return "<pokemon>, <move>,...,<move>";
	}
	
	@SuppressWarnings("incomplete-switch")
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify 1 Pokemon and 1 to 20 Moves as input for this command "
							+ "(seperated by commas).");
				return false;	
			}
			
			//Because inputs that are not valid (case 2) are allowed this far, it is necessary to check if
			//the Pokemon is valid but allow other arguments to go unchecked
			if(!input.getArg(0).isValid())
			{
				reply.addToReply("\""+input.getArg(0).getRawInput()+"\" is not a recognized Pokemon.");
				return false;
			}
		}
		
		return true;
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Organize input
		List<String> urlParams = new ArrayList<String>();
		urlParams.add(input.getArg(0).getFlexForm());
		
		List<AbstractArgument> moves = new ArrayList<AbstractArgument>();
		moves.addAll(input.getArgs());
		moves.remove(0);	//remove the name of the Pokemon
		
		//Obtain data
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, urlParams);
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			//Format reply
			reply.addToReply(("**__"+TextFormatter.pokemonFlexFormToProper(pokemon.getName())+"__**").intern());
			reply.setEmbededReply(formatEmbed(pokemon, moves));
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1007", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon, List<AbstractArgument> movesToCheckFor)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		List<Move> allLearnableMoves = pokemon.getMoves();
		
		for(AbstractArgument moveToCheck : movesToCheckFor)
		{
			if(moveToCheck.getFlexForm() == null)
			{
				builder.appendField(TextFormatter.flexFormToProper(moveToCheck.getRawInput()), 
						"not recognized", true);
				continue;
			}
			
			Optional<Move> moveCheck = getMove(allLearnableMoves, moveToCheck.getFlexForm());
			
			if(!moveCheck.isPresent())
			{
				builder.appendField(TextFormatter.flexFormToProper(moveToCheck.getFlexForm()), 
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
			if(move.getMove().getName().equals(moveToCheck))
				return Optional.of(move);
		
		return Optional.empty();
	}
}