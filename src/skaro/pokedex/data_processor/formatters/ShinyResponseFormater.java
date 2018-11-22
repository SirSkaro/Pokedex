package skaro.pokedex.data_processor.formatters;

import java.io.File;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.util.EmbedBuilder;

public class ShinyResponseFormater implements IDiscordFormatter 
{
	private final String baseModelPath;
	
	public ShinyResponseFormater() 
	{
		baseModelPath = Configurator.getInstance().get().getModelBasePath();
	}
	
	@Override
	public Response invalidInputResponse(Input input) 
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify exactly one Pokemon as input for this command".intern());
			break;
			case INVALID_ARGUMENT:
				response.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Pokemon in "+input.getLanguage().getName());
			break;
			default:
				response.addToReply("A technical error occured (code 112)");
		}
		
		return response;
	}

	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		String path;
		File image;
		Language lang = input.getLanguage();
		Response response = new Response();
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		builder.setLenient(true);
		
		//Format reply
		response.addToReply("**__"+TextFormatter.pokemonFlexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+" | #" + Integer.toString(species.getId()) 
			+ " | " + TextFormatter.formatGeneration(species.getGeneration().getName(), lang) + "__**");
		
		//Upload local file
		path = baseModelPath + "/" + pokemon.getName() + ".gif";
		image = new File(path);
		response.addImage(image);
		
		//Add images
		builder.withImage("attachment://"+image.getName());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		response.setEmbededReply(builder.build());
		return response;
	}
	
}
