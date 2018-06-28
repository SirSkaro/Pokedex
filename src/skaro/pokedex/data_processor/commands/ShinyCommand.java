package skaro.pokedex.data_processor.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.core.PrivilegeChecker;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class ShinyCommand implements ICommand
{
	private static ShinyCommand instance;
	private static ArgumentRange expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	private static String baseModelPath;
	private static PrivilegeChecker checker;
	
	private ShinyCommand(PokeFlexFactory pff, PrivilegeChecker pc)
	{
		commandName = "shiny".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new ArgumentRange(1,1);
		factory = pff;
		baseModelPath = Configurator.getInstance().get().getModelBasePath();
		checker = pc;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff, PrivilegeChecker pc)
	{
		if(instance != null)
			return instance;

		instance = new ShinyCommand(pff, pc);
		return instance;
	}

	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name]";
	}
	
	public boolean inputIsValid(Response reply, Input input) 
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify exactly one Pokemon as input for this command.".intern());
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Pokemon.");
				break;
				default:
					reply.addToReply("A technical error occured (code 112)");
			}
			return false;
		}
		return true;
	}

	@Override
	public Response discordReply(Input input, IUser requester) 
	{
		//Set up utility variables
		Response reply = new Response();
		List<String> urlParameters = new ArrayList<String>();
		String path;
		File image;
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
				
		//Obtain data
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			urlParameters.add(pokemon.getSpecies().getName());
			flexObj = factory.createFlexObject(Endpoint.POKEMON_SPECIES, input.argsAsList());
			PokemonSpecies speciesData = PokemonSpecies.class.cast(flexObj);
			
			//Format reply
			reply.addToReply("**__"+TextFormatter.pokemonFlexFormToProper(pokemon.getName())+" | #" + Integer.toString(speciesData.getId()) 
				+ " | " + TextFormatter.formatGeneration(speciesData.getGeneration().getName()) + "__**");
			
			
			//Upload local file
			path = baseModelPath + "/" + pokemon.getName() + ".gif";
			image = new File(path);
			reply.addImage(new File(path));
			
			reply.setEmbededReply(formatEmbed(pokemon, image));
		} 
		catch (IOException | PokeFlexException e) { this.addErrorMessage(reply, "1012", e); }
				
		return reply;
	}
	
	private Response privilegedReply()
	{
		return null;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon, File image) throws IOException
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		//Add images
		builder.withImage("attachment://"+image.getName());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		return builder.build();
	}
}
