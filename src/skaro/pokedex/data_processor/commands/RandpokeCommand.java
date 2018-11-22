package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.RandpokeResponseFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class RandpokeCommand extends AbstractCommand 
{
	public RandpokeCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "randpoke".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		aliases.put("rand", Language.ENGLISH);
		aliases.put("randompoke", Language.ENGLISH);
		aliases.put("randompokemon", Language.ENGLISH);
		aliases.put("randpokemon", Language.ENGLISH);
		aliases.put("zufällig", Language.GERMAN);
		aliases.put("zufallig", Language.GERMAN);
		aliases.put("casuale", Language.ITALIAN);
		aliases.put("mujagwiui", Language.KOREAN);
		aliases.put("suíjī", Language.CHINESE_SIMPMLIFIED);
		aliases.put("suiji", Language.CHINESE_SIMPMLIFIED);
		aliases.put("aleatorio", Language.SPANISH);
		aliases.put("randamu", Language.JAPANESE_HIR_KAT);
		aliases.put("hasard", Language.FRENCH);
		
		aliases.put("ランダム", Language.JAPANESE_HIR_KAT);
		aliases.put("随机", Language.CHINESE_SIMPMLIFIED);
		aliases.put("무작위의", Language.KOREAN);
		
		formatter = new RandpokeResponseFormatter();
		extraMessages.add("See the shiny with the %shiny command! (Patrons only)");
		this.createHelpMessage("https://i.imgur.com/cOEo8jW.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "none"; }

	@Override
	public Response discordReply(Input input, IUser requester)
	{
	
		try
		{
			MultiMap<Object> dataMap = new MultiMap<Object>();
			EmbedBuilder builder = new EmbedBuilder();
			int randDexNum = ThreadLocalRandom.current().nextInt(1, 807 + 1);
			List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
			List<Object> flexData = new ArrayList<Object>();
			
			concurrentRequestList.add(new Request(Endpoint.POKEMON, Integer.toString(randDexNum)));
			concurrentRequestList.add(new Request(Endpoint.POKEMON_SPECIES, Integer.toString(randDexNum)));

			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			Pokemon pokemon = (Pokemon)dataMap.getValue(Pokemon.class.getName(), 0);
			
			addAdopter(pokemon, builder);
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1002", e); 
			return response;
		}
		
	}
}
