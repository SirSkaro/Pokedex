package skaro.pokedex.input_processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;

import skaro.pokedex.data_processor.ICommand.ArgumentCategory;
import skaro.pokedex.database_resources.ResourceManager;

public class SpellChecker 
{
	private SpellDictionary pokeDict;
	private SpellDictionary itemDict;
	private SpellDictionary typeDict;
	private SpellDictionary abilityDict;
	private SpellDictionary moveDict;
	private SpellDictionary versionDict;
	private SpellDictionary regionDict;
	
	public SpellChecker() throws IOException
	{
		pokeDict = new SpellDictionaryHashMap();
		itemDict = new SpellDictionaryHashMap();
		typeDict = new SpellDictionaryHashMap();
		abilityDict = new SpellDictionaryHashMap();
		moveDict = new SpellDictionaryHashMap();
		versionDict = new SpellDictionaryHashMap();
		regionDict = new SpellDictionaryHashMap();
		
		populateDict(pokeDict, "pokemon.txt");
		populateDict(itemDict, "items.txt");
		populateDict(typeDict, "types.txt");
		populateDict(abilityDict, "abilities.txt");
		populateDict(moveDict, "moves.txt");
		populateDict(versionDict, "versions.txt");
		populateDict(regionDict, "regions.txt");
	}
	
	/**
	 * A method to spell check any String
	 * @param arg - The string to spell check
	 * @param ac - the category of the string to check
	 * @return - the corrected String if a correction could be found. The same string if no correction could be found
	 * 		null if no need to spell check
	 */
	public String spellCheckArgument(String arg, ArgumentCategory ac)
	{
		switch(ac)
		{
			case ABILITY:
				return spellCheckAbility(arg);
			case ITEM:
				return spellCheckItem(arg);
			case MOVE:
				return spellCheckMove(arg);
			case POKEMON:
				return spellCheckPokemon(arg);
			case TYPE:
				return spellCheckType(arg);
			case VERSION:
				return spellCheckVersion(arg);
			default:
				return null;
		}
	}
	
	private String getBestSuggestion(SpellDictionary dict, String word)
	{
		List<?> suggestions = dict.getSuggestions(word, 5);
		if(suggestions.isEmpty())	//No suggestions for this word
			return word;
		
		Word best = (Word)dict.getSuggestions(word, 5).get(0);
		return best.getWord();
	}
	
	//Spell checks a Pokemon
	private String spellCheckPokemon(String poke)
	{
		poke = poke.toLowerCase();
		String[] temp = null;
		
		if(poke.contains("-"))
		{
			temp = poke.split("-");
			
			//If more than two dashes are used, return null
			if(temp.length > 3)
				return null;
			
			temp[0] = getBestSuggestion(pokeDict, temp[0]);
			
			//Check for slang that is often used
			if(temp[1].equalsIgnoreCase("male")) //for nidoran male
				temp[1] = "m";
			else if(temp[1].equalsIgnoreCase("female"))//for nidoran female
				temp[1] = "f";
			else if(temp[1].equalsIgnoreCase("t"))//For genie forms
				temp[1] = "therian";
			//Rotom forms
			else if(temp[1].equalsIgnoreCase("W"))
				temp[1] = "wash";
			else if(temp[1].equalsIgnoreCase("M"))
				temp[1] = "mow";
			else if(temp[1].equalsIgnoreCase("F"))
				temp[1] = "frost";
			else if(temp[1].equalsIgnoreCase("H"))
				temp[1] = "heat";
			else
				temp[1] = getBestSuggestion(pokeDict, temp[1]);
			
			//System.out.println(temp[1]);
			
			return temp[0]+"-"+temp[1]+((temp.length == 3) ? "-"+temp[2] : "");
		}
		else if(poke.contains(" "))
		{
			temp = poke.split(" ");
			
			//If more than two spaces are used, return null
			if(temp.length > 3)
				return null;
			
			temp[0] = getBestSuggestion(pokeDict, temp[0]);
			
			//Check for slang that is often used
			if(temp[1].equalsIgnoreCase("male")) //for nidoran male
				temp[1] = "m";
			else if(temp[1].equalsIgnoreCase("female"))//for nidoran female
				temp[1] = "f";
			else if(temp[1].equalsIgnoreCase("t"))//For genie forms
				temp[1] = "therian";
			//Rotom forms
			else if(temp[1].equalsIgnoreCase("W"))
				temp[1] = "wash";
			else if(temp[1].equalsIgnoreCase("M"))
				temp[1] = "mow";
			else if(temp[1].equalsIgnoreCase("F"))
				temp[1] = "frost";
			else if(temp[1].equalsIgnoreCase("H"))
				temp[1] = "heat";
			else
				temp[1] = getBestSuggestion(pokeDict, temp[1]);
			
			return temp[0]+" "+temp[1]+((temp.length == 3) ? " "+temp[2] : "");
		}
		
		else
			return getBestSuggestion(pokeDict, poke);
	}
	
	//Spell checks abilities
	private String spellCheckAbility(String abil)
	{
		abil = abil.toLowerCase();
		String[] temp = abil.split(" ");
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < temp.length; i++)
		output.append(getBestSuggestion(abilityDict, temp[i]) + " ");
		
		return output.toString().trim();
	}
	
	//Spell checks items
	private String spellCheckItem(String item)
	{
		item = item.toLowerCase();
		String[] temp = item.split(" ");
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < temp.length; i++)
			output.append(getBestSuggestion(itemDict, temp[i]) + " ");
			
		return output.toString().trim();
	}
	
	//Spell checks moves
	private String spellCheckMove(String move)
	{
		move = move.toLowerCase();
		String[] temp = move.split(" ");
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < temp.length; i++)
			output.append(getBestSuggestion(moveDict, temp[i]) + " ");
		
		return output.toString().trim();
	}
	
	//Spell checks types
	private String spellCheckType(String type)
	{
		type = type.toLowerCase();
		String[] temp = type.split(" ");
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < temp.length; i++)
			output.append(getBestSuggestion(typeDict, temp[i]) + " ");
		
		return output.toString().trim();
	}
	
	//Spell checks region
	@SuppressWarnings("unused")
	private String spellCheckRegion(String reg)
	{
		reg = reg.toLowerCase();
		return getBestSuggestion(regionDict, reg);
	}
	
	//Spell checks artist
	private String spellCheckVersion(String ver)
	{
		StringBuilder version = new StringBuilder(ver.toLowerCase());
		int index;
		
		//Check for abbreviations
		if((index = version.indexOf("1")) != -1)
			version.replace(index, index + 1, "");
			//version.replace("1", "");
		else if((index = version.indexOf("one")) != -1)
			version.replace(index, index + 3, "");
			//ver.replace("one", "");
		else if((index = version.indexOf("two")) != -1)
			version.replace(index, index + 3, "2");
			//ver.replace("two", "2");
		else if((index = version.indexOf("as")) != -1)
			version.replace(index, index + 2, "alpha sapphire");
			//ver.replace("as", "alpha sapphire");
		else if((index = version.indexOf("or")) != -1)
			version.replace(index, index + 2, "omega ruby");
			//ver.replace("or", "omega ruby");
		else if((index = version.indexOf("ss")) != -1)
			version.replace(index, index + 2, "soul silver");
			//ver.replace("ss", "soul silver");
		else if((index = version.indexOf("hg")) != -1)
			version.replace(index, index + 2, "heart gold");
			//ver.replace("hg", "heart gold");
		else if((index = version.indexOf("fr")) != -1)
			version.replace(index, index + 2, "fire red");
			//ver.replace("fr", "fire red");
		else if((index = version.indexOf("lg")) != -1)
			version.replace(index, index + 2, "leaf green");
			//ver.replace("lg", "leaf green");
		
		String[] temp = version.toString().split(" ");
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < temp.length; i++)
			output.append(getBestSuggestion(versionDict, temp[i]) + " ");

		return output.toString().trim();
	}
	
	//Helper method to train spell checkers
	private void populateDict(SpellDictionary dict, String fileName) throws IOException
	{
		InputStreamReader reader;
		BufferedReader bReader;
		String line = null;
		reader = new InputStreamReader(ResourceManager.getDictionaryResource(fileName));
		bReader = new BufferedReader(reader);
		
		while((line = bReader.readLine()) != null)
		{
			dict.addWord(line.toLowerCase());
		}
		
		bReader.close();
	}
}
