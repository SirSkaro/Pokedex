package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.StatsResponseFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class StatsCommand extends AbstractCommand  
{	
	public StatsCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "stats".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new ArgumentRange(1,1);
		factory = pff;
		formatter = new StatsResponseFormatter();
		
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
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify exactly one Pokemon as input for this command.".intern());
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("\""+ input.getArg(0).getRawInput() +"\" is not a recognized Pokemon in " + input.getLanguage().getName());
				break;
				default:
					reply.addToReply("A technical error occured (code 101)");
			}
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		try
		{
			MultiMap<Object> dataMap = new MultiMap<Object>();
			EmbedBuilder builder = new EmbedBuilder();
			Object flexObj;
			
			//Initial data - Item object
			Pokemon pokemon = (Pokemon)factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
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