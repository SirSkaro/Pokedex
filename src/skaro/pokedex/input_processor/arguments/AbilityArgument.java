package skaro.pokedex.input_processor.arguments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.SpellChecker;

public class AbilityArgument extends AbstractArgument 
{
	public AbilityArgument()
	{
		
	}
	
	public void setUp(String argument) 
	{
		//Utility variables
		SpellChecker sc = SpellChecker.getInstance();
		
		//Set up argument
		this.dbForm = TextFormatter.dbFormat(argument);
		this.cat = ArgumentCategory.ABILITY;
		this.rawInput = argument;
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!isAbility(this.dbForm))
		{
			String correction;
			correction = sc.spellCheckAbility(argument);
			
			if(!isAbility(correction))
			{
				this.valid = false;
				return;
			}
			
			this.dbForm = TextFormatter.dbFormat(correction).intern();
			this.rawInput = correction.intern();
			this.spellChecked = true;
		}
		
		this.valid = true;
		this.flexForm = sqlManager.getAbilityFlexForm(dbForm).get();
	}

	private boolean isAbility(String s)
	{
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT aid FROM Ability WHERE aid = '"+s+"-a';");
		boolean resourceExists = false;
		
		if(resultOptional.isPresent())
		{
			try 
			{ 
				resourceExists = resultOptional.get().next();
				resultOptional.get().close();
			} 
			catch(SQLException e)
			{ return resourceExists; }
		}

		return resourceExists;
	}
}
