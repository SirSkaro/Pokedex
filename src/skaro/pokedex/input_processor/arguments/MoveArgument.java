package skaro.pokedex.input_processor.arguments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.SpellChecker;

public class MoveArgument extends AbstractArgument {

	@Override
	public void setUp(String argument) 
	{
		//Utility variables
		SpellChecker sc = SpellChecker.getInstance();
		
		//Set up argument
		this.dbForm = TextFormatter.dbFormat(argument);
		this.cat = ArgumentCategory.MOVE;
		this.rawInput = argument;
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!isMove(this.dbForm))
		{
			String correction;
			correction = sc.spellCheckMove(argument);
			
			this.dbForm = TextFormatter.dbFormat(correction).intern();
			if(!isMove(this.dbForm))
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

	private boolean isMove(String s)
	{
		Optional<ResultSet> resultOptional = sqlManager.dbQuery("SELECT mid FROM Move WHERE mid = '"+s+"-m';");
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