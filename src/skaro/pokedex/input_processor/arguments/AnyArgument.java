package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Language;

public class AnyArgument extends AbstractArgument 
{
	public AnyArgument()
	{
		
	};
	
	@Override
	public void setUp(String argument, Language lang)
	{
		this.dbForm = TextFormatter.dbFormat(argument);
		this.cat = ArgumentCategory.ANY_NONE;
		this.rawInput = argument;
		this.valid = true;
	}
}
