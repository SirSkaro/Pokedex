package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import skaro.pokedex.data_processor.SearchCriteriaFilter;
import skaro.pokedex.data_processor.SearchCriteriaFilter.SearchCriteriaBuilder;
import skaro.pokedex.input_processor.ArgumentSpec;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.AbilityArgument;
import skaro.pokedex.input_processor.arguments.MoveArgument;
import skaro.pokedex.input_processor.arguments.TypeArgument;
import skaro.pokedex.services.FlexCacheService;
import skaro.pokedex.services.FlexCacheService.CachedResource;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.type.Type;

public class SearchCommand extends PokedexCommand
{
	private int maxResultCap;
	
	public SearchCommand(PokedexServiceManager serviceManager, ResponseFormatter discordFormatter, int cap) throws ServiceConsumerException
	{
		super(serviceManager, discordFormatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
	
		commandName = "search".intern();
		maxResultCap = cap;
		createArgumentSpecifications();
		
		aliases.put("filter", Language.ENGLISH);
		aliases.put("chercher", Language.FRENCH);
		aliases.put("suchen", Language.GERMAN);
		aliases.put("cercare", Language.ITALIAN);
		aliases.put("chajda", Language.KOREAN);
		aliases.put("buscar", Language.SPANISH);
		aliases.put("motomeru", Language.JAPANESE_HIR_KAT);
		aliases.put("sōusuǒ", Language.CHINESE_SIMPMLIFIED);
		aliases.put("sousuo", Language.CHINESE_SIMPMLIFIED);
		
		aliases.put("求める", Language.JAPANESE_HIR_KAT);
		aliases.put("찾다", Language.KOREAN); 
		aliases.put("搜索", Language.CHINESE_SIMPMLIFIED);
		
		createHelpMessage("Magic Guard, unaware, moonblast, Fairy",
				"fire, drought",
				"Adaptability, Return, Normal",
				"hyper beam, ice beam, charge beam, solar beam");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }

	@Override
	public String getArguments()
	{
		return "list of <move> (max of 4) and/or <ability> (max of 3) and/or <type> (max of 2)";
	}

	@Override
	public Mono<Response> respondTo(Input input, User author, Guild guild)
	{
		if(!input.allArgumentValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		List<CommandArgument> trimmedArguments = dropExcessArguments(input.getArguments());
		Mono<MultiMap<IFlexObject>> result = getDataForArguments(trimmedArguments)
				.collectList()
				.map(flexObjects -> populateMap(flexObjects))
				.flatMap(map -> fetchAndAddPokemonByCriteria(map));
		
		return result.flatMap(dataMap -> Mono.fromCallable(() -> formatter.format(input, dataMap, builder)))
			.onErrorResume(error -> { error.printStackTrace(); return Mono.just(this.createErrorResponse(input, error));});
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.CACHE);
	}
	
	@Override
	protected void createArgumentSpecifications()
	{
		argumentSpecifications = new ArrayList<>();
		
		ArgumentSpec argumentSpec = new ArgumentSpec(false, TypeArgument.class, AbilityArgument.class, MoveArgument.class);
		argumentSpecifications.add(argumentSpec);
		for(int i = 0; i < 8; i++)
		{
			argumentSpec = new ArgumentSpec(true, TypeArgument.class, AbilityArgument.class, MoveArgument.class);
			argumentSpecifications.add(argumentSpec);
		}
	}
	
	private List<CommandArgument> dropExcessArguments(List<CommandArgument> arguments)
	{
		int moveCount = 0;
		int abilityCount = 0;
		int typeCount = 0;
		List<CommandArgument> result = new ArrayList<>();
		
		for(CommandArgument argument : arguments)
		{
			if(argument instanceof MoveArgument && moveCount < 4)
			{
				result.add(argument);
				moveCount++;
			}
			else if(argument instanceof AbilityArgument && abilityCount < 3)
			{
				result.add(argument);
				abilityCount++;
			}
			else if(argument instanceof TypeArgument && typeCount < 2)
			{
				result.add(argument);
				typeCount++;
			}
		}
		
		return result;
	}
	
	private Flux<IFlexObject> getDataForArguments(List<CommandArgument> arguments)
	{
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		List<PokeFlexRequest> requestsToMake = createRequestsFromArguments(arguments);
		List<IFlexObject> cachedData = getCachedDataFromArguments(arguments);
		
		return Flux.fromIterable(requestsToMake)
				.parallel()
				.runOn(factory.getScheduler())
				.flatMap(request -> request.makeRequest(factory))
				.sequential()
				.concatWithValues(cachedData.stream().toArray(IFlexObject[]::new));
		
	}
	
	private MultiMap<IFlexObject> populateMap(List<IFlexObject> requestedData)
	{
		MultiMap<IFlexObject> result = new MultiMap<IFlexObject>();
		
		for(IFlexObject flexObj : requestedData)
			result.add(flexObj.getClass().getName(), flexObj);
		
		return result;
	}
	
	private List<IFlexObject> getCachedDataFromArguments(List<CommandArgument> arguments)
	{
		FlexCacheService cache = (FlexCacheService)services.getService(ServiceType.CACHE);
		
		 return arguments.stream()
				.filter(argument -> argument instanceof TypeArgument)
				.map(argument -> (TypeArgument)argument)
				.map(type -> cache.getCachedData(CachedResource.TYPE, type.getFlexForm()))
				.collect(Collectors.toList());
	}
	
	private List<PokeFlexRequest> createRequestsFromArguments(List<CommandArgument> arguments)
	{
		List<PokeFlexRequest> result = new ArrayList<>();
		Map<String, Endpoint> endpoints = new HashMap<>();
		endpoints.put(MoveArgument.class.getName(), Endpoint.MOVE);
		endpoints.put(AbilityArgument.class.getName(), Endpoint.ABILITY);
		
		for(CommandArgument argument : arguments)
		{
			String argumentClassName = argument.getClass().getName();
			if(!endpoints.containsKey(argumentClassName))
				continue;
			
			Endpoint endpoint = endpoints.get(argumentClassName);
			result.add(new Request(endpoint, argument.getFlexForm()));
		}
		
		return result;
	}
	
	private Mono<MultiMap<IFlexObject>> fetchAndAddPokemonByCriteria(MultiMap<IFlexObject> searchCriteria)
	{
		SearchCriteriaFilter search = createFilter(searchCriteria);
		List<PokeFlexRequest> pokemonRequests = createPokemonRequests(search);
		Flux<IFlexObject> pokemonData = makePokemonRequests(pokemonRequests);
		
		return pokemonData.collectList()
				.map(pokemon -> populateMap(pokemon))
				.doOnNext(result -> result.add(SearchCriteriaFilter.class.getName(), search))
				.doOnNext(result -> result.addAllValues(searchCriteria));
	}
	
	private Flux<IFlexObject> makePokemonRequests(List<PokeFlexRequest> requests)
	{
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		return Flux.fromIterable(requests)
				.take(maxResultCap)
				.parallel()
				.runOn(factory.getScheduler())
				.flatMap(request -> request.makeRequest(factory))
				.map(pokemon -> (Pokemon)pokemon)
				.map(pokemon -> new RequestURL(pokemon.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES))
				.flatMap(request -> request.makeRequest(factory))
				.sequential();
	}
	
	private List<PokeFlexRequest> createPokemonRequests(SearchCriteriaFilter search)
	{
		return search.getPokemonThatMeetCriteria()
				.stream()
				.map(pokemon -> new Request(Endpoint.POKEMON, pokemon))
				.collect(Collectors.toList());
	}
	
	private SearchCriteriaFilter createFilter(MultiMap<IFlexObject> searchCriteria)
	{
		SearchCriteriaBuilder builder = SearchCriteriaBuilder.newInstance();
		
		if(searchCriteria.containsKey(Ability.class.getName()))
		{
			List<Ability> abilities = searchCriteria.get(Ability.class.getName())
					.stream()
					.map(ability -> (Ability)ability)
					.collect(Collectors.toList());
			builder.withAbilities(abilities);
		}
		
		if(searchCriteria.containsKey(Type.class.getName()))
		{
			List<Type> types = searchCriteria.get(Type.class.getName())
					.stream()
					.map(type -> (Type)type)
					.collect(Collectors.toList());
			builder.withTypes(types);
		}
		
		if(searchCriteria.containsKey(Move.class.getName()))
		{
			List<Move> moves = searchCriteria.get(Move.class.getName())
					.stream()
					.map(move -> (Move)move)
					.collect(Collectors.toList());
			builder.withMoves(moves);
		}
		
		return builder.build();
	}

}
