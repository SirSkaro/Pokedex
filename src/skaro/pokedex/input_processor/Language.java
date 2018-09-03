package skaro.pokedex.input_processor;

public enum Language 
{
	CHINESE_SIMPMLIFIED("简化字", "zh-Hans", "zh_hans"),
	CHINESE_TRADITIONAL("正體字", "zh-Hant", "zh_hant"),
	JAPANESE("日本語", "ja", "ja"),
	JAPANESE_HIR_KAT("日本語","ja-Hrkt", "ja_hrkt"),
	JAPANESE_ROMAJI("Nihongo","roomaji", "roomaji"),
	ENGLISH("English","en", null),
	ITALIAN("Italiano", "it", "it"),
	SPANISH("Español", "es", "es"),
	GERMAN("Deutsch", "de", "de"),
	FRENCH("Français", "fr", "fr"),
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
