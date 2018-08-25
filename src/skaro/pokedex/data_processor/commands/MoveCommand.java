package skaro.pokedex.data_processor.commands;

import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.move.Move;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class MoveCommand extends AbstractCommand 
{
	public MoveCommand(PokeFlexFactory pff)
	{
		super(pff);
		commandName = "move".intern();
		argCats.add(ArgumentCategory.MOVE);
		expectedArgRange = new ArgumentRange(1,1);
		aliases.add("mv");
		aliases.add("attack");
		
		createHelpMessage("Ember", "dragon ascent", "aeroblast", "Blast Burn",
				"https://i.imgur.com/B3VtWyg.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<move>"; }
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify exactly one Move as input for this command.".intern());
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Move");
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
		
		try 
		{
			//Obtain data
			Object flexObj = factory.createFlexObject(Endpoint.MOVE, input.argsAsList());
			Move move = Move.class.cast(flexObj);
			
			//Format reply
			reply.addToReply(("**__"+TextFormatter.flexFormToProper(move.getName())+"__**").intern());
			reply.setEmbededReply(formatEmbed(move));
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1006", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Move move)
	{
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		if(move.getPower() != 0)
			builder.appendField("Base Power", Integer.toString(move.getPower()), true);
		if(move.getZPower() != 0)
			builder.appendField("Z-Base Power", Integer.toString(move.getZPower()), true);
		if(move.getCrystal() != null)
			builder.appendField("Z-Crystal", move.getCrystal().toString(), true);
		builder.appendField("Accuracy", (move.getAccuracy() != 0 ? Integer.toString(move.getAccuracy()) : "-"), true);
		builder.appendField("Category", TextFormatter.flexFormToProper(move.getDamageClass().getName()), true);
		builder.appendField("Type", TextFormatter.flexFormToProper(move.getType().getName()), true);
		builder.appendField("Base PP", Integer.toString(move.getPp()), true);
		builder.appendField("Max PP", Integer.toString(move.getMaxPp()), true);
		if(move.getZBoost() != null)
			builder.appendField("Z-Boosts", move.getZBoost().toString(), true);
		if(move.getZEffect() != null)
			builder.appendField("Z-Effect", move.getZEffect().toString(), true);
		builder.appendField("Priority", Integer.toString(move.getPriority()), true);
		builder.appendField("Target", TextFormatter.flexFormToProper(move.getTarget().getName()), true);
		if(move.getContestType() != null)
			builder.appendField("Contest Category", TextFormatter.flexFormToProper(move.getContestType().getName()), true);
		builder.appendField("Game Description", move.getSdesc(), false);
		builder.appendField("Technical Description", move.getLdesc(), false);
		
		if(move.getFlags() != null)
		{
			StringBuilder flagBuilder = new StringBuilder();
			for(String flag : move.getFlags())
				flagBuilder.append(flag + " ");
			
			builder.appendField("Other Properties", flagBuilder.toString(), false);
		}
		
		//Set embed color
		builder.withColor(ColorTracker.getColorForType(move.getType().getName()));
		this.addRandomExtraMessage(builder);
		
		return builder.build();
	}
}
