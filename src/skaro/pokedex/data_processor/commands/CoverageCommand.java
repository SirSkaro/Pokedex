package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.Type;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.move.Move;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class CoverageCommand extends AbstractCommand 
{
	public CoverageCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "coverage".intern();
		argCats.add(ArgumentCategory.MOVE_TYPE_LIST);
		expectedArgRange = new ArgumentRange(1,4);
		aliases.put("strong", Language.ENGLISH);
		aliases.put("cov", Language.ENGLISH);
		aliases.put("effective", Language.ENGLISH);
		
		extraMessages.add("You may also like the %weak command!");
		
		createHelpMessage("ice, electric", "blizzard, thunder", "Ghost, Fire, Vine Whip, Hyper Beam", "Water",
				"https://i.imgur.com/MLIpXYN.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<type/move>,...,<type/move>"; }
	
	protected boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify between 1 to 4 Types or Moves as input for this command "
							+ "(seperated by commas).");
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(AbstractArgument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized Type or Move.");
					reply.addToReply("\n*top suggestion*: did you include commas between inputs?");
				break;
				default:
					reply.addToReply("A technical error occured (code 107)");
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
		
		//If argument is a move, get the typing
		TypeInteractionWrapper wrapper;
		ArrayList<Type> typeList = new ArrayList<Type>();
		ArrayList<String> moveNames = new ArrayList<String>();
		
		for(int i = 0; i < input.getArgs().size(); i++)
		{
			if(input.getArg(i).getCategory() == ArgumentCategory.TYPE)
				typeList.add(Type.getByName(input.getArg(i).getDbForm()));
			else	//Category is ArgumentCategory.MOVE
				moveNames.add(input.getArg(i).getFlexForm());
		}
		
		//If the user included Moves in their input, then request the Move's data from the FlexAPI
		//and add it to the list of types
		if(!moveNames.isEmpty())
		{
			try
			{
				List<Type> typesFromMoves = getMoveTypes(moveNames);
				typeList.addAll(typesFromMoves);
			}
			catch(Exception e)
			{
				this.addErrorMessage(reply, input, "1009", e);
				return reply;
			}
		}
		wrapper = TypeTracker.onOffense(typeList);
		
		//Build reply
		reply.addToReply("**__"+wrapper.typesToString()+"__**");
		reply.setEmbededReply(formatEmbed(wrapper));
		
		return reply;
	}
	
	private EmbedObject formatEmbed(TypeInteractionWrapper wrapper)
	{
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		builder.appendField("Super Effective", getList(wrapper, 2.0), false);
		builder.appendField("Neutral", getList(wrapper, 1.0), false);
		builder.appendField("Resistant", getList(wrapper, 0.5), false);
		builder.appendField("Immune", getList(wrapper, 0.0), false);
		builder.withColor(ColorTracker.getColorForWrapper(wrapper));
		
		this.addRandomExtraMessage(builder);
		return builder.build();
	}
	
	private List<Type> getMoveTypes(List<String> moveNames) throws InterruptedException, PokeFlexException
	{
		List<Object> flexObjs = getMoveFlexObjs(moveNames);
		List<Type> result = new ArrayList<Type>();
		Move move;
		Type type;
		
		for(Object obj : flexObjs)
		{			
			move = Move.class.cast(obj);
			type = Type.getByName(move.getType().getName());
			result.add(type);
		}
		
		return result;
	}
	
	private List<Object> getMoveFlexObjs(List<String> moveNames) throws InterruptedException, PokeFlexException
	{
		ArrayList<String> urlParams;
		List<Request> requests = new ArrayList<Request>();
		
		for(String move :moveNames)
		{
			urlParams = new ArrayList<String>();
			urlParams.add(move);
			requests.add(new Request(Endpoint.MOVE, urlParams));
		}
		
		return factory.createFlexObjects(requests);
	}
	
	private String getList(TypeInteractionWrapper wrapper, double mult)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
}