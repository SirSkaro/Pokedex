package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class MoveArgument extends CommandArgument 
{
	public MoveArgument()
	{
		super();
		this.category = ArgumentCategory.MOVE;
		this.sqlResource = SQLResource.MOVE;
	}
}
