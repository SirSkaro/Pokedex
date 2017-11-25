package skaro.pokedex.data_processor.commands;

import java.awt.Color;
import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.ComplexItem;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class ItemCommand implements ICommand 
{
	private static ItemCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private ItemCommand()
	{
		commandName = "item".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.ITEM);
		expectedArgRange = new Integer[]{1,1};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new ItemCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[item name]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("You must specify exactly one Item as input for this command.".intern());
				break;
				case 2:
					reply.addToReply("\""+input.getArg(0).getRaw() +"\" is not a recognized Item");
				break;
				default:
					reply.addToReply("A technical error occured (code 104)");
			}
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		ComplexItem item = dbi.extractComplexItemFromDB(input.getArg(0).getDB()+"-i");
		
		//If data is null, then an error occurred
		if(item.getName() == null)
		{
			reply.addToReply("A technical error occured (code 1005). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Organize the data and add it to the reply
		reply.addToReply(("**__"+item.getName()+"__**").intern());
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		builder.appendField("Item Category", item.getCategory(), true);
		builder.appendField("Debut", "Gen "+ Integer.toString(item.getDebut()), true);
		
		if(item.getFlingPower() > 0)
			builder.appendField("Fling Base Power", Integer.toString(item.getFlingPower()), true);
		if(item.getNGType() != null)
			builder.appendField("Natural Gift Type", item.getNGType(), true);
		if(item.getNGPower() > 0)
			builder.appendField("Natural Gift Power", Integer.toString(item.getNGPower()), true);
		
		builder.appendField("Game Description", item.getShortDesc(), false);
		builder.appendField("Technical Description", item.getTechDesc(), false);
		
		//Set embed color
		builder.withColor(new Color(0xE89800));
		
		reply.setEmbededReply(builder.build());
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		ComplexItem item = dbi.extractComplexItemFromDB(input.getArg(0).getDB()+"-i");
		
		//If data is null, then an error occurred
		if(item.getName() == null)
		{
			reply.addToReply("A technical error occured (code 1005). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Organize the data and add it to the reply
		reply.addToReply("*"+item.getName()+"*");
		
		reply.addToReply("Category:"+item.getCategory());
		reply.addToReply("Description:"+item.getShortDesc());
		reply.addToReply("Debut:Gen "+item.getDebut());
		if(item.getFlingPower() > 0)
			reply.addToReply("Fling Power:"+item.getFlingPower());
		if(item.getNGType() != null)
			reply.addToReply("Natural Gift Type:"+item.getNGType());
		if(item.getNGPower() > 0)
			reply.addToReply("Natural Gift Power:"+item.getNGPower());
		
		return reply;
	}
}
