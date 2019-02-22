package skaro.pokedex.input_processor;

import skaro.pokedex.input_processor.arguments.ArgumentCategory;

public abstract class CommandArgument 
{
	protected String rawInput;	
	protected String dbForm;		
	protected String flexForm;
	protected boolean valid;		
	protected boolean isSpellChecked;	
	protected ArgumentCategory category;	
	protected static MySQLManager sqlManager = MySQLManager.getInstance();
	
	abstract public void setUp(String argument, Language lang);
	
	public String getRawInput() { return rawInput; }
	public String getDbForm() { return dbForm; }
	public String getFlexForm() { return flexForm; }
	public boolean isValid() { return valid; }
	public boolean isSpellChecked() { return isSpellChecked; }
	public ArgumentCategory getCategory() { return category; }
	
	public String toString()
	{
		return rawInput;
	}
}
