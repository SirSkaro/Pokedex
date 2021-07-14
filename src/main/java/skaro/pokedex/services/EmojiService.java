package skaro.pokedex.services;

import java.util.HashMap;
import java.util.Map;

public class EmojiService implements PokedexService
{
	private final Map<String, String> typeEmojiMap;
	private final Map<String, String> crystalEmojiMap;
	private final Map<String, String> damageCategoryEmojiMap;
	private final Map<String, String> contestCategoryEmojiMap;
	private final Map<String, String> cardSetEmojiMap;
	
	public EmojiService() 
	{
		typeEmojiMap = new HashMap<String, String>();
		crystalEmojiMap = new HashMap<String, String>();
		damageCategoryEmojiMap = new HashMap<String, String>();
		contestCategoryEmojiMap = new HashMap<String, String>();
		cardSetEmojiMap = new HashMap<String, String>();
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
		
		cardSetEmojiMap.put("xyp", "<:xyp:691085835617042483>");
		cardSetEmojiMap.put("xy9", "<:xy9:691085835428429865>");
		cardSetEmojiMap.put("xy8", "<:xy8:691085835407196162>");
		cardSetEmojiMap.put("xy7", "<:xy7:691085836107776041>");
		cardSetEmojiMap.put("xy6", "<:xy6:691085835809980516>");
		cardSetEmojiMap.put("xy5", "<:xy5:691085835818500157>");
		cardSetEmojiMap.put("xy4", "<:xy4:691085835512184945>");
		cardSetEmojiMap.put("xy12", "<:xy12:691085835755454474>");
		cardSetEmojiMap.put("xy11", "<:xy11:691085835692408903>");
		cardSetEmojiMap.put("xy10", "<:xy10:691085835805786173>");
		cardSetEmojiMap.put("ex9", "<:ex9:691084359805042769>");
		cardSetEmojiMap.put("ex8", "<:ex8:691084359486406709>");
		cardSetEmojiMap.put("ex7", "<:ex7:691084359406714923>");
		cardSetEmojiMap.put("ex6", "<:ex6:691084359649984553>");
		cardSetEmojiMap.put("ex5", "<:ex5:691084359427555349>");
		cardSetEmojiMap.put("ex4", "<:ex4:691084359792590888>");
		cardSetEmojiMap.put("ex3", "<:ex3:691084359616167947>");
		cardSetEmojiMap.put("ex2", "<:ex2:691084359826145340>");
		cardSetEmojiMap.put("ex16", "<:ex16:691084360023277568>");
		cardSetEmojiMap.put("ex15", "<:ex15:691084359851180092>");
		cardSetEmojiMap.put("ex14", "<:ex14:691084359796523068>");
		cardSetEmojiMap.put("ex13", "<:ex13:691084360069414995>");
		cardSetEmojiMap.put("ex12", "<:ex12:691084359574356099>");
		cardSetEmojiMap.put("ex11", "<:ex11:691084359830339584>");
		cardSetEmojiMap.put("ex10", "<:ex10:691084359880671252>");
		cardSetEmojiMap.put("ex1", "<:ex1:691084359821951037>");
		cardSetEmojiMap.put("ecard3", "<:ecard3:691084359779876904>");
		cardSetEmojiMap.put("ecard2", "<:ecard2:691084359368966206>");
		cardSetEmojiMap.put("ecard1", "<:ecard1:691084359804911647>");
		cardSetEmojiMap.put("dv1", "<:dv1:691084359846985788>");
		cardSetEmojiMap.put("dpp", "<:dpp:691084359586807979>");
		cardSetEmojiMap.put("dp7", "<:dp7:691084359805042840>");
		cardSetEmojiMap.put("dp6", "<:dp6:691084359809237002>");
		cardSetEmojiMap.put("dp5", "<:dp5:691084359448395797>");
		cardSetEmojiMap.put("dp4", "<:dp4:691084359800717322>");
		cardSetEmojiMap.put("dp3", "<:dp3:691084360123678820>");
		cardSetEmojiMap.put("dp2", "<:dp2:691084359914094633>");
		cardSetEmojiMap.put("dp1", "<:dp1:691084359456784455>");
		cardSetEmojiMap.put("det1", "<:det1:691084359805042860>");
		cardSetEmojiMap.put("dc1", "<:dc1:691084359502921780>");
		cardSetEmojiMap.put("col1", "<:col1:691084360052637736>");
		cardSetEmojiMap.put("bwp", "<:bwp:691084359574487042>");
		cardSetEmojiMap.put("bw9", "<:bw9:691084359729676420>");
		cardSetEmojiMap.put("bw8", "<:bw8:691084359666499675>");
		cardSetEmojiMap.put("bw7", "<:bw7:691084359670693938>");
		cardSetEmojiMap.put("bw6", "<:bw6:691084359662567434>");
		cardSetEmojiMap.put("bw5", "<:bw5:691084359704379442>");
		cardSetEmojiMap.put("bw4", "<:bw4:691084359419297795>");
		cardSetEmojiMap.put("bw3", "<:bw3:691084359490469959>");
		cardSetEmojiMap.put("bw2", "<:bw2:691084359972683797>");
		cardSetEmojiMap.put("bw11", "<:bw11:691084359809237022>");
		cardSetEmojiMap.put("bw10", "<:bw10:691084359381418076>");
		cardSetEmojiMap.put("bw1", "<:bw1:691084359385743362>");
		cardSetEmojiMap.put("basep", "<:basep:691084359624687696>");
		cardSetEmojiMap.put("base6", "<:base6:691084359410647071>");
		cardSetEmojiMap.put("base5", "<:base5:691084359607910503>");
		cardSetEmojiMap.put("base4", "<:base4:691084359587069972>");
		cardSetEmojiMap.put("base3", "<:base3:691084359616299069>");
		cardSetEmojiMap.put("base2", "<:base2:691084359683538994>");
		cardSetEmojiMap.put("base1", "<:base1:691084359868088380>");
		cardSetEmojiMap.put("xy3", "<:xy3:691085643107008532>");
		cardSetEmojiMap.put("xy2", "<:xy2:691085642808950805>");
		cardSetEmojiMap.put("xy1", "<:xy1:691085643501010945>");
		cardSetEmojiMap.put("xy0", "<:xy0:691085643064934411>");
		cardSetEmojiMap.put("swsh1", "<:swsh1:691085643018797116>");
		cardSetEmojiMap.put("smp", "<:smp:691085642951557172>");
		cardSetEmojiMap.put("sma", "<:sma:691085643329306674>");
		cardSetEmojiMap.put("sm9", "<:sm9:691085642997825546>");
		cardSetEmojiMap.put("sm8", "<:sm8:691085643039899689>");
		cardSetEmojiMap.put("sm75", "<:sm75:691085642842767361>");
		cardSetEmojiMap.put("sm7", "<:sm7:691085643182374952>");
		cardSetEmojiMap.put("sm6", "<:sm6:691085642926653561>");
		cardSetEmojiMap.put("sm5", "<:sm5:691085643123654666>");
		cardSetEmojiMap.put("sm4", "<:sm4:691085642712481844>");
		cardSetEmojiMap.put("sm35", "<:sm35:691085643010539581>");
		cardSetEmojiMap.put("sm3", "<:sm3:691085642821664772>");
		cardSetEmojiMap.put("sm2", "<:sm2:691085642997694484>");
		cardSetEmojiMap.put("sm12", "<:sm12:691085643031511140>");
		cardSetEmojiMap.put("sm115", "<:sm115:691085642746036285>");
		cardSetEmojiMap.put("sm11", "<:sm11:691085642855219231>");
		cardSetEmojiMap.put("sm10", "<:sm10:691085643119329350>");
		cardSetEmojiMap.put("sm1", "<:sm1:691085643102814218>");
		cardSetEmojiMap.put("si1", "<:si1:691085643027054702>");
		cardSetEmojiMap.put("ru1", "<:ru1:691085642742104175>");
		cardSetEmojiMap.put("pop9", "<:pop9:691085643035705374>");
		cardSetEmojiMap.put("pop8", "<:pop8:691085643022860288>");
		cardSetEmojiMap.put("pop7", "<:pop7:691085643136106566>");
		cardSetEmojiMap.put("pop6", "<:pop6:691085642808950885>");
		cardSetEmojiMap.put("pop5", "<:pop5:691085642997694495>");
		cardSetEmojiMap.put("pop4", "<:pop4:691085642817339413>");
		cardSetEmojiMap.put("pop3", "<:pop3:691085642674995271>");
		cardSetEmojiMap.put("pop2", "<:pop2:691085643060871181>");
		cardSetEmojiMap.put("pop1", "<:pop1:691085642997825536>");
		cardSetEmojiMap.put("pl4", "<:pl4:691085643064934420>");
		cardSetEmojiMap.put("pl3", "<:pl3:691085643039768666>");
		cardSetEmojiMap.put("pl2", "<:pl2:691085642733453314>");
		cardSetEmojiMap.put("pl1", "<:pl1:691085642549035010>");
		cardSetEmojiMap.put("np", "<:np:691085642926391346>");
		cardSetEmojiMap.put("neo4", "<:neo4:691085643052351608>");
		cardSetEmojiMap.put("neo3", "<:neo3:691085642653761537>");
		cardSetEmojiMap.put("neo2", "<:neo2:691085642893099038>");
		cardSetEmojiMap.put("neo1", "<:neo1:691085642708287520>");
		cardSetEmojiMap.put("hsp", "<:hsp:691085642658086953>");
		cardSetEmojiMap.put("hgss4", "<:hgss4:691085642859282483>");
		cardSetEmojiMap.put("hgss3", "<:hgss3:691085642574069812>");
		cardSetEmojiMap.put("hgss2", "<:hgss2:691085642968596530>");
		cardSetEmojiMap.put("hgss1", "<:hgss1:691085642549166112>");
		cardSetEmojiMap.put("gym2", "<:gym2:691085642922328124>");
		cardSetEmojiMap.put("gym1", "<:gym1:691085642943430676>");
		cardSetEmojiMap.put("g1", "<:g1:691085642511417375>");
	}
	
	public String getTypeEmoji(String type) { return typeEmojiMap.get(type); }
	public String getCrystalEmoji(String type) { return crystalEmojiMap.get(type); }
	public String getDamageEmoji(String category) { return damageCategoryEmojiMap.get(category); }
	public String getContestEmoji(String category) { return contestCategoryEmojiMap.get(category); }
	public String getCardSetEmoji(String set) { return cardSetEmojiMap.get(set); }
	
}
