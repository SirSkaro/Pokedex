package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.formatters.WeakResponseFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.Type;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class WeakCommand extends AbstractCommand 
{
	public WeakCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "weak".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKE_TYPE_LIST);
		expectedArgRange = new ArgumentRange(1,2);
		factory = pff;
		formatter = new WeakResponseFormatter();
		
		aliases.put("weakness", Language.ENGLISH);
		aliases.put("debilidad", Language.SPANISH);
		aliases.put("faiblesses", Language.FRENCH);
		aliases.put("debole", Language.ITALIAN);
		aliases.put("schwach", Language.GERMAN);
		aliases.put("yowai", Language.JAPANESE_HIR_KAT);
		aliases.put("ruò", Language.CHINESE_SIMPMLIFIED);
		aliases.put("ruo", Language.CHINESE_SIMPMLIFIED);
		aliases.put("yagjeom", Language.KOREAN);
		
		aliases.put("弱い", Language.JAPANESE_HIR_KAT);
		aliases.put("弱", Language.CHINESE_SIMPMLIFIED);
		aliases.put("약점", Language.KOREAN);
		
		extraMessages.add("You may also like the %coverage command");
		
		createHelpMessage("Ghost, Normal", "Scizor", "Swampert", "Fairy",
				"https://i.imgur.com/E79RCZO.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon> or <type> or <type>, <type>"; }
	
	public Response discordReply(Input input, IUser requester)
	{ 
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		
		try
		{
			//Gather data according to the argument case
			if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON) //argument is a Pokemon
			{	
				Pokemon pokemon = (Pokemon)factory.createFlexObject(new Request(Endpoint.POKEMON, input.getArg(0).getFlexForm()));
				dataMap.put(Pokemon.class.getName(), pokemon);
				
				Object flexObj = factory.createFlexObject(pokemon.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES);
				dataMap.put(PokemonSpecies.class.getName(), flexObj);
				
				for(Type type : pokemon.getTypes())
				{
					TypeData typeData = TypeData.getByName(type.getType().getName());
					dataMap.add(TypeData.class.getName(), typeData);
				}
				
				this.addAdopter(pokemon, builder);
			}
			else //data is a list of types
			{
				for(AbstractArgument arg : input.getArgs())
				{
					TypeData typeData = TypeData.getByName(arg.getFlexForm());
					dataMap.add(TypeData.class.getName(), typeData);
				}
			}
			
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1006", e); 
			return response;
		}
	}
}