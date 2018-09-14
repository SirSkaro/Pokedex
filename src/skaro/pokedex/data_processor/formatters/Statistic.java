package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;

import skaro.pokedex.input_processor.Language;

public enum Statistic 
{
	HIT_POINTS("Health Points", "Puntos de Salud", "Points de Vie", "Punti Salute"),
	ATTACK("Attack", "Ataque", "Attaque", "Attacco"),
	DEFENSE("Defense","Defensa","Défense", "Difesa"),
	SPECIAL_ATTACK("Special Attack","Ataque Especial","Attaque Spéciale", "Attacco Speciale"),
	SPECIAL_DEFENSE("Special Defense","Defensa especial","Défense Spéciale", "Difesa Speciale"),
	SPEED("Speed","Velocidad","Vitesse", "Velocità"),
	
	HP("HP", "PS", "PV", "PS"),
	ATK("Atk","Ata","Att", "Att"),
	DEF("Def","Def","Déf", "Dif"),
	SP_ATK("Sp.Atk","At.Esp","Att.Sp", "Att.Sp"),
	SP_DEF("Sp.Def","De.Esp","Déf.Sp", "Dif.Sp"),
	SPE("Spe","Vel","Vit","Vel"),
	;
	
	private Map<Language, String> statLanguageMap;
	
	Statistic(String english, String spanish, String french, String italian)
	{
		statLanguageMap = new HashMap<Language, String>();
		statLanguageMap.put(Language.ENGLISH, english);
		statLanguageMap.put(Language.SPANISH, spanish);
		statLanguageMap.put(Language.FRENCH, french);
		statLanguageMap.put(Language.ITALIAN, italian);
	}
	
	public String getInLanguage(Language lang)
	{
		return statLanguageMap.get(lang);
	}
}
