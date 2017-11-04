package skaro.pokedex.input_processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import skaro.pokedex.core.CommandLibrary;
import skaro.pokedex.data_processor.ICommand.ArgumentCategory;
import skaro.pokedex.database_resources.DatabaseInterface;

public class InputProcessor 
{
	private SpellChecker sc;
	private ArgumentMap argMap;
	private DatabaseInterface dbi;
	
	public InputProcessor(CommandLibrary lib)
	{
		dbi = DatabaseInterface.getInstance();
		argMap = new ArgumentMap(lib);
		
		try
		{
			sc = new SpellChecker();
		}
		catch(IOException e)
		{
			System.out.println("Spellchecker could not be initialized");
			System.exit(1);
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * A method to parse and check all user input
	 * @param input - A String array of size two. Index 0 contains the function name, index 1 contains all arguments
	 * @return - Processed input. Null if command is not in the argument map
	 */
	public Input processInput(String input)
	{
		//Utility variables
		String[] args = parseTextMessage(input);
		
		//If args is null, then the input does not match the command format. Discard.
		if(args == null)
			return null;
		
		//More Utility variables
		ArrayList<ArgumentCategory> expectedArgTypes = argMap.get(args[0]);
		Integer[] expectedArgNum = argMap.getExpectedNumber(args[0]);
		Input result;
		ArgumentCategory cursor;
		Argument tempArg;
		
		//If argument is not in the map, the command is not supported
		if(expectedArgTypes == null)
			return null;
		
		result = new Input(args[0].intern());
		
		//No argument case
		if(expectedArgTypes.get(0) == ArgumentCategory.NONE && args[1] == null)
		{
			result.setValid(true);
			return result;
		}
		//If the number of received arguments is not in the expected range, then set the error flag and return
		else if(expectedArgTypes.get(0) != ArgumentCategory.NONE && args[1] == null)
		{
			result.setValid(false);
			result.setError(1);
			return result;
		}
		
		args = args[1].split(",");
		
		if(!(args.length >= expectedArgNum[0].intValue() && args.length <= expectedArgNum[1].intValue()))
		{
			result.setValid(false);
			result.setError(1);
			return result;
		}
		
		//Check individual arguments to see if they are valid
		for(int i = 0; i < expectedArgTypes.size(); i++)
		{
			tempArg = null;
			args[i] = args[i].trim();
			cursor = expectedArgTypes.get(i);
			
			//Check for option select categories
			switch(cursor)
			{
				case POKE_ABIL:
					tempArg = setUpArg(ArgumentCategory.POKEMON, args[i]);
					if(!tempArg.isValid())
						tempArg = setUpArg(ArgumentCategory.ABILITY, args[i]);
					consolodate(result, tempArg);
					break;
				default:
					break;
			}
				
			//Check for list categories
			if(tempArg == null)
			switch(cursor)
			{
				case MOVE_LIST:
					for(; i < args.length; i++)
					{
						tempArg = setUpArg(ArgumentCategory.MOVE, args[i].trim());
						consolodate(result, tempArg);
					}
					break;
				case MOVE_TYPE_LIST:
					for(; i < args.length; i++)
					{
						tempArg = setUpArg(ArgumentCategory.TYPE, args[i].trim());
						if(!tempArg.isValid())
							tempArg = setUpArg(ArgumentCategory.MOVE, args[i].trim());
						consolodate(result, tempArg);
					}
					break;
				case TYPE_LIST:
					for(; i < args.length; i++)
					{
						tempArg = setUpArg(ArgumentCategory.TYPE, args[i].trim());
						consolodate(result, tempArg);
					}
					break;
				case POKE_TYPE_LIST:
					tempArg = setUpArg(ArgumentCategory.POKEMON, args[i].trim());
					if(!tempArg.isValid())//if it's not a Pokemon, check for Type
					{
						for(; i < args.length; i++)
						{
							tempArg = setUpArg(ArgumentCategory.TYPE, args[i].trim());
							consolodate(result, tempArg);
						}
					}
					else
						consolodate(result, tempArg);
					break;
				default:
					break;
			}
				
			//All other categories
			if(tempArg == null)
			{
				tempArg = setUpArg(cursor, args[i]);
				consolodate(result, tempArg);
			}
		}
		
		return result;
	}
	
	/**
	 * A method to add an Argument to the Input and check for validity
	 * @param input
	 * @param arg
	 */
	public void consolodate(Input input, Argument arg)
	{
		//Add the argument to the input
		input.addArg(arg);
		
		//Check if the argument was valid
		if(!arg.isValid())
		{
			input.setValid(false);
			input.setError(2);
		}
	}
	
	/**
	 * A method to set up any argument with spell checking.
	 * @param ac - category of argument
	 * @param resource - argument
	 * @return - A new instance of an Argument object.
	 */
	public Argument setUpArg(ArgumentCategory ac, String resource)
	{	
		//Create new argument
		Argument result = new Argument(resource.intern(), dbi.dbFormat(resource).intern(), ac);
		
		//Check if resource is recognized. If it is not recognized, attempt to spell check it.
		//If it is still not recognized, then return the argument as invalid (default)
		if(!dbi.resourceExists(ac, result.getDB()))
		{
			String correction;
			correction = sc.spellCheckArgument(resource, ac);
			
			//If no spell correction occured, return invalid argument
			if(correction != null && correction.equalsIgnoreCase(resource))
				return result;
			
			result.setDB(dbi.dbFormat(correction).intern());
			
			//If argument is still not recognized, return invalid argument
			if(!dbi.resourceExists(ac, result.getDB()))
				return result;
			
			//Otherwise, spell check was successful
			result.setRaw(correction.intern());
			result.setChecked(true);
		}
		
		result.setValid(true);
		
		return result;
	}
	
	/**
     * A method to parse an incoming message. Tests if the message is a command that
     * the bot replies textually to. If it is a command
     * then the message is organized into a String array
     * @param msg - the message to be parsed
     * @return a string array of size 2. Index 0 contains the command name and index 1 contains the arguments
     * @return null if parsed message does not match the regex expression
     */
    public String[] parseTextMessage(String msg)
    {	    	
    	if(msg == null || msg.trim().equals(""))
    		return null;
    	
    	//Pattern matching variables
    	Pattern p1;
    	Matcher m1;
    	String[] result = new String[2]; //result[0] contains the command, result[1] contains the argument

    	//Current recognized prefixes: '!' and '%'
    	if(msg.substring(0, 1).equals("!") || msg.substring(0, 1).equals("%"))
    	{	
			//format: [prefix] [command] [args]
			p1 = Pattern.compile("[\\p{Punct}][a-zA-Z]+[\\s]+.+");
			m1 = p1.matcher(msg);
			 
			//This is a command with an argument
			if(m1.matches())
			{	        	 
				result[0] = msg.substring(1, msg.indexOf(" ")).toLowerCase();
				result[1] = msg.substring(msg.indexOf(" ") + 1).trim();
				
				return result;
			}
			
			//format: [prefix] [command]
			p1 = Pattern.compile("[\\p{Punct}][a-zA-Z]+[\\s]*");
			m1 = p1.matcher(msg);
			 
			//This is a command with no argument
			if(m1.matches())
			{
				result[0] = msg.substring(1).trim().toLowerCase();
				result[1] = null;
				
				return result;
			}
    	}
    	
    	//Other formats: command(args) or command()
    	if(msg.endsWith(")"))
    	{
    		//format: [command] [(] [args] [)]
			p1 = Pattern.compile("[a-zA-Z]+[\\s]*[(].+[)]");
			m1 = p1.matcher(msg);
			 
			//This is a command with an argument
			if(m1.matches())
			{	        	 
				result[0] = msg.substring(0, msg.indexOf("(")).toLowerCase();
				result[1] = msg.substring(msg.indexOf("(") + 1, msg.indexOf(")")).trim();
				
				return result;
			}
			
			//format: [prefix] [command]
			p1 = Pattern.compile("[a-zA-Z]+[\\s]*[(][)]");
			m1 = p1.matcher(msg);
			 
			//This is a command with no argument
			if(m1.matches())
			{
				result[0] = msg.substring(0, msg.indexOf("(")).toLowerCase();
				result[1] = null;
	
				return result;
			}
    	}
 
		//Patterns do not match
		return null;
    }
}
