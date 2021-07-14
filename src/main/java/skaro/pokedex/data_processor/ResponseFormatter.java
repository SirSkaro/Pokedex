package skaro.pokedex.data_processor;

import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.InvalidCommandArgument;
import skaro.pokeflex.api.IFlexObject;

public interface ResponseFormatter 
{
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder);
	
	default Response invalidInputResponse(Input input)
	{
		Response response = new Response();
		
		response.addToReply("Could not process your request due to the following reasons:");
		
		for(CommandArgument argument : input.getArguments())
		{
			if(argument instanceof InvalidCommandArgument)
			{
				InvalidCommandArgument invalidArgument = (InvalidCommandArgument)argument;
				response.addToReply(invalidArgument.createErrorMessage());
			}
		}
		
		if(input.getArguments().size() > 1) {
			response.addToReply(":question:*hint*: did you add __commas__ between arguments?");
		}
		
		return response;
	}
	
	default String listToItemizedString(List<?> list)
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
}
