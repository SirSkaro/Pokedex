package skaro.pokedex.data_processor.formatters;

import java.io.File;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
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
		
		//Upload local file
		String path = baseModelPath + "/" + pokemon.getName() + ".gif";
		File image = new File(path);
		response.addImage(image);
		
		//Add images
		builder.setImage("attachment://"+image.getName());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.setColor(colorService.getColorForType(type));
		
		response.setEmbed(builder);
		return response;
	}
	
}
