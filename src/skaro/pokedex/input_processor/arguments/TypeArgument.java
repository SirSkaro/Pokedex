package skaro.pokedex.input_processor.arguments;

import java.util.ArrayList;
import java.util.List;

import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.SpellChecker;

public class TypeArgument extends AbstractArgument 
{
	private static List<String> types;
	
	static
	{
		types = new ArrayList<String>();
		
		types.add("normal"); types.add("fighting"); types.add("flying"); types.add("poison"); 
		types.add("ground"); types.add("rock"); types.add("bug"); types.add("water");
		types.add("ghost"); types.add("steel"); types.add("fire"); types.add("electric");
		types.add("grass"); types.add("psychic"); types.add("ice"); types.add("dragon");
		types.add("dark"); types.add("fairy");
	}
	
	public TypeArgument()
	{
		
	}

	public void setUp(String argument, Language lang) 
	{
		//Utility variables
		SpellChecker sc = SpellChecker.getInstance();
		
		//Set up argument
		this.dbForm = TextFormatter.dbFormat(argument, lang);
		this.cat = ArgumentCategory.TYPE;
		this.rawInput = argument;
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!isType(this.dbForm))
		{
			String correction;
			correction = sc.spellCheckType(argument, lang);
			
			if(!isType(correction))
			{
				this.valid = false;
				return;
			}
			
			this.dbForm = TextFormatter.dbFormat(correction, lang).intern();
			this.rawInput = correction.intern();
			this.spellChecked = true;
		}
		
		this.valid = true;
		this.flexForm = this.dbForm;
	}
	
	private boolean isType(String s)
	{
		return types.contains(s);
	}
}
