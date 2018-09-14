package skaro.pokedex.data_processor;

import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public interface IDiscordFormatter 
{
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder);
	public Response invalidInputResponse(Input input);
	
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
