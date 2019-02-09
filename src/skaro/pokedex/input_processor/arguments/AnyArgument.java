package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class AnyArgument extends CommandArgument 
{
	public AnyArgument()
	{
		
	};
	
	@Override
	public void setUp(String argument, Language lang)
	{
		this.dbForm = TextFormatter.dbFormat(argument, lang);
		this.category = ArgumentCategory.ANY_NONE;
		this.rawInput = argument;
		this.valid = true;
	}
}
