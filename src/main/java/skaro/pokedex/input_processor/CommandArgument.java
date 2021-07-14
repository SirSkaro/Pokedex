package skaro.pokedex.input_processor;

import java.util.Optional;

import skaro.pokedex.data_processor.TextUtility;

public abstract class CommandArgument 
{
	protected String argumentName;
	protected String rawInput;
	protected String dbForm;		
	protected String flexForm;
	protected boolean valid;		
	protected boolean isSpellChecked;	
	protected SQLResource sqlResource;
	protected static SpellChecker sc = SpellChecker.getInstance();
	private static MySQLManager sqlManager = MySQLManager.getInstance();
	
	public String getArgumentname() {return argumentName; }
	public String getRawInput() { return rawInput; }
	public String getDbForm() { return dbForm; }
	public String getFlexForm() { return flexForm; }
	public boolean isValid() { return valid; }
	public boolean isSpellChecked() { return isSpellChecked; }
	
	public CommandArgument(String name, SQLResource resource) {
		argumentName = name;
		sqlResource = resource;
	}
	
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
