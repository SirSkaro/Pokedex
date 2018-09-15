package skaro.pokedex.data_processor.commands;

import java.awt.Color;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.item.Item;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class ItemCommand extends AbstractCommand
{
	public ItemCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "item".intern();
		argCats.add(ArgumentCategory.ITEM);
		expectedArgRange = new ArgumentRange(1,1);
		aliases.put("itm", Language.ENGLISH);
		
		createHelpMessage("Life Orb", "leftovers", "Choice Band", "eviolite",
				"https://i.imgur.com/B1NlcYh.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<item>"; }
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify exactly one Item as input for this command.".intern());
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Item");
				break;
				default:
					reply.addToReply("A technical error occured (code 104)");
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
			Object flexObj = factory.createFlexObject(Endpoint.ITEM, input.argsAsList());
			Item item = Item.class.cast(flexObj);
			
			//Format reply
			reply.addToReply(("**__"+TextFormatter.flexFormToProper(item.getName())+"__**").intern());
			reply.setEmbededReply(formatEmbed(item));
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1004", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Item item)
	{
		//Organize the data and add it to the reply
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		builder.appendField("Item Category", TextFormatter.flexFormToProper(item.getCategory().getName()), true);
		builder.appendField("Debut", TextFormatter.formatGeneration(item.getDebut()), true);
		
		if(item.getFlingPower() > 0)
			builder.appendField("Fling Base Power", Integer.toString(item.getFlingPower()), true);
		if(item.getNgType() != null)
			builder.appendField("Natural Gift Type", item.getNgType().toString(), true);
		if((Integer)item.getNgPower() != null)
			builder.appendField("Natural Gift Power", Integer.toString((Integer)item.getNgPower()), true);
		
		builder.appendField("Game Description", item.getSdesc(), false);
		builder.appendField("Technical Description", item.getLdesc(), false);
		
		builder.withColor(new Color(0xE89800));
		builder.withThumbnail(item.getSprites().getDefault());
		
		this.addRandomExtraMessage(builder);
		return builder.build();
	}
}
