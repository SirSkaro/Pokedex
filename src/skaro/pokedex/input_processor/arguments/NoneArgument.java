package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class NoneArgument extends CommandArgument 
{
	public NoneArgument()
	{
		
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{
		this.valid = true;
	}
}
