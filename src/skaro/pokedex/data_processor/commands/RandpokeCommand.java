package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.RandpokeResponseFormatter;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
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
	
	public Response discordReply2(Input input, IUser requester)
	{
		//Set up utility variables
		Response reply = new Response();
		
		//Obtain data
		int randDexNum = ThreadLocalRandom.current().nextInt(1, 807 + 1);
		List<String> urlParams = new ArrayList<String>();
		urlParams.add(Integer.toString(randDexNum));
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, urlParams);
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			//Format reply
			reply.setEmbededReply(formatEmbed(pokemon));
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1002", e);}
				
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		builder.withTitle(TextFormatter.flexFormToProper(pokemon.getName()) + " | #" + Integer.toString(pokemon.getId()));
		
		//Add images
		builder.withImage(pokemon.getModel().getUrl());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		//Add adopter
		addAdopter(pokemon, builder);
		
		this.addRandomExtraMessage(builder);
		
		return builder.build();
	}
}
