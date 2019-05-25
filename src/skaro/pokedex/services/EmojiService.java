package skaro.pokedex.services;

import java.util.HashMap;
import java.util.Map;

public class EmojiService implements PokedexService
{
	private final Map<String, String> typeEmojiMap;
	private final Map<String, String> crystalEmojiMap;
	private final Map<String, String> damageCategoryEmojiMap;
	private final Map<String, String> contestCategoryEmojiMap;
	
	public EmojiService() 
	{
		typeEmojiMap = new HashMap<String, String>();
		crystalEmojiMap = new HashMap<String, String>();
		damageCategoryEmojiMap = new HashMap<String, String>();
		contestCategoryEmojiMap = new HashMap<String, String>();
		initialize();
	}
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.EMOJI;
	}
	
	private void initialize()
	{
		typeEmojiMap.put("bug", "<:type_bug:495141042174033920>");
		typeEmojiMap.put("dark", "<:type_dark:495141042299994122>");
		typeEmojiMap.put("dragon", "<:type_dragon:495141042107187200>");
		typeEmojiMap.put("electric", "<:type_electric:495141042102992896>");
		typeEmojiMap.put("fairy", "<:type_fairy:495141041750409217>");
		typeEmojiMap.put("fighting", "<:type_fighting:495141042081759232>");
		typeEmojiMap.put("fire", "<:type_fire:495141041918312449>");
		typeEmojiMap.put("flying", "<:type_flying:495141042035621888>");
		typeEmojiMap.put("ghost", "<:type_ghost:495141041763123201>");
		typeEmojiMap.put("grass", "<:type_grass:495141041955930123>");
		typeEmojiMap.put("ground", "<:type_ground:495141041998004224>");
		typeEmojiMap.put("ice", "<:type_ice:495141041947803648>");
		typeEmojiMap.put("normal", "<:type_normal:495141041981358081>");
		typeEmojiMap.put("poison", "<:type_poison:495141041985552394>");
		typeEmojiMap.put("psychic", "<:type_psychic:495141042031689728>");
		typeEmojiMap.put("rock", "<:type_rock:495141041939415040>");
		typeEmojiMap.put("steel", "<:type_steel:495141042048335872>");
		typeEmojiMap.put("water", "<:type_water:495141041868111873>");
		
		crystalEmojiMap.put("bug", "<:z_bug:495433575269662750>");
		crystalEmojiMap.put("dark", "<:z_dark:495433575244627968>");
		crystalEmojiMap.put("dragon", "<:z_dragon:495433575051690005>");
		crystalEmojiMap.put("electric", "<:z_electric:495433575265599498>");
		crystalEmojiMap.put("fairy", "<:z_fairy:495433575215267840>");
		crystalEmojiMap.put("fighting", "<:z_fighting:495433575227719680>");
		crystalEmojiMap.put("fire", "<:z_fire:495433574921666631>");
		crystalEmojiMap.put("flying", "<:z_flying:495433574879723541>");
		crystalEmojiMap.put("ghost", "<:z_ghost:495433575207010304>");
		crystalEmojiMap.put("grass", "<:z_grass:495433575194427411>");
		crystalEmojiMap.put("ground", "<:z_ground:495433575181844481>");
		crystalEmojiMap.put("ice", "<:z_ice:495433575215136798>");
		crystalEmojiMap.put("normal", "<:z_normal:495433575122862089>");
		crystalEmojiMap.put("poison", "<:z_poison:495433575215398942>");
		crystalEmojiMap.put("psychic", "<:z_psychic:495433575215136778>");
		crystalEmojiMap.put("rock", "<:z_rock:495433575181713418>");
		crystalEmojiMap.put("steel", "<:z_steel:495433575181713408>");
		crystalEmojiMap.put("water", "<:z_water:495433574959415323>");
		
		damageCategoryEmojiMap.put("physical", "<:cat_physical:495143932959653898>");
		damageCategoryEmojiMap.put("special", "<:cat_special:495143932951396362>");
		damageCategoryEmojiMap.put("status", "<:cat_status:495143933123493888>");
		
		contestCategoryEmojiMap.put("cool", "<:contest_cool:495427549820616714>");
		contestCategoryEmojiMap.put("beauty", "<:contest_beauty:495427549451780108>");
		contestCategoryEmojiMap.put("cute", "<:contest_cute:495427549414031369>");
		contestCategoryEmojiMap.put("smart", "<:contest_clever:495427549363437581>");
		contestCategoryEmojiMap.put("tough", "<:contest_tough:495427549787324436>");
	}
	
	public String getTypeEmoji(String type) { return typeEmojiMap.get(type); }
	public String getCrystalEmoji(String type) { return crystalEmojiMap.get(type); }
	public String getDamageEmoji(String category) { return damageCategoryEmojiMap.get(category); }
	public String getContestEmoji(String category) { return contestCategoryEmojiMap.get(category); }
	
}
