package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.formatters.DataResponseFormatter;
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
import skaro.pokeflex.objects.pokemon.Ability;
import skaro.pokeflex.objects.pokemon.Form;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.Type;
import skaro.pokeflex.objects.pokemon_species.EggGroup;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import skaro.pokeflex.objects.pokemon_species.Variety;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class DataCommand extends AbstractCommand 
{
	public DataCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "data".intern();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new ArgumentRange(1,1);
		formatter = new DataResponseFormatter();
		
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
		
		extraMessages.add("HD Shiny Pokemon are here! See the shiny with %shiny (Patreons only)");
		
		createHelpMessage("mew", "mega charizard x", "primal-kyogre", "Alolan Raichu",
				"https://i.imgur.com/DZsD3Je.gif");
	}

	public boolean makesWebRequest() { return true; }
	public String getArguments(){ return "<pokemon>"; }
	
	@SuppressWarnings("unchecked")
	public Response discordReply(Input input, IUser requester)
	{
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		Request request;
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		//Obtain data
		try
		{
			//Pokemon
			Pokemon pokemon = (Pokemon)factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
			dataMap.put(Pokemon.class.getName(), pokemon);
			
			//PokemonSpecies
			request = new Request(Endpoint.POKEMON_SPECIES);
			request.addParam(pokemon.getSpecies().getName());
			PokemonSpecies species = (PokemonSpecies)factory.createFlexObject(request);
			dataMap.put(PokemonSpecies.class.getName(), species);
			
			/* Round 1 of concurrent requests */
			//Evolution chain
			concurrentRequestList.add(new RequestURL(species.getEvolutionChain().getUrl(), Endpoint.EVOLUTION_CHAIN));
			
			//Abilities
			for(Ability ability : pokemon.getAbilities())
				concurrentRequestList.add(new RequestURL(ability.getAbility().getUrl(), Endpoint.ABILITY));
			
			//Egg Groups
			for(EggGroup group : species.getEggGroups())
				concurrentRequestList.add(new RequestURL(group.getUrl(), Endpoint.EGG_GROUP));
			
			//Growth Rate
			concurrentRequestList.add(new RequestURL(species.getGrowthRate().getUrl(), Endpoint.GROWTH_RATE));
			
			//Varieties - the Pokemon resource of all forms of this Pokemon
			for(Variety variety : species.getVarieties())
			{
				if(!pokemon.getName().equals(variety.getPokemon().getName()))
					concurrentRequestList.add(new RequestURL(variety.getPokemon().getUrl(), Endpoint.POKEMON));
			}
			
			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			/* Round 2 of concurrent requests */
			concurrentRequestList.clear();
			
			//Pokemon in evolution chain
			concurrentRequestList.addAll(getPokemonInChain((EvolutionChain) dataMap.get(EvolutionChain.class.getName()).get(0), pokemon));
			
			//Forms of other varieties of this Pokemon
			List<Pokemon> formsList = (List<Pokemon>)(List<?>)dataMap.get(Pokemon.class.getName());
			for(Pokemon pokemonForm : formsList)
			{
				//if(pokemon.getId() != pokemonForm.getId())
					for(Form form : pokemonForm.getForms())
						concurrentRequestList.add(new RequestURL(form.getUrl(), Endpoint.POKEMON_FORM));
			}
			
			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map again
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			//Types
			for(Type type : pokemon.getTypes())
				dataMap.add(skaro.pokeflex.objects.type.Type.class.getName(), TypeData.getByName(type.getType().getName()).getType());
			
			//Format all data
			addAdopter(pokemon, builder);
			this.addRandomExtraMessage(builder);
			
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();;
			this.addErrorMessage(response, input, "1002", e);
			return response;
		}
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