package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class StatsCommand extends AbstractCommand  
{	
	public StatsCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "stats".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
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
	
	public boolean makesWebRequest() { return true; }	
	public String getArguments() { return "<pokemon>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK);
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		try
		{
			PokeFlexFactory factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			MultiMap<Object> dataMap = new MultiMap<Object>();
			EmbedBuilder builder = new EmbedBuilder();
			Object flexObj;
			
			//Initial data - Pokemon data
			Pokemon pokemon = (Pokemon)factory.createFlexObject(new Request(Endpoint.POKEMON, input.getArg(0).getFlexForm()));
			dataMap.put(Pokemon.class.getName(), pokemon);
			
			flexObj = factory.createFlexObject(pokemon.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES);
			dataMap.put(PokemonSpecies.class.getName(), flexObj);
			
			this.addAdopter(pokemon, builder);
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1001", e); 
			return response;
		}
	}
	
}