package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.DexResponseFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class DexCommand extends AbstractCommand
{
	public DexCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "dex".intern();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.VERSION);
		expectedArgRange = new ArgumentRange(2,2);
		formatter = new DexResponseFormatter();
		
		aliases.put("pokedex", Language.ENGLISH);
		aliases.put("entry", Language.ENGLISH);
		aliases.put("giib", Language.KOREAN);
		aliases.put("entrada", Language.SPANISH);
		aliases.put("iscrizione", Language.ITALIAN);
		aliases.put("eintrag", Language.GERMAN);
		aliases.put("entrée", Language.FRENCH);
		aliases.put("entree", Language.FRENCH);
		aliases.put("tiáomù", Language.CHINESE_SIMPMLIFIED);
		aliases.put("entori", Language.JAPANESE_HIR_KAT);
		
		extraMessages.add("Connect to a voice channel to hear entries spoken! (English, German, Italian, and French only)");
		
		createHelpMessage("Mew, Red", "kadabra, fire red", "Phantump, y", "Darumaka, white",
				"https://i.imgur.com/AvJMBpR.gif");
		
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>, <version>"; }
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify a Pokemon and a Version as input for this command "
							+ "(seperated by commas).");
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(AbstractArgument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
				break;
				default:
					reply.addToReply("A technical error occured (code 110)");
			}
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input, IUser requester)
	{
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		Request request;
		RequestURL requestURL;
		
		//Obtain data
		try
		{
			//Pokemon
			request = new Request(Endpoint.POKEMON);
			request.addParam(input.getArg(0).getFlexForm());
			concurrentRequestList.add(request);
			
			//Version
			request = new Request(Endpoint.VERSION);
			request.addParam(input.getArg(1).getFlexForm());
			concurrentRequestList.add(request);
			
			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			//PokemonSpecies
			Pokemon pokemon = (Pokemon)dataMap.getValue(Pokemon.class.getName(), 0);
			requestURL = new RequestURL(pokemon.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES);
			PokemonSpecies species = (PokemonSpecies)factory.createFlexObject(requestURL);
			dataMap.put(PokemonSpecies.class.getName(), species);
			
			//Add adopter
			this.addAdopter(pokemon, builder);
			this.addRandomExtraMessage(builder);
			
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
