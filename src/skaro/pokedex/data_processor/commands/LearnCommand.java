package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.LearnMethodWrapper;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.evolution_chain.EvolutionChain;
import skaro.pokeflex.objects.evolution_chain.EvolvesTo;
import skaro.pokeflex.objects.pokemon.Move;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class LearnCommand extends AbstractCommand
{
	public LearnCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "learn".intern();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.MOVE_LIST);
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
		
		createHelpMessage("primal groudon, roar, attract", "Mew, Thunder, Iron tail, Ice Beam, Stealth Rock, Spikes", "Golurk, Fly", "gible, earthquake, dual chop",
				"https://i.imgur.com/EkXAXCP.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>, <move>,...,<move>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK);
	}
	
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
			if(!input.getArg(0).isValid())
			{
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Response discordReply(Input input, IUser requester)
	{ 
		//Check if input is valid
		if(!inputIsValid(null, input))
			return formatter.invalidInputResponse(input);
		
		PokeFlexFactory factory;
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		List<PokeFlexRequest> concurrentRequsts = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		List<LearnMethodWrapper> methodWrappers = new ArrayList<LearnMethodWrapper>(4);
		List<skaro.pokeflex.objects.move.Move> movesToCheckFor; 
		
		try
		{
			factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			
			//Get data of Pokemon
			concurrentRequsts.add(new Request(Endpoint.POKEMON, input.getArg(0).getFlexForm()));
			
			//Get data for every valid move
			for(int i = 1; i < input.getArgs().size(); i++)
			{
				AbstractArgument arg = input.getArg(i);
				if(arg.isValid())
					concurrentRequsts.add(new Request(Endpoint.MOVE, arg.getFlexForm()));
				else
					methodWrappers.add(new LearnMethodWrapper(arg.getRawInput()));
			}
			
			//Get data from PokeFlex
			flexData = factory.createFlexObjects(concurrentRequsts);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			flexData.clear();
			Pokemon pokemon = (Pokemon)dataMap.getValue(Pokemon.class.getName(), 0);
			movesToCheckFor = (List<skaro.pokeflex.objects.move.Move>)(List<?>)dataMap.get(skaro.pokeflex.objects.move.Move.class.getName());
			
			/* Round 1 of concurrent requests */
			//Pokemon's species data
			PokemonSpecies species = (PokemonSpecies)factory.createFlexObject(pokemon.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES);
			dataMap.put(PokemonSpecies.class.getName(), species);
			
			/* Round 2 of concurrent requests */
			//Evolution chain
			EvolutionChain evoChain = (EvolutionChain)factory.createFlexObject(new RequestURL(species.getEvolutionChain().getUrl(), Endpoint.EVOLUTION_CHAIN));
			
			/* Round 3 of concurrent requests */
			//Pre-Evolutions
			concurrentRequsts = getAllPreEvolutions(evoChain, species);
			if(!concurrentRequsts.isEmpty())
			{
				flexData = factory.createFlexObjects(concurrentRequsts);
				concurrentRequsts.clear();
				
				/* Round 4 of concurrent requests */
				//Get the /pokemon endpoint data of each pre evolution
				for(Object obj : flexData)
				{
					species = (PokemonSpecies)obj;
					concurrentRequsts.add(new Request(Endpoint.POKEMON, Integer.toString(species.getId())));
				}
				
				//Get data from PokeFlex
				flexData = factory.createFlexObjects(concurrentRequsts);
			}
			
			//Finish creating all method wrappers and add them to the data map
			if(movesToCheckFor != null && !movesToCheckFor.isEmpty())
			{
				Map<String, Move> allLearnableMoves = getAllLearnableMoves(pokemon, flexData);
				for(skaro.pokeflex.objects.move.Move move : movesToCheckFor)
					methodWrappers.add(new LearnMethodWrapper(allLearnableMoves, move));
			}
			
			for(LearnMethodWrapper wrapper : methodWrappers)
				dataMap.add(LearnMethodWrapper.class.getName(), wrapper);
			
			this.addAdopter(pokemon, builder);
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1007", e);
			e.printStackTrace();
			return response;
		}
	}
	
	private Map<String, Move> getAllLearnableMoves(Pokemon thisPokemon, List<Object> preEvolutions)
	{
		Map<String, Move> result = new HashMap<>();
		
		for(Move move : thisPokemon.getMoves())
			result.put(move.getMove().getName(), move);
		
		for(Object pokemon : preEvolutions)
			for(Move move : ((Pokemon)pokemon).getMoves())
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