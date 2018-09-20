package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.AbilityResponseFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class AbilityCommand extends AbstractCommand 
{	
	public AbilityCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "ability".intern();
		argCats.add(ArgumentCategory.POKE_ABIL);
		expectedArgRange = new ArgumentRange(1,1);
		formatter = new AbilityResponseFormatter();
		
		aliases.put("ab", Language.ENGLISH);
		aliases.put("abil", Language.ENGLISH);
		aliases.put("habilidad", Language.SPANISH);
		aliases.put("habil", Language.SPANISH);
		aliases.put("talents", Language.FRENCH);
		aliases.put("talent", Language.FRENCH);
		aliases.put("abilità", Language.ITALIAN);
		aliases.put("abilita", Language.ITALIAN);
		aliases.put("fähigkeiten", Language.GERMAN);
		aliases.put("fahigkeiten", Language.GERMAN);
		aliases.put("tokusei", Language.JAPANESE_HIR_KAT);
		aliases.put("toku", Language.JAPANESE_HIR_KAT);
		aliases.put("tèxìng", Language.CHINESE_SIMPMLIFIED);
		aliases.put("teugseong", Language.KOREAN);
		
		createHelpMessage("Starmie", "Flash Fire", "celebi", "natural cure",
				"https://i.imgur.com/biWBKIL.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon> or <ability>"; }
	
	public Response discordReply(Input input, IUser requester)
	{
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		
		try
		{
			if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
			{
				Object flexObj = factory.createFlexObject(Endpoint.ABILITY, input.argsAsList());
				dataMap.put(Ability.class.getName(), flexObj);
			}
			else//if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON)
			{
				//Pokemon
				Pokemon pokemon = (Pokemon)factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
				dataMap.put(Pokemon.class.getName(), pokemon);
				
				//PokemonSpecies
				Request request = new Request(Endpoint.POKEMON_SPECIES);
				request.addParam(pokemon.getSpecies().getName());
				PokemonSpecies species = (PokemonSpecies)factory.createFlexObject(request);
				dataMap.put(PokemonSpecies.class.getName(), species);
				
				//Abilities
				for(skaro.pokeflex.objects.pokemon.Ability abil : pokemon.getAbilities())
					concurrentRequestList.add(new RequestURL(abil.getAbility().getUrl(), Endpoint.ABILITY));
				
				//Make PokeFlex request
				flexData = factory.createFlexObjects(concurrentRequestList);
				
				//Add all data to the map
				for(Object obj : flexData)
					dataMap.add(obj.getClass().getName(), obj);
			}
			
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			response.addToReply("Get back in there and figure out what you did wrong!");
			e.printStackTrace();
			return response;
		}
	}
}