package skaro.pokedex.data_processor;

import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.IFlexObject;

public interface ResponseFormatter 
{
	public Response invalidInputResponse(Input input);
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder);
	
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
