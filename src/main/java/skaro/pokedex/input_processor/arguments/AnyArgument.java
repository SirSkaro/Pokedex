package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class AnyArgument extends CommandArgument 
{
	public AnyArgument()
	{
		super("Anything", null);
	}
	
	@Override
	public void setUp(String argument, Language lang)
	{
		this.dbForm = TextUtility.dbFormat(argument, lang);
		this.rawInput = argument;
		this.valid = true;
	}
}
