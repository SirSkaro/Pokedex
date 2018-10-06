package skaro.pokedex.data_processor;

import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import sx.blah.discord.util.EmbedBuilder;

public abstract class AbstractFormatter 
{
	protected Map<Language, Enum<?>> fieldMap;
	
	public AbstractFormatter()
	{	}
	
	public abstract Response format(Input input, MultiMap<Object> data, EmbedBuilder builder);
	public abstract Response invalidInputResponse(Input input);
	
	protected String listToItemizedString(List<?> list)
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
