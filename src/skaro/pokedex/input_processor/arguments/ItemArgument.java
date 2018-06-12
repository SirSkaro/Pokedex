package skaro.pokedex.input_processor.arguments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.SpellChecker;

public class ItemArgument extends AbstractArgument {

	@Override
	public void setUp(String argument)
	{
		//Utility variables
		SpellChecker sc = SpellChecker.getInstance();
		
		//Set up argument
		this.dbForm = TextFormatter.dbFormat(argument);
		this.cat = ArgumentCategory.ITEM;
		this.rawInput = argument;
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!isItem(this.dbForm))
		{
			String correction;
			correction = sc.spellCheckItem(argument);
			
			if(!isItem(correction))
			{
				this.valid = false;
				return;
			}
			
			this.dbForm = TextFormatter.dbFormat(correction).intern();
			this.rawInput = correction.intern();
			this.spellChecked = true;
		}
		
		this.valid = true;
		this.flexForm = sqlManager.getItemFlexForm(dbForm).get();
	}
	
	private boolean isItem(String s)
	{
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT iid FROM Item WHERE iid = '"+s+"-i';");
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
