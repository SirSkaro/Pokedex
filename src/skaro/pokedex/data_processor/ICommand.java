package skaro.pokedex.data_processor;

import java.util.ArrayList;

import skaro.pokedex.input_processor.Input;

/**
 * An interface for all Command objects. These objects format replies for users.
 * Each Command is a singleton class.
 *
 */
public interface ICommand 
{	
	//Basic data getters
	public Integer[] getExpectedArgNum();	//The min and max number of arguments expected (size always 2)
	public String getCommandName();			//The name of the command
	public ArrayList<ArgumentCategory> getArgumentCats();	//The categories of the expected argument(s)
	
	//Response functions
	public Response discordReply(Input input);	//Format a reply for Discord
	public Response twitchReply(Input input);	//Format a reply for Twitch
	public String getArguments();				//Get the arguments in a response-friendly form
	public boolean inputIsValid(Response reply, Input input);	//Check if user input is valid
	
	public default String listToItemizedDiscordString(ArrayList<?> list)
	{
		if(list.isEmpty())
			return "None";
		
		StringBuilder result = new StringBuilder();
		int i;
		for(i = 0; i < list.size() - 1; i++)
			if(i %2 == 0)
				result.append(list.get(i).toString() + "*/* ");
			else
				result.append(list.get(i).toString() + "\n");

		result.append(list.get(i).toString());
		
		return result.toString();
	}
	
	public default String listToItemizedTwitchString(ArrayList<?> list)
	{
		StringBuilder result = new StringBuilder();
		int i;
		for(i = 0; i < list.size() - 1; i++)
			result.append(list.get(i).toString() + "/");
		result.append(list.get(i).toString());
		
		return result.toString();
	}
	
	public default void wanringMessage(String err)
	{
		System.err.println("["+Class.class.getName()+"] WARNING: "+err);
	}
	
	public enum ArgumentCategory 	//Argument categories
	{
		POKEMON,		//atomic
		ITEM,			//atomic
		TYPE,			//atomic
		MOVE,			//atomic
		META,			//atomic
		ABILITY,		//atomic
		VERSION,		//atomic
		POKE_ABIL,		//option select
		MOVE_TYPE_LIST, //option select list
		MOVE_LIST,		//list
		POKE_TYPE_LIST,	//option select list
		TYPE_LIST,		//list
		GEN,			//atomic
		NONE;			//atomic
		
		/**
		 * A method to get extract a string that corresponds to a relative URL location from
		 * an atomic category
		 */
		public String toString()
	    {	
			String result;
			
			switch(this)
			{
				case POKEMON:
					result = "Pokemon";
					break;
				case ITEM:
					result = "Item";
					break;
				case TYPE:
					result = "Type";
					break;
				case MOVE:
					result = "Move";
					break;
				case META:
					result = "Meta";
					break;
				case ABILITY:
					result = "Ability";
					break;
				case VERSION:
					result = "Version";
					break;
				case GEN:
					result = "Gen";
					break;
				default:
					result = null;
					break;
			}
			
			return result;
	    }
	}
}

