package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class AbilityArgument extends CommandArgument 
{
	public AbilityArgument()
	{
		super();
		this.category = ArgumentCategory.ABILITY;
		this.sqlResource = SQLResource.ABILITY;
	}
}
