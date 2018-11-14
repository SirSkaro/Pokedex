package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.data_processor.formatters.WeakResponseFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.Type;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
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
		
		aliases.put("debilidad", Language.SPANISH);
		aliases.put("faiblesses", Language.FRENCH);
		aliases.put("debole", Language.ITALIAN);
		aliases.put("schwach", Language.GERMAN);
		aliases.put("yowai", Language.JAPANESE_HIR_KAT);
		aliases.put("ruò", Language.CHINESE_SIMPMLIFIED);
		aliases.put("ruo", Language.CHINESE_SIMPMLIFIED);
		aliases.put("yagjeom", Language.KOREAN);
		
		extraMessages.add("You may also like the %coverage command");
		
		createHelpMessage("Ghost, Normal", "Scizor", "Swampert", "Fairy",
				"https://i.imgur.com/E79RCZO.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon> or <type> or <type>, <type>"; }
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify 1 Pokemon or between 1 and 2 Types (seperated by commas) "
							+ "as input for this command.");
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(AbstractArgument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: did you include commas between inputs?");
				break;
				default:
					reply.addToReply("A technical error occured (code 106)");
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
		
		try
		{
			//Gather data according to the argument case
			if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON) //argument is a Pokemon
			{	
				List<PokeFlexRequest> concurrentRequsts = new ArrayList<PokeFlexRequest>();
				concurrentRequsts.add(new Request(Endpoint.POKEMON, input.getArg(0).getFlexForm()));
				concurrentRequsts.add(new Request(Endpoint.POKEMON_SPECIES, input.getArg(0).getFlexForm()));
				List<Object> flexData = factory.createFlexObjects(concurrentRequsts);
				
				//Add all data to the map again
				for(Object obj : flexData)
					dataMap.add(obj.getClass().getName(), obj);
				
				//Get data for the Typing of the Pokemon
				Pokemon pokemon = (Pokemon)dataMap.getValue(Pokemon.class.getName(), 0);
				
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
			e.printStackTrace();
			return response;
		}
	}
	
	public Response discordReply2(Input input, IUser requester)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Declare utility variables
		TypeData type1 = null, type2 = null;
		Pokemon pokemon = null;
		StringBuilder header = new StringBuilder();
		Optional<String> model = Optional.empty();
		
		//Build reply according to the argument case
		if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON) //argument is a Pokemon
		{	
			//Obtain data
			Object flexObj;
			try 
			{
				flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
				pokemon = Pokemon.class.cast(flexObj);
				model = Optional.ofNullable(pokemon.getSprites().getFrontDefault());
				List<Type> types = pokemon.getTypes();
				type1 = TypeData.getByName(types.get(0).getType().getName());
				if(types.size() > 1)
					type2 = TypeData.getByName(types.get(1).getType().getName());
			} 
			catch(Exception e)
			{ 
				this.addErrorMessage(reply, input, "1006", e); 
				return reply;
			}
			
		}
		else //argument is a list of Types
		{
			type1 = TypeData.getByName(input.getArg(0).getDbForm());
			if(input.getArgs().size() > 1)
				type2 = TypeData.getByName(input.getArg(1).getDbForm());
		}
		
		if(pokemon != null)
		{
			header.append("**__"+TextFormatter.pokemonFlexFormToProper(pokemon.getName())+" ");
			header.append("("+type1.toProperName());
			header.append(type2 != null ? "/"+type2.toProperName() +")__**": ")__**");
		}
		else
		{
			header.append("**__"+type1.toProperName());
			header.append(type2 != null ? "/"+type2.toProperName() +"__**": "__**");
		}
		
		reply.addToReply(header.toString());
		reply.setEmbededReply(formatEmbed(type1, type2, Optional.ofNullable(pokemon), model));
		
		return reply;
	}
	
	private EmbedObject formatEmbed(TypeData type1, TypeData type2, Optional<Pokemon> pokemon, Optional<String> model)
	{
		EmbedBuilder builder = new EmbedBuilder();
		TypeInteractionWrapper wrapper = TypeTracker.onDefense(type1, type2);
		builder.setLenient(true);
		
		builder.appendField("Weak", combineLists(wrapper, 2.0, 4.0), false);
		builder.appendField("Neutral", getList(wrapper, 1.0), false);
		builder.appendField("Resist", combineLists(wrapper, 0.5, 0.25), false);
		builder.appendField("Immune", getList(wrapper, 0.0), false);
		
		//Add model if present
		if(model.isPresent())
			builder.withThumbnail(model.get());
		
		//Set color
		builder.withColor(ColorTracker.getColorForWrapper(wrapper));
		
		//Add adopter
		if(pokemon.isPresent())
			addAdopter(pokemon.get(), builder);
		
		this.addRandomExtraMessage(builder);
		return builder.build();
	}
	
	private String combineLists(TypeInteractionWrapper wrapper, double mult1, double mult2)
	{
		Optional<String> strCheck;
		String inter1, intern2;
		StringBuilder builder = new StringBuilder();
		
		strCheck = wrapper.interactionToString(mult1, Language.ENGLISH);
		inter1 = strCheck.isPresent() ? strCheck.get() : null;
		
		strCheck = wrapper.interactionToString(mult2, Language.ENGLISH);
		intern2 = strCheck.isPresent() ? strCheck.get() : null;
		
		if(inter1 == null && intern2 == null)
			return null;
		
		if(inter1 != null)
			builder.append(inter1);
		
		if(inter1 != null && intern2 != null)
			builder.append(", **"+intern2+"**");
		else if(intern2 != null)
			builder.append("**"+intern2+"**");
		
		return builder.toString();
	}
	
	private String getList(TypeInteractionWrapper wrapper, double mult)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult, Language.ENGLISH);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
}