package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class TypeArgument extends CommandArgument 
{
	public TypeArgument()
	{
		super();
		this.category = ArgumentCategory.TYPE;
		this.sqlResource = SQLResource.TYPE;
	}
}
