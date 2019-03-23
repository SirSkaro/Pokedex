package skaro.pokedex.input_processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellChecker 
{
	private Map<SQLResource, ResourceDictionary> dictionaryMap;
	private static SpellChecker instance;
	
	private SpellChecker() throws IOException
	{
		List<Language> supportedLanguages = new ArrayList<Language>();
		supportedLanguages.add(Language.ENGLISH);
		supportedLanguages.add(Language.SPANISH);
		supportedLanguages.add(Language.FRENCH);
		supportedLanguages.add(Language.ITALIAN);
		supportedLanguages.add(Language.GERMAN);
		supportedLanguages.add(Language.JAPANESE_HIR_KAT);
		supportedLanguages.add(Language.CHINESE_SIMPMLIFIED);
		supportedLanguages.add(Language.KOREAN);
		
		dictionaryMap = new HashMap<>();
		dictionaryMap.put(SQLResource.ABILITY, new ResourceDictionary("abilities.txt", supportedLanguages));
		dictionaryMap.put(SQLResource.ITEM, new ResourceDictionary("items.txt", supportedLanguages));
		dictionaryMap.put(SQLResource.MOVE, new ResourceDictionary("moves.txt", supportedLanguages));
		dictionaryMap.put(SQLResource.POKEMON, new ResourceDictionary("pokemon.txt", supportedLanguages));
		dictionaryMap.put(SQLResource.TYPE, new ResourceDictionary("types.txt", supportedLanguages));
		dictionaryMap.put(SQLResource.VERSION, new ResourceDictionary("versions.txt", supportedLanguages));
	}
	
	public static SpellChecker getInstance()
	{
		if(instance == null)
			try { instance = new SpellChecker(); }
			catch(IOException e)
			{
				System.out.println("[SpellChecker] Unable to create instance of SpellChecker");
				System.exit(1);
			}
		
		return instance;
	}
	
	public String spellCheckResource(SQLResource resourceType, String phrase, Language lang)
	{
		ResourceDictionary dictionary = dictionaryMap.get(resourceType);
		return dictionary.spellCheckPhrase(phrase, lang);
	}
}
