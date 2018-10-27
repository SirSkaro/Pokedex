package skaro.pokedex.data_processor;

import java.util.HashMap;
import java.util.Map;

public enum EmojiTracker 
{
	TRACKER();
	
	private final static Map<TypeData, String> typeEmojiMap = new HashMap<TypeData, String>();
	private final static Map<TypeData, String> crystalEmojiMap = new HashMap<TypeData, String>();
	private final static Map<String, String> damageCategoryEmojiMap = new HashMap<String, String>();
	private final static Map<String, String> contestCategoryEmojiMap = new HashMap<String, String>();
	
	private EmojiTracker() {}
	
	public static void initialize()
	{
		typeEmojiMap.put(TypeData.BUG, "<:type_bug:495141042174033920>");
		typeEmojiMap.put(TypeData.DARK, "<:type_dark:495141042299994122>");
		typeEmojiMap.put(TypeData.DRAGON, "<:type_dragon:495141042107187200>");
		typeEmojiMap.put(TypeData.ELECTRIC, "<:type_electric:495141042102992896>");
		typeEmojiMap.put(TypeData.FAIRY, "<:type_fairy:495141041750409217>");
		typeEmojiMap.put(TypeData.FIGHTING, "<:type_fighting:495141042081759232>");
		typeEmojiMap.put(TypeData.FIRE, "<:type_fire:495141041918312449>");
		typeEmojiMap.put(TypeData.FLYING, "<:type_flying:495141042035621888>");
		typeEmojiMap.put(TypeData.GHOST, "<:type_ghost:495141041763123201>");
		typeEmojiMap.put(TypeData.GRASS, "<:type_grass:495141041955930123>");
		typeEmojiMap.put(TypeData.GROUND, "<:type_ground:495141041998004224>");
		typeEmojiMap.put(TypeData.ICE, "<:type_ice:495141041947803648>");
		typeEmojiMap.put(TypeData.NORMAL, "<:type_normal:495141041981358081>");
		typeEmojiMap.put(TypeData.POISON, "<:type_poison:495141041985552394>");
		typeEmojiMap.put(TypeData.PSYCHIC, "<:type_psychic:495141042031689728>");
		typeEmojiMap.put(TypeData.ROCK, "<:type_rock:495141041939415040>");
		typeEmojiMap.put(TypeData.STEEL, "<:type_steel:495141042048335872>");
		typeEmojiMap.put(TypeData.WATER, "<:type_water:495141041868111873>");
		
		crystalEmojiMap.put(TypeData.BUG, "<:z_bug:495433575269662750>");
		crystalEmojiMap.put(TypeData.DARK, "<:z_dark:495433575244627968>");
		crystalEmojiMap.put(TypeData.DRAGON, "<:z_dragon:495433575051690005>");
		crystalEmojiMap.put(TypeData.ELECTRIC, "<:z_electric:495433575265599498>");
		crystalEmojiMap.put(TypeData.FAIRY, "<:z_fairy:495433575215267840>");
		crystalEmojiMap.put(TypeData.FIGHTING, "<:z_fighting:495433575227719680>");
		crystalEmojiMap.put(TypeData.FIRE, "<:z_fire:495433574921666631>");
		crystalEmojiMap.put(TypeData.FLYING, "<:z_flying:495433574879723541>");
		crystalEmojiMap.put(TypeData.GHOST, "<:z_ghost:495433575207010304>");
		crystalEmojiMap.put(TypeData.GRASS, "<:z_grass:495433575194427411>");
		crystalEmojiMap.put(TypeData.GROUND, "<:z_ground:495433575181844481>");
		crystalEmojiMap.put(TypeData.ICE, "<:z_ice:495433575215136798>");
		crystalEmojiMap.put(TypeData.NORMAL, "<:z_normal:495433575122862089>");
		crystalEmojiMap.put(TypeData.POISON, "<:z_poison:495433575215398942>");
		crystalEmojiMap.put(TypeData.PSYCHIC, "<:z_psychic:495433575215136778>");
		crystalEmojiMap.put(TypeData.ROCK, "<:z_rock:495433575181713418>");
		crystalEmojiMap.put(TypeData.STEEL, "<:z_steel:495433575181713408>");
		crystalEmojiMap.put(TypeData.WATER, "<:z_water:495433574959415323>");
		
		
		damageCategoryEmojiMap.put("physical", "<:cat_physical:495143932959653898>");
		damageCategoryEmojiMap.put("special", "<:cat_special:495143932951396362>");
		damageCategoryEmojiMap.put("status", "<:cat_status:495143933123493888>");
		
		contestCategoryEmojiMap.put("cool", "<:contest_cool:495427549820616714>");
		contestCategoryEmojiMap.put("beauty", "<:contest_beauty:495427549451780108>");
		contestCategoryEmojiMap.put("cute", "<:contest_cute:495427549414031369>");
		contestCategoryEmojiMap.put("smart", "<:contest_clever:495427549363437581>");
		contestCategoryEmojiMap.put("tough", "<:contest_tough:495427549787324436>");
	}
	
	public static String getTypeEmoji(TypeData type) { return typeEmojiMap.get(type); }
	public static String getCrystalEmoji(TypeData type) { return crystalEmojiMap.get(type); }
	public static String getDamageEmoji(String category) { return damageCategoryEmojiMap.get(category); }
	public static String getContestEmoji(String category) { return contestCategoryEmojiMap.get(category); }
	
}
