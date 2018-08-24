package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.data_processor.TextFormatter;

public class AnyArgument extends AbstractArgument 
{
	public AnyArgument()
	{
		
	};
	
	@Override
	public void setUp(String argument)
	{
		this.dbForm = TextFormatter.dbFormat(argument);
		this.cat = ArgumentCategory.ANY_NONE;
		this.rawInput = argument;
		this.valid = true;
	}
}
