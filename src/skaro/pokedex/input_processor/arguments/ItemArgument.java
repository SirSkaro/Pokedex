package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.SQLResource;

public class ItemArgument extends CommandArgument 
{
	public ItemArgument()
	{
		super();
		this.category = ArgumentCategory.ITEM;
		this.sqlResource = SQLResource.ITEM;
	}
}
