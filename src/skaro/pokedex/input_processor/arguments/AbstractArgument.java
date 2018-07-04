package skaro.pokedex.input_processor.arguments;

import skaro.pokedex.input_processor.MySQLManager;

public abstract class AbstractArgument 
{
	protected String rawInput;		//The user's raw input
	protected String dbForm;		//user's input converted to the database's format
	protected String flexForm;
	protected boolean valid;		//Flag to track if argument is valid
	protected boolean spellChecked;	//Flag to track if argument needed to be spell checked
	protected ArgumentCategory cat;	//Category of the argument
	protected static MySQLManager sqlManager = MySQLManager.getInstance();
	
	abstract public void setUp(String argument);
	
	public String getRawInput() { return rawInput; }
	public String getDbForm() { return dbForm; }
	public String getFlexForm() { return flexForm; }
	public boolean isValid() { return valid; }
	public boolean isSpellChecked() { return spellChecked; }
	public ArgumentCategory getCategory() { return cat; }
	
	public String toString()
	{
		return dbForm;
	}
}
