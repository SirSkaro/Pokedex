package skaro.pokedex.data_processor.formatters;

import java.util.TreeMap;

//import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

import skaro.pokedex.input_processor.Language;

public class TextFormatter 
{
	public static String flexFormToProper(String string)
	{
		String noDashes = string.replace("-", " ");
		return WordUtils.capitalize(noDashes.trim()).intern();
	}
	
	public static String pokemonFlexFormToProper(String string)
	{
		String components[] = string.split("-");
		
		if(string.contains("-mega"))
		{
			if(components.length == 2)
				return WordUtils.capitalize(components[1] + " " + components[0]).intern();
			else
				return WordUtils.capitalize(components[1] + " " + components[0] + " " + components[2]).intern();
		}
		
		return flexFormToProper(string);
	}
	
	public static String formatGeneration(String string)
	{
		if(string.contains("sun"))
			string = "generation-vii";
		String[] words = string.split("-");
		return (WordUtils.capitalize(words[0]) + " " + words[1].toUpperCase()).intern();
	}
	
	public static String formatGeneration(String string, Language lang)
	{
		if(string.contains("sun"))
			string = "generation-vii";
		String[] words = string.split("-");
		return (CommonData.GENERATION.getInLanguage(lang) + " " + words[1].toUpperCase()).intern();
	}
	
	public static String formatGeneration(int gen)
	{
		return ("Generation " + toRoman(gen)).intern();
	}
	
	public static String[] getURLComponents(String url)
	{
		return url.split("/");
	}
	
	public static String flexToDBForm(String string)
	{
		
		return string.replace("-", "").toLowerCase();
	}
	
	public static String formatDexEntry(String string)
	{
		String result = string.replace("\n", " ");
		result = result.replace("POKéMON", "Pokémon");
		return result.replace("\f", " ");
	}
	
	/**
	 * A recursive function to convert base 10 to Roman Numeral
	 * @param number - the number to convert
	 * @return A string representing the equivalent Roman Numeral
	 */
	private static String toRoman(int number) 
	{
		TreeMap<Integer, String> numeralMap = new TreeMap<Integer, String>();
		numeralMap.put(10, "X"); numeralMap.put(9, "IX"); numeralMap.put(5, "V");
        numeralMap.put(4, "IV"); numeralMap.put(1, "I");
		
        int largestSubtractableNumeral =  numeralMap.floorKey(number);
        if(number == largestSubtractableNumeral) 
            return numeralMap.get(number);
      
        return numeralMap.get(largestSubtractableNumeral) + toRoman(number-largestSubtractableNumeral);
    }
	
	/**
	 * Takes in a name and returns it in the formatting used
	 * in the data base. No spaces or symbols.
	 * @param s - pokemon name
	 * @return formatted String
	 */
	public static String dbFormat(String s, Language lang)
	{
		if(s == null)
			return "";
		
		s = s.toLowerCase();
		
		//Check prefixes
		if(!s.contains("launcher"))
		if(s.contains("primal ") 
				|| (s.contains("mega ") && !s.contains("omega "))
				|| (s.contains("alola ") || s.contains("alolan ")) 
				|| (s.contains("ultra "))						)
		{
			s = s.replace("alolan", "alola");
			String[] name = s.split(" ");
			return (name[1]+name[0]+((name.length == 3) ? name[2] : ""));
		}
		
		//Check for Necrozma forms
		if( s.contains("necrozma") &&
				((s.contains("dusk") || s.contains("dawn")))
				)
		{
			String temp = s.replace("necrozma", "");
			return "necrozma"+temp.replace(" ", "");
		}
		
		//Check for other symbols
		if(s.endsWith("♀"))
			s = s.replace("♀", "f");
		
		if(s.endsWith("♂"))
			s = s.replace("♂", "m");
		
		if(s.contains("-"))
			s = s.replace("-", "");
		
		if(s.contains("."))
			s = s.replace(".", "");
		
		if(s.contains(" "))
			s = s.replace(" ", "");
		
		if(s.contains(","))
			s = s.replace(",", "");
		
		if(s.contains(":"))
			s = s.replace(":", "");
		
		if(s.contains("%"))
			s = s.replace("%", "");
		
		if(s.contains("alolan"))
			s = s.replace("alolan", "alola");
		
		//Remove characters that would cause an SQL exception
		if(s.contains("\\"))
			s = s.replace("\\", "");
		if(s.contains("\""))
			s = s.replace("\"", "");
		if(s.contains("'"))
			s = s.replace("'", "");
		
		//Remove accents
		if(lang != Language.KOREAN)
			s = StringUtils.stripAccents(s);
		
		return s.intern();
	}
}
