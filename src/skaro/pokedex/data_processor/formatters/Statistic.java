package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.input_processor.Language;

public enum Statistic 
{
	HIT_POINTS("Health Points", "Puntos de Salud", "Points de Vie", "Punti Salute", "Statuswerte", "エイチピー", "体力", "HP"),
	ATTACK("Attack", "Ataque", "Attaque", "Attacco", "Angriff", "こうげき", "攻击", "공격"),
	DEFENSE("Defense","Defensa","Défense", "Difesa", "Verteidigung", "ぼうぎょ", "防御", "방어"),
	SPECIAL_ATTACK("Special Attack","Ataque Especial","Attaque Spéciale", "Attacco Speciale", "Spezial-Angriff", "とくこう", "特攻", "특수공격"),
	SPECIAL_DEFENSE("Special Defense","Defensa especial","Défense Spéciale", "Difesa Speciale", "Spezial-Verteidigung", "とくぼう", "特防", "특수방어"),
	SPEED("Speed","Velocidad","Vitesse", "Velocità", "Initiative", "すばやさ", "速度", "스피드"),
	
	HP("HP", "PS", "PV", "PS", "KP", "HP", "ＨＰ", "HP"),
	ATK("Atk","Ata","Att", "Att", "Angr", "攻撃", "攻击", "공격"),
	DEF("Def","Def","Déf", "Dif", "Vert", "防御", "防御", "방어"),
	SP_ATK("Sp.Atk","At.Esp","Att.Sp", "Att.Sp", "Spez.A", "とくこう", "特攻", "특수공격"),
	SP_DEF("Sp.Def","De.Esp","Déf.Sp", "Dif.Sp", "Spez.V", "特防", "特防", "특수방어"),
	SPE("Spe","Vel","Vit","Vel", "Init", "すばやさ", "速度", "스피드"),
	;
	
	private Map<Language, String> statLanguageMap;
	
	Statistic(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
	}
	
	public String getInLanguage(Language lang)
	{
		return statLanguageMap.get(lang);
	}
}
