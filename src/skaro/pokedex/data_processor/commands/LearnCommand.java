package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.LearnMethodData;
import skaro.pokedex.data_processor.LearnMethodWrapper;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.FlexCacheService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokedex.services.FlexCacheService.CachedResource;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.evolution_chain.EvolutionChain;
import skaro.pokeflex.objects.evolution_chain.EvolvesTo;
import skaro.pokeflex.objects.pokemon.Move;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class LearnCommand extends PokedexCommand
{
	public LearnCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "learn".intern();
		orderedArgumentCategories.add(ArgumentCategory.POKEMON);
		orderedArgumentCategories.add(ArgumentCategory.MOVE_LIST);
		expectedArgRange = new ArgumentRange(2,5);
		
		aliases.put("knows", Language.ENGLISH);
		aliases.put("erlernen", Language.GERMAN);
		aliases.put("aprender", Language.SPANISH);
		aliases.put("apprentissage", Language.FRENCH);
		aliases.put("imparare", Language.ITALIAN);
		aliases.put("manabu", Language.JAPANESE_HIR_KAT);
		aliases.put("xuéxí", Language.CHINESE_SIMPMLIFIED);
		aliases.put("xuexi", Language.CHINESE_SIMPMLIFIED);
		aliases.put("baeuda", Language.KOREAN);
		
		aliases.put("学ぶ", Language.JAPANESE_HIR_KAT);
		aliases.put("学习", Language.CHINESE_SIMPMLIFIED);
		aliases.put("배우다", Language.KOREAN);
		
		createHelpMessage("primal groudon, roar, attract", "Mew, Thunder, Iron tail, Ice Beam, Stealth Rock", "Golurk, Fly", "gible, earthquake, dual chop",
				"https://i.imgur.com/EkXAXCP.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<pokemon>, <move>,...,<move>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK, ServiceType.CACHE);
	}
	
	@Override
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					return false;
				default:
					break;
			}
			
			//Because inputs that are not valid (case 2) are allowed this far, it is necessary to check if
			//the Pokemon is valid but allow other arguments to go unchecked
			if(!input.getArgument(0).isValid())
			{
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild)
	{ 
		if(!inputIsValid(null, input))
			return Mono.just(formatter.invalidInputResponse(input));
		
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		LearnMethodData learnMethodData = (LearnMethodData)((FlexCacheService)services.getService(ServiceType.CACHE)).getCachedData(CachedResource.LEARN_METHOD);
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result;
		List<PokeFlexRequest> initialRequests = new ArrayList<>();
		MultiMap<IFlexObject> dataToFormat = new MultiMap<>();
		
		for(int i = 1; i < input.getArguments().size(); i++)
		{
			CommandArgument arg = input.getArgument(i);
			if(arg.isValid())
				initialRequests.add(new Request(Endpoint.MOVE, arg.getFlexForm()));
			else
				dataToFormat.add(LearnMethodWrapper.class.getName(), new LearnMethodWrapper(arg.getRawInput()));
		}
		
		//Get data of Pokemon
		initialRequests.add(new Request(Endpoint.POKEMON, input.getArgument(0).getFlexForm()));
		
		result = Mono.just(dataToFormat)
				.flatMap(dataMap -> Flux.fromIterable(initialRequests)
					.parallel()
					.runOn(factory.getScheduler())
					.flatMap(request -> request.makeRequest(factory))
					.doOnNext(flexObject -> dataMap.add(flexObject.getClass().getName(), flexObject))
					.sequential()
					.ofType(Pokemon.class)
					.flatMap(pokemon -> this.addAdopter(pokemon, builder))
					.flatMap(pokemon -> Mono.just(dataMap.get(skaro.pokeflex.objects.move.Move.class.getName()))
							.ofType(List.class)
							.flatMap(movesToCheckFor -> Mono.just(new RequestURL(pokemon.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES))
									.flatMap(request -> request.makeRequest(factory))
									.ofType(PokemonSpecies.class)
									.doOnNext(sepcies -> dataMap.put(PokemonSpecies.class.getName(), sepcies))
									.flatMap(species -> Mono.just(new RequestURL(species.getEvolutionChain().getUrl(), Endpoint.EVOLUTION_CHAIN))
											.flatMap(request -> request.makeRequest(factory))
											.ofType(EvolutionChain.class)
											.flatMap(evolutionChain -> Flux.fromIterable(getAllPreEvolutions(evolutionChain, species))
												.parallel()
												.runOn(factory.getScheduler())
												.flatMap(request -> request.makeRequest(factory))
												.sequential()
												.ofType(PokemonSpecies.class)
												.map(preEvoSpecies -> new Request(Endpoint.POKEMON, String.valueOf(preEvoSpecies.getId())))
												.flatMap(preEvoPokemonRequest -> preEvoPokemonRequest.makeRequest(factory))
												.ofType(Pokemon.class)
												.collectList()
												.map(preEvoList -> getAllLearnableMoves(pokemon, preEvoList))
												.flatMap(allLearnableMoves -> Flux.fromIterable(movesToCheckFor)
														.ofType(skaro.pokeflex.objects.move.Move.class)
														.map(moveToCheckFor -> new LearnMethodWrapper(allLearnableMoves.get(((skaro.pokeflex.objects.move.Move)moveToCheckFor).getName()), (skaro.pokeflex.objects.move.Move)moveToCheckFor, learnMethodData))
														.doOnNext(methodWrapper -> dataMap.add(LearnMethodWrapper.class.getName(), (IFlexObject) methodWrapper))
														.then(Mono.just(dataMap)))))))
					.then(Mono.just(dataMap)));
			
			this.addRandomExtraMessage(builder);
			return result
					.map(dataMap -> formatter.format(input, dataMap, builder))
					.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
	}
	
	private Map<String, Move> getAllLearnableMoves(Pokemon thisPokemon, List<Pokemon> preEvolutions)
	{
		Map<String, Move> result = new HashMap<>();
		
		for(Move move : thisPokemon.getMoves())
			result.put(move.getMove().getName(), move);
		
		for(Pokemon pokemon : preEvolutions)
			for(Move move : pokemon.getMoves())
				result.put(move.getMove().getName(), move);
		
		return result;
	}
	
	private List<PokeFlexRequest> getAllPreEvolutions(EvolutionChain chain, PokemonSpecies thisPokemon)
	{
		List<PokeFlexRequest> result = new ArrayList<PokeFlexRequest>();
		
		if(chain.getChain().getSpecies().getName().equals(thisPokemon.getName()))
			return result;
		
		result.add(new RequestURL(chain.getChain().getSpecies().getUrl(), Endpoint.POKEMON_SPECIES));
		
		List<EvolvesTo> evoTo = chain.getChain().getEvolvesTo();
		while(evoTo != null && !evoTo.isEmpty())
		{
			if(pokemonInEvolvesTo(thisPokemon, evoTo))
				break;
			
			EvolvesTo evoStage = evoTo.get(0); //Assume only one Pokemon is in the evolution branch at this stage
			result.add(new RequestURL(evoStage.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES));
			evoTo = evoStage.getEvolvesTo();
		}
		
		return result;
	}
	
	private boolean pokemonInEvolvesTo(PokemonSpecies pokemon, List<EvolvesTo> evoTo)
	{
		for(EvolvesTo evo : evoTo)
			if(evo.getSpecies().getName().equals(pokemon.getName()))
				return true;
		return false;
	}
	
}