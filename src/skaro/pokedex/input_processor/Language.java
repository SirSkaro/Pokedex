package skaro.pokedex.input_processor;

public enum Language 
{
	ENGLISH("English","en", "en"),
	SPANISH("Español", "es", "es"),
	GERMAN("Deutsch", "de", "de"),
	ITALIAN("Italiano", "it", "it"),
	FRENCH("Français", "fr", "fr"),
	CHINESE_SIMPMLIFIED("简化字", "zh-Hans", "zh_hans"),
	CHINESE_TRADITIONAL("正體字", "zh-Hant", "zh_hant"),
	JAPANESE("日本語", "ja", "ja"),
	JAPANESE_HIR_KAT("日本語","ja-Hrkt", "ja_hrkt"),
	JAPANESE_ROMAJI("Nihongo","roomaji", "roomaji"),
	KOREAN("조선말","ko", "ko"),
	;
	
	private String pokeFlexKey, sqlAttribute, languageName;
	
	private Language(String lang, String key, String att)
	{
		languageName = lang;
		pokeFlexKey = key;
		sqlAttribute = att;
	}
	
	public String getSQLAttribute() { return sqlAttribute; }
	public String getFlexKey() { return pokeFlexKey; }
	public String getName() { return languageName; }
}
