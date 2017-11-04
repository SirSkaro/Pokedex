package skaro.pokedex.input_processor;

import skaro.pokedex.data_processor.ICommand.ArgumentCategory;

public class Argument 
{
	private String rawForm;	//The user's raw input
	private String dbForm;	//user's input converted to the database's format
	private boolean valid;	//Flag to track if argument is valid
	private boolean spellChecked;	//Flag to track if argument needed to be spell checked
	private ArgumentCategory cat;	//Category of the argument
	
	public Argument()
	{
		rawForm = null;
		dbForm = null;
		valid = false;
		spellChecked = false;
		cat = null;
	}
	
	public Argument(String raw, String db)
	{
		rawForm = raw;
		dbForm = db;
		valid = false;
		spellChecked = false;
		cat = null;
	}
	
	public Argument(String raw, String db, ArgumentCategory ac)
	{
		rawForm = raw;
		dbForm = db;
		valid = false;
		spellChecked = false;
		cat = ac;
	}
	
	/**
	 * Get and Set methods
	 */
	public void setRaw(String s) { rawForm = s; }
	public void setDB(String s) { dbForm = s; }
	public void setValid(boolean b) { valid = b; }
	public void setChecked(boolean b) { spellChecked = b; }
	public void setCategory(ArgumentCategory ac) { cat = ac; }
	
	public String getRaw() { return rawForm; }
	public String getDB() { return dbForm; }
	public boolean isValid() { return valid; }
	public boolean wasChecked() { return spellChecked; }
	public ArgumentCategory getCategory() { return cat; }
}
