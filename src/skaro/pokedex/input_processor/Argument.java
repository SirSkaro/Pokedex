package skaro.pokedex.input_processor;

import skaro.pokedex.data_processor.ICommand.ArgumentCategory;

public class Argument 
{
	private String rawForm;	//The user's raw input
	private String dbForm;	//user's input converted to the database's format
	private String flexForm;
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
		flexForm = null;
	}
	
	public Argument(String raw, String db)
	{
		rawForm = raw;
		dbForm = db;
		valid = false;
		spellChecked = false;
		cat = null;
		flexForm = null;
	}
	
	public Argument(String raw, String db, ArgumentCategory ac)
	{
		rawForm = raw;
		dbForm = db;
		valid = false;
		spellChecked = false;
		cat = ac;
		flexForm = null;
	}
	
	public Argument(String raw, String db, String flex, ArgumentCategory ac)
	{
		rawForm = raw;
		dbForm = db;
		valid = false;
		spellChecked = false;
		cat = ac;
		flexForm = flex;
	}
	
	/**
	 * Get and Set methods
	 */
	public void setRaw(String s) { rawForm = s; }
	public void setDB(String s) { dbForm = s; }
	public void setValid(boolean b) { valid = b; }
	public void setChecked(boolean b) { spellChecked = b; }
	public void setCategory(ArgumentCategory ac) { cat = ac; }
	public void setFlexForm(String s) { flexForm = s; }
	
	public String getRaw() { return rawForm; }
	public String getDB() { return dbForm; }
	public boolean isValid() { return valid; }
	public boolean wasChecked() { return spellChecked; }
	public ArgumentCategory getCategory() { return cat; }
	public String getFlex() { return flexForm; }
}
