package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class StatsCommand extends PokedexCommand  
{	
	public StatsCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "stats".intern();
		orderedArgumentCategories = new ArrayList<ArgumentCategory>();
		orderedArgumentCategories.add(ArgumentCategory.POKEMON);
		expectedArgRange = new ArgumentRange(1,1);
		
		aliases.put("statistiken", Language.GERMAN);
		aliases.put("statistica", Language.ITALIAN);
		aliases.put("tonggye", Language.KOREAN);
		aliases.put("tǒngjì", Language.CHINESE_SIMPMLIFIED);
		aliases.put("tongji", Language.CHINESE_SIMPMLIFIED);
		aliases.put("estadística", Language.SPANISH);
		aliases.put("estadistica", Language.SPANISH);
		aliases.put("estad", Language.SPANISH);
		aliases.put("tōkei", Language.JAPANESE_HIR_KAT);
		aliases.put("tokei", Language.JAPANESE_HIR_KAT);
		aliases.put("statistiques", Language.FRENCH);
		
		aliases.put("統計", Language.JAPANESE_HIR_KAT);
		aliases.put("통계량", Language.KOREAN);
		aliases.put("统计", Language.CHINESE_SIMPMLIFIED);
		
		createHelpMessage("Darmanitan", "Alolan Sandshrew", "Ninetales Alola", "Mega Venusaur",
				"https://i.imgur.com/svFfe9Q.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }	
	@Override
	public String getArguments() { return "<pokemon>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK);
	}
	
	@Override
	public Mono<Response> prepareResponse(Input input, User author)
	{ 
		if(!input.isValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		EmbedCreateSpec builder = new EmbedCreateSpec();
		String pokemonName = input.getArgument(0).getFlexForm();
		Mono<MultiMap<IFlexObject>> result;
		
		Request request = new Request(Endpoint.POKEMON, pokemonName);
		result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> request.makeRequest(factory)
					.ofType(Pokemon.class)
					.flatMap(pokemon -> this.addAdopter(pokemon, builder))
					.doOnNext(pokemon -> dataMap.put(Pokemon.class.getName(), pokemon))
					.map(pokemon -> new Request(Endpoint.POKEMON_SPECIES, pokemon.getSpecies().getName()))
					.flatMap(speciesRequest -> speciesRequest.makeRequest(factory))
					.doOnNext(species -> dataMap.put(PokemonSpecies.class.getName(), species))
					.then(Mono.just(dataMap)));
		
		this.addRandomExtraMessage(builder);
		return result
				.map(dataMap -> formatter.format(input, dataMap, builder))
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
	}
	
}