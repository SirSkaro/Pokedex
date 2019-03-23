package skaro.pokedex.input_processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;

import skaro.pokedex.core.ResourceManager;

public class ResourceDictionary
{
	private Map<Language, SpellDictionary> languageMap;
	
	public ResourceDictionary(String fileName, List<Language> supportedLanguages) throws IOException
	{
		languageMap = new HashMap<>();
		for(Language lang : supportedLanguages)
		{
			SpellDictionary dictionary = populateDict(fileName, lang);
			languageMap.put(lang, dictionary);
		}
	}
	
	public String spellCheckPhrase(String word, Language lang)
	{
		word = word.toLowerCase();
		String[] temp = word.split(" ");
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < temp.length; i++)
			output.append(getBestSuggestion(languageMap.get(lang), temp[i]) + " ");
		
		return output.toString().trim();
	}
	
	private SpellDictionary populateDict(String fileName, Language lang) throws IOException
	{
		String line;
		SpellDictionary result = new SpellDictionaryHashMap();
		InputStreamReader reader = new InputStreamReader(ResourceManager.getDictionaryResource(fileName, lang));
		BufferedReader bReader = new BufferedReader(reader);
		
		while((line = bReader.readLine()) != null)
			result.addWord(line.toLowerCase());
		
		bReader.close();
		return result;
	}
	
	private String getBestSuggestion(SpellDictionary dict, String word)
	{
		List<?> suggestions = dict.getSuggestions(word, 5);
		if(suggestions.isEmpty())
			return word;
		
		Word best = (Word)dict.getSuggestions(word, 5).get(0);
		return best.getWord();
	}
}
