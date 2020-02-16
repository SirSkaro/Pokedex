package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class NatureArgument extends CommandArgument 
{
	public NatureArgument()
	{
		super("Nature", SQLResource.NATURE);
	}
	
}
