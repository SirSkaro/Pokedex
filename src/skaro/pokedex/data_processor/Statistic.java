package skaro.pokedex.data_processor;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.input_processor.Language;

public enum Statistic 
{
	HIT_POINTS("HP", "PS", "PV", "Punti Salute", "Kraftpunkte", "エイチピー", "体力", "HP", "hp"),
	ATTACK("Attack", "Ataque", "Attaque", "Attacco", "Angriff", "こうげき", "攻击", "공격", "attack"),
	DEFENSE("Defense","Defensa","Défense", "Difesa", "Verteidigung", "ぼうぎょ", "防御", "방어", "defense"),
	SPECIAL_ATTACK("Sp.Attack","Ataque Esp.","Attaque Spé.", "Attacco Spec.", "Spezial-Ang.", "とくこう", "特攻", "특수공격", "special-attack"),
	SPECIAL_DEFENSE("Sp.Defense","Defensa Esp.","Défense Spé.", "Difesa Spec.", "Spezial-Ver.", "とくぼう", "特防", "특수방어", "special-defense"),
	SPEED("Speed","Velocidad","Vitesse", "Velocità", "Initiative", "すばやさ", "速度", "스피드", "speed"),
	
	HP("HP", "PS", "PV", "PS", "KP", "HP", "ＨＰ", "HP","hp"),
	ATK("Atk","Ata","Att", "Att", "Angr", "攻撃", "攻击", "공격", "attack"),
	DEF("Def","Def","Déf", "Dif", "Vert", "防御", "防御", "방어", "defense"),
	SP_ATK("Sp.Atk","At.Esp","Att.Sp", "Att.Sp", "Spez.A", "とくこう", "特攻", "특수공격", "special-attack"),
	SP_DEF("Sp.Def","De.Esp","Déf.Sp", "Dif.Sp", "Spez.V", "特防", "特防", "특수방어", "special-defense"),
	SPE("Spe","Vel","Vit","Vel", "Init", "すばやさ", "速度", "스피드", "speed"),
	;
	
	private Map<Language, String> statLanguageMap;
	private String apiKey;
	
	Statistic(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean, String key)
	{
		statLanguageMap = new HashMap<Language, String>();
		statLanguageMap.put(Language.ENGLISH, english);
		statLanguageMap.put(Language.SPANISH, spanish);
		statLanguageMap.put(Language.FRENCH, french);
		statLanguageMap.put(Language.ITALIAN, italian);
		statLanguageMap.put(Language.GERMAN, german);
		statLanguageMap.put(Language.JAPANESE_HIR_KAT, japanese);
		statLanguageMap.put(Language.CHINESE_SIMPMLIFIED, chinese);
		statLanguageMap.put(Language.KOREAN, korean);
		
		apiKey = key;
	}
	
	public String getInLanguage(Language lang)
	{
		return statLanguageMap.get(lang);
	}
	
	public String getAPIKey()
	{
		return apiKey;
	}
}
