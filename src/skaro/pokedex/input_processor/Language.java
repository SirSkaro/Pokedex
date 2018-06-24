package skaro.pokedex.input_processor;

public enum Language 
{
	CHINESE_SIMPMLIFIED("zh-Hans"),
	CHINESE_TRADITIONAL("zh-Hant"),
	JAPANESE("ja"),
	JAPANESE_HIR_KAT("ja-Hrkt"),
	JAPANESE_ROMAJI("roomaji"),
	ENGLISH("en"),
	ITALIAN("it"),
	SPANISH("es"),
	GERMAN("de"),
	FRENCH("fr"),
	KOREAN("ko"),
	;
	
	private String abbreviation;
	
	private Language(String abb)
	{
		abbreviation = abb;
	}
	
	public String getAbbreviation() { return abbreviation; }
}
