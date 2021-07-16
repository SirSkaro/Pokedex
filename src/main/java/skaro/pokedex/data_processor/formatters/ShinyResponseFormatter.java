package skaro.pokedex.data_processor.formatters;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.Exceptions;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class ShinyResponseFormatter implements ResponseFormatter, PokedexServiceConsumer
{
	private final String baseModelPath;
	private PokedexServiceManager services;
	
	public ShinyResponseFormatter(PokedexServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
		baseModelPath = ((ConfigurationService)services.getService(ServiceType.CONFIG)).getModelBasePath();
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR, ServiceType.CONFIG);
	}
	
	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder)
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		Response response = new Response();
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		
		//Format reply
		response.addToReply("**__"+TextUtility.pokemonFlexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+" | #" + Integer.toString(species.getId()) 
			+ " | " + TextUtility.formatGeneration(species.getGeneration().getName(), lang) + "__**");
		
		//set image file
		addImageToResponse(response, pokemon, builder);
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.setColor(colorService.getColorForType(type));
		
		response.setEmbed(builder);
		return response;
	}
	
	private void addImageToResponse(Response response, Pokemon pokemon, EmbedCreateSpec builder) {
		String fileName = pokemon.getName() + ".gif";
		try {
			URL url =  new URL(baseModelPath + "/"+ fileName);
			response.addImage(fileName, url.openStream());
			builder.setImage("attachment://"+fileName);
		} catch(IOException e) {
			throw Exceptions.propagate(e);
		}
	}
	
}
