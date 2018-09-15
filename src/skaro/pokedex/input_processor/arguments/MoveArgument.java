package skaro.pokedex.input_processor.arguments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.SpellChecker;

public class MoveArgument extends AbstractArgument {

	@Override
	public void setUp(String argument, Language lang) 
	{
		//Utility variables
		SpellChecker sc = SpellChecker.getInstance();
		
		//Set up argument
		this.dbForm = TextFormatter.dbFormat(argument);
		this.cat = ArgumentCategory.MOVE;
		this.rawInput = argument;
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!isMove(this.dbForm, lang))
		{
			String correction;
			correction = sc.spellCheckMove(argument);
			
			this.dbForm = TextFormatter.dbFormat(correction).intern();
			if(!isMove(this.dbForm, lang))
			{
				this.valid = false;
				return;
			}
			
			this.rawInput = correction.intern();
			this.spellChecked = true;
		}
		
		this.valid = true;
		this.flexForm = sqlManager.getMoveFlexForm(dbForm).get();
	}

	private boolean isMove(String s, Language lang)
	{
		String attribute = (lang == Language.ENGLISH ? "mid" : lang.getSQLAttribute());
		String value = (lang == Language.ENGLISH ? s + "-m" : s);
		
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT "+attribute+" FROM Move WHERE "+attribute+" = '"+value+"';");
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
