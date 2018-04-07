package skaro.pokedex.data_processor;

import java.util.TreeMap;

//import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang.WordUtils;

public class TextFormatter 
{
	public static String flexFormToProper(String string)
	{
		String noDashes = string.replace("-", " ");
		return WordUtils.capitalize(noDashes);
	}
	
	public static String formatGeneration(String string)
	{
		String[] words = string.split("-");
		return WordUtils.capitalize(words[0]) + " " + words[1].toUpperCase();
	}
	
	public static String formatGeneration(int gen)
	{
		return "Generation " + toRoman(gen);
	}
	
	public static String[] getURLComponents(String url)
	{
		return url.split("/");
	}
	
	public static String flexToDBForm(String string)
	{
		return string.replace("-", "");
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
}
