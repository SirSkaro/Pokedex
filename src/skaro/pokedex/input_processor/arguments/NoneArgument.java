package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Language;

public class NoneArgument extends AbstractArgument 
{
	public NoneArgument()
	{
		
	}
	
	public void setUp(String argument, Language lang) 
	{
		this.valid = true;
	}
}
