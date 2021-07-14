package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.input_processor.Language;

public enum CommonData 
{
	GENERATION("Generation", "Generacion", "Génération", "Generazione", "Generation", "世代", "代", "세대"),
	RESIST("Resist", "Resistencia", "Résistances", "Non è Molto Efficace", "Widerstehen", "効果はいまひとつ", "抗", "효과는 별로였다"),
	WEAK("Weak", "Debilidad", "Faiblesses", "Debole", "Schwach", "弱い", "弱", "약점"),
	NEUTRAL("Neutral", "Normal", "Neutro", "Normalement", "Neutral", "中性", "中性", "중립국"),
	IMMUNE("Immune", "Inmunidad", "Immunisés", "Non ha Effetto", "Immunität", "効果はなし", "免疫的", "효과가 없다"),
	SUPER_EFFECTIVE("Super Effective", "Muy Efectivo", "Très Efficace", "Superefficaci", "Super Effektiv", "効果は抜群", "超级有效", "효과는 굉장했다"),
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
