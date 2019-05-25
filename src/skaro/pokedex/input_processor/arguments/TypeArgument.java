package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class TypeArgument extends CommandArgument 
{
	public TypeArgument()
	{
		super("Typing", SQLResource.TYPE);
	}
}
