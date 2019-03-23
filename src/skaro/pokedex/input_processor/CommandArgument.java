package skaro.pokedex.input_processor;

import java.util.Optional;

import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;

public abstract class CommandArgument 
{
	protected String rawInput;	
	protected String dbForm;		
	protected String flexForm;
	protected boolean valid;		
	protected boolean isSpellChecked;	
	protected ArgumentCategory category;	
	protected SQLResource sqlResource;
	protected static MySQLManager sqlManager = MySQLManager.getInstance();
	protected static SpellChecker sc = SpellChecker.getInstance();
	
	public String getRawInput() { return rawInput; }
	public String getDbForm() { return dbForm; }
	public String getFlexForm() { return flexForm; }
	public boolean isValid() { return valid; }
	public boolean isSpellChecked() { return isSpellChecked; }
	public ArgumentCategory getCategory() { return category; }
	
	@Override
	public String toString()
	{
		return rawInput;
	}
	
	public void setUp(String argument, Language lang)
	{
		this.dbForm = TextUtility.dbFormat(argument, lang);
		this.rawInput = argument;
		Optional<String> flexFormCheck = sqlManager.getFlexFormOfResource(this.sqlResource, this.dbForm, lang);
		
		if(!flexFormCheck.isPresent())
		{
			String correction = sc.spellCheckResource(sqlResource, argument, lang);
			flexFormCheck = sqlManager.getFlexFormOfResource(this.sqlResource, correction, lang);
			
			if(!flexFormCheck.isPresent())
			{
				this.valid = false;
				return;
			}
			
			this.dbForm = TextUtility.dbFormat(correction, lang).intern();
			this.rawInput = correction.intern();
			this.isSpellChecked = true;
		}
		
		this.valid = true;
		this.flexForm = flexFormCheck.get();
	}
}
