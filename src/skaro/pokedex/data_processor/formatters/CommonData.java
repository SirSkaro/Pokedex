package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.input_processor.Language;

public enum CommonData 
{
	GENERATION("Generation", "Generacion", "Génération", "Generazione"),
	;
	
	private Map<Language, String> languageMap;
	
	CommonData(String english, String spanish, String french, String italian)
	{
		languageMap = new HashMap<Language, String>();
		languageMap.put(Language.ENGLISH, english);
		languageMap.put(Language.SPANISH, spanish);
		languageMap.put(Language.FRENCH, french);
		languageMap.put(Language.ITALIAN, italian);
	}
	
	public String getInLanguage(Language lang)
	{
		return languageMap.get(lang);
	}
}
