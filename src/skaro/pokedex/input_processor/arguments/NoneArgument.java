package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class NoneArgument extends CommandArgument 
{
	public NoneArgument()
	{
		this.valid = true;
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{ }
}
