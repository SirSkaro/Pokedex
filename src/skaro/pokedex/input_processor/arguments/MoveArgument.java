package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class MoveArgument extends CommandArgument 
{
	public MoveArgument()
	{
		super("Move", SQLResource.MOVE);
	}
}
