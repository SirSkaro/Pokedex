package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

/**
 * An interface for all Command objects. These objects format replies for users.
 * Each Command is a singleton class.
 *
 */
public interface ICommand 
{	
	//Basic data getters
	public ArgumentRange getExpectedArgumentRange();	//The min and max number of arguments expected (size always 2)
	public String getCommandName();			//The name of the command
	public ArrayList<ArgumentCategory> getArgumentCats();	//The categories of the expected argument(s)
	
	//Response functions
	public Response discordReply(Input input, IUser requester);	//Format a reply
	public String getArguments();				//Get the arguments in a response-friendly form
	public boolean inputIsValid(Response reply, Input input);	//Check if user input is valid
	
	public default String listToItemizedString(List<?> list)
	{
		if(list.isEmpty())
			return "None".intern();
		
		StringBuilder result = new StringBuilder();
		int i;
		for(i = 0; i < list.size() - 1; i++)
			if(i %2 == 0)
				result.append(list.get(i).toString() + "*/* ");
			else
				result.append(list.get(i).toString() + "\n");

		result.append(list.get(i).toString());
		
		return result.toString();
	}
	
	public default void addErrorMessage(Response reply, Input input, String errCode, Exception e)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		reply.addToReply("**Error Report**");
		builder.withDesc("Could not get requested data. My external API may be down or may not have your data. Please try again later.\n\n"
				+ "If you think this is a bug, please __screenshot this report__ and post it in the Support Server!");
		builder.appendField("Error Code", errCode, true);
		builder.appendField("Technical Error", e.getClass().getSimpleName(), true);
		builder.appendField("User Input", input.argsToString(), true);
		builder.appendField("Link to Support Server", "[Click here to report](https://discord.gg/D5CfFkN)", true);
		
		reply.setEmbededReply(builder.build());
	}
}

