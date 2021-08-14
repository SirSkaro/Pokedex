package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.input_processor.ArgumentSpec;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.PokemonArgument;
import skaro.pokedex.services.FlexCacheService;
import skaro.pokedex.services.FlexCacheService.CachedResource;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.evolution_chain.EvolutionChain;
import skaro.pokeflex.objects.evolution_chain.EvolvesTo;
import skaro.pokeflex.objects.pokemon.Form;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class DataCommand extends PokedexCommand 
{
	public DataCommand(PokedexServiceManager services, ResponseFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "data".intern();
		
		aliases.put("pokemon", Language.ENGLISH);
		aliases.put("dt", Language.ENGLISH);
		aliases.put("poke", Language.ENGLISH);
		aliases.put("info", Language.ENGLISH);
		aliases.put("datos", Language.SPANISH);
		aliases.put("dennees", Language.FRENCH);
		aliases.put("dati", Language.ITALIAN);
		aliases.put("daten", Language.GERMAN);
		aliases.put("dēta", Language.JAPANESE_HIR_KAT);
		aliases.put("shùjù", Language.CHINESE_SIMPMLIFIED);
		aliases.put("deiteo", Language.KOREAN);
		
		aliases.put("データ", Language.JAPANESE_HIR_KAT);
		aliases.put("数据", Language.CHINESE_SIMPMLIFIED);
		aliases.put("데이터", Language.KOREAN);
		
		extraMessages.add("HD Shiny Pokemon are here! See the shiny with %shiny (Patreons only)");
		
		createHelpMessage("mew", "mega charizard x", "primal-kyogre", "Alolan Raichu");
	}

	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments(){ return "<pokemon>"; }
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK, ServiceType.CACHE);
	}
	
	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild)
	{
		if(!input.allArgumentValid())
			return Mono.just(formatter.invalidInputResponse(input));

		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		EmbedCreateSpec builder = new EmbedCreateSpec();
		String pokemonArgument = input.getArgument(0).getFlexForm();
		Request pokemonRequest = new Request(Endpoint.POKEMON, pokemonArgument);
		
		Mono<MultiMap<IFlexObject>> result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> pokemonRequest.makeRequest(factory)
						.ofType(Pokemon.class)
						.flatMap(pokemon -> this.addAdopter(pokemon, builder))
						.doOnNext(pokemon -> {
							dataMap.put(Pokemon.class.getName(), pokemon);
							addTypesToMap(pokemon, dataMap);
						})
						.flatMap(pokemon -> Mono.just(new Request(Endpoint.POKEMON_SPECIES, pokemon.getSpecies().getName()))
								.flatMap(request -> request.makeRequest(factory))
								.ofType(PokemonSpecies.class)
								.doOnNext(species -> dataMap.put(PokemonSpecies.class.getName(), species))
								.flatMap(species -> Flux.just(new RequestURL(species.getEvolutionChain().getUrl(), Endpoint.EVOLUTION_CHAIN))
										.concatWithValues(new RequestURL(species.getGrowthRate().getUrl(), Endpoint.GROWTH_RATE))
										.concatWithValues(pokemon.getAbilities()
												.stream()
												.map(ability -> new RequestURL(ability.getAbility().getUrl(), Endpoint.ABILITY))
												.toArray(RequestURL[]::new))
										.concatWithValues(species.getEggGroups()
												.stream()
												.map(eggGroup -> new RequestURL(eggGroup.getUrl(), Endpoint.EGG_GROUP))
												.toArray(RequestURL[]::new))
										.concatWithValues(species.getVarieties()
												.stream()
												.filter(variety -> !pokemon.getName().equals(variety.getPokemon().getName()))
												.map(variety -> new RequestURL(variety.getPokemon().getUrl(), Endpoint.POKEMON))
												.toArray(RequestURL[]::new))
										.parallel()
										.runOn(factory.getScheduler())
										.flatMap(request -> request.makeRequest(factory))
										.doOnNext(flexObj -> dataMap.add(flexObj.getClass().getName(), flexObj))
										.sequential()
										.then(Mono.fromCallable(() -> getEvolutionChain(dataMap))))
								.map(evoChain -> getPokemonInChain(evoChain, pokemon))
								.flatMap(pokemonRequests -> Flux.fromIterable(pokemonRequests)
										.concatWithValues(getPokemonForms(dataMap)
												.stream()
												.toArray(RequestURL[]::new))
										.parallel()
										.runOn(factory.getScheduler())
										.flatMap(request -> request.makeRequest(factory))
										.doOnNext(flexObj -> dataMap.add(flexObj.getClass().getName(), flexObj))
										.sequential()
										.then(Mono.just(dataMap)))));
		
		this.addRandomExtraMessage(builder);
		return result.flatMap(dataMap -> Mono.fromCallable(() -> formatter.format(input, dataMap, builder)))
				.onErrorResume(error -> { error.printStackTrace(); return Mono.just(this.createErrorResponse(input, error)); } );
	}
	
	@Override
	protected void createArgumentSpecifications()
	{
		argumentSpecifications.add(new ArgumentSpec(false, PokemonArgument.class));
	}
	
	private void addTypesToMap(Pokemon pokemon, MultiMap<IFlexObject> map)
	{
		FlexCacheService cache = (FlexCacheService)services.getService(ServiceType.CACHE);
		
		pokemon.getTypes()
			.stream()
			.map(type -> type.getType())
			.map(type -> type.getName())
			.map(type -> cache.getCachedData(CachedResource.TYPE, type))
			.forEach(type -> map.add(type.getClass().getName(), type));
	}
	
	private List<PokeFlexRequest> getPokemonForms(MultiMap<IFlexObject> map)
	{
		List<Pokemon> pokemon = map.get(Pokemon.class.getName())
				.stream()
				.map(pokemonFromMap -> (Pokemon)pokemonFromMap)
				.collect(Collectors.toList());
		
		List<PokeFlexRequest> result = new ArrayList<>();
		for(Pokemon pokemonForm : pokemon)
		{
			for(Form form : pokemonForm.getForms())
				result.add(new RequestURL(form.getUrl(), Endpoint.POKEMON_FORM));
		}
		
		return result;
	}
	
	private EvolutionChain getEvolutionChain(MultiMap<IFlexObject> map)
	{
		return (EvolutionChain)map.getValue(EvolutionChain.class.getName(), 0);
	}
	
	private List<PokeFlexRequest> getPokemonInChain(EvolutionChain chain, Pokemon toIgnore)
	{
		List<PokeFlexRequest> result = new ArrayList<PokeFlexRequest>();
		
		if(!chain.getChain().getSpecies().getName().equals(toIgnore.getName()))
			result.add(new RequestURL(chain.getChain().getSpecies().getUrl(), Endpoint.POKEMON_SPECIES));
		getPokemonInChainRecursive(chain.getChain().getEvolvesTo(), result, toIgnore);
		
		return result;
	}
	
	private void getPokemonInChainRecursive(List<EvolvesTo> evoTo, List<PokeFlexRequest> result, Pokemon toIgnore)
	{
		for(EvolvesTo evo : evoTo)
		{
			if(!evo.getSpecies().getName().equals(toIgnore.getName()))
				result.add(new RequestURL(evo.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES));
			if(!evo.getEvolvesTo().isEmpty())
				getPokemonInChainRecursive(evo.getEvolvesTo(), result, toIgnore);
		}
	}
}