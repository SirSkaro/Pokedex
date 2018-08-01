package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.data_processor.Type;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.AbstractArgument;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class WeakCommand extends AbstractCommand 
{
	public WeakCommand(PokeFlexFactory pff)
	{
		super(pff);
		commandName = "weak".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKE_TYPE_LIST);
		expectedArgRange = new ArgumentRange(1,2);
		factory = pff;
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
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Declare utility variables
		Type type1 = null, type2 = null;
		Pokemon pokemon = null;
		StringBuilder header = new StringBuilder();
		
		//Build reply according to the argument case
		if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON) //argument is a Pokemon
		{	
			//Obtain data
			Object flexObj;
			try 
			{
				flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
				pokemon = Pokemon.class.cast(flexObj);
				List<skaro.pokeflex.objects.pokemon.Type> types = pokemon.getTypes();
				type1 = Type.getByName(types.get(0).getType().getName());
				if(types.size() > 1)
					type2 = Type.getByName(types.get(1).getType().getName());
			} 
			catch(Exception e)
			{ 
				this.addErrorMessage(reply, input, "1006", e); 
				return reply;
			}
			
		}
		else //argument is a list of Types
		{
			type1 = Type.getByName(input.getArg(0).getDbForm());
			if(input.getArgs().size() > 1)
				type2 = Type.getByName(input.getArg(1).getDbForm());
		}
		
		header.append("**__"+type1.toProperName());
		header.append(type2 != null ? "/"+type2.toProperName() : "");
		header.append(pokemon != null ? " ("+TextFormatter.pokemonFlexFormToProper(pokemon.getName())+")__**" : "__**");
		reply.addToReply(header.toString());
		reply.setEmbededReply(formatEmbed(type1, type2));
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Type type1, Type type2)
	{
		int randNum;
		EmbedBuilder builder = new EmbedBuilder();
		TypeInteractionWrapper wrapper = TypeTracker.onDefense(type1, type2);
		builder.setLenient(true);
		
		builder.appendField("Weak:", combineLists(wrapper, 2.0, 4.0), false);
		builder.appendField("Neutral", getList(wrapper, 1.0), false);
		builder.appendField("Resist", combineLists(wrapper, 0.5, 0.25), false);
		builder.appendField("Immune", getList(wrapper, 0.0), false);
		
		//Set color
		builder.withColor(ColorTracker.getColorForWrapper(wrapper));
		
		//set footer with random chance
		randNum = ThreadLocalRandom.current().nextInt(1, 4 + 1); //1 in 4 chance
		if(randNum == 1)
			builder.withFooterText("You may also like the %coverage command!");
		
		return builder.build();
	}
	
	public Response twitchReply(Input input)
	{ 
		return null;
	}
	
	private String combineLists(TypeInteractionWrapper wrapper, double mult1, double mult2)
	{
		Optional<String> strCheck;
		String inter1, intern2;
		StringBuilder builder = new StringBuilder();
		
		strCheck = wrapper.interactionToString(mult1);
		inter1 = strCheck.isPresent() ? strCheck.get() : null;
		
		strCheck = wrapper.interactionToString(mult2);
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
		Optional<String> strCheck = wrapper.interactionToString(mult);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
}