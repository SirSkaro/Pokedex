package skaro.pokedex.input_processor.arguments;

import java.util.ArrayList;
import java.util.List;

import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.SpellChecker;

public class VersionArgument extends AbstractArgument
{
	private static List<String> versions;
	
	static
	{
		versions = new ArrayList<String>();
		versions.add("red"); versions.add("blue"); versions.add("yellow"); versions.add("gold"); versions.add("silver");
		versions.add("crystal"); versions.add("ruby"); versions.add("sapphire");
		versions.add("emerald"); versions.add("leafgreen"); versions.add("firered"); versions.add("diamond");
		versions.add("pearl"); versions.add("platinum"); versions.add("black");
		versions.add("black2"); versions.add("white"); versions.add("white2"); versions.add("heartgold");
		versions.add("soulsilver"); versions.add("x"); versions.add("y");
		versions.add("omegaruby"); versions.add("alphasapphire"); versions.add("sun"); versions.add("moon");
		versions.add("ultrasun"); versions.add("ultramoon");
	}
	
	public VersionArgument()
	{
		
	}

	public void setUp(String argument, Language lang) 
	{
		//Utility variables
		SpellChecker sc = SpellChecker.getInstance();
		
		//Set up argument
		this.dbForm = TextFormatter.dbFormat(argument);
		this.cat = ArgumentCategory.VERSION;
		this.rawInput = argument;
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!isVersion(this.dbForm))
		{
			String correction;
			correction = sc.spellCheckVersion(argument, lang);
			
			if(!isVersion(correction))
			{
				this.valid = false;
				return;
			}
			
			this.dbForm = TextFormatter.dbFormat(correction).intern();
			this.rawInput = correction.intern();
			this.spellChecked = true;
		}
		
		this.valid = true;
		this.flexForm = this.dbForm;
	}
	
	private boolean isVersion(String s)
	{
		return versions.contains(s);
	}
}
