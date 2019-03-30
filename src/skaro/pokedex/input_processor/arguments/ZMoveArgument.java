package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class ZMoveArgument extends CommandArgument
{
	public ZMoveArgument()
	{
		super();
		this.category = ArgumentCategory.ZMOVE;
		this.sqlResource = SQLResource.ZMOVE;
	}
}
