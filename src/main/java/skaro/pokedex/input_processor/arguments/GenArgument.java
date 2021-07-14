package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public class GenArgument extends CommandArgument 
{
	public GenArgument()
	{
		super("Generation", null);
	}
	
	@Override
	public void setUp(String argument, Language lang) 
	{
		//Set up argument
		this.dbForm = TextUtility.dbFormat(argument, lang).replaceAll("[^0-9]", "");
		this.rawInput = argument;
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!isGen(this.dbForm))
		{
			this.valid = false;
			return;
		}
		
		this.valid = true;
		this.flexForm = this.dbForm;
	}
	
	private boolean isGen(String s)
	{
		if(s.length() != 1)
			return false;
		
        char c = s.charAt(0);
        if (c < '1' || c > '7') 
        	return false;
        
        return true;
	}
}
