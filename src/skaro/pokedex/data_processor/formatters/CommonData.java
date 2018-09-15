package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.input_processor.Language;

public enum CommonData 
{
	GENERATION("Generation", "Generacion", "Génération", "Generazione", "Generation", "世代", "代", "세대"),
	;
	
	private Map<Language, String> languageMap;
	
	CommonData(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
	{
		languageMap = new HashMap<Language, String>();
		languageMap.put(Language.ENGLISH, english);
		languageMap.put(Language.SPANISH, spanish);
		languageMap.put(Language.FRENCH, french);
		languageMap.put(Language.ITALIAN, italian);
		languageMap.put(Language.GERMAN, german);
		languageMap.put(Language.JAPANESE_HIR_KAT, japanese);
		languageMap.put(Language.CHINESE_SIMPMLIFIED, chinese);
		languageMap.put(Language.KOREAN, korean);
	}
	
	public String getInLanguage(Language lang)
	{
		return languageMap.get(lang);
	}
}
