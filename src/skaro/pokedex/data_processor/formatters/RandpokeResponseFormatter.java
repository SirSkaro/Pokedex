package skaro.pokedex.data_processor.formatters;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.ColorService;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.util.EmbedBuilder;

public class RandpokeResponseFormatter implements IDiscordFormatter 
{

	@Override
	public Response invalidInputResponse(Input input) 
	{
		return null;
	}

	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		Response response = new Response();
		Language lang = input.getLanguage();
		builder.setLenient(true);
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		
		builder.withTitle(TextFormatter.flexFormToProper(species.getNameInLanguage(lang.getFlexKey())) + " | #" + Integer.toString(pokemon.getId()));
		
		//Add image
		builder.withImage(pokemon.getModel().getUrl());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorService.getColorForType(type));
		
		response.setEmbededReply(builder.build());
		return response;
	}

}
