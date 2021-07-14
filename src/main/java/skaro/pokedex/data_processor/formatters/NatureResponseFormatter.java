package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.Statistic;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.berry_flavor.BerryFlavor;
import skaro.pokeflex.objects.nature.Nature;

public class NatureResponseFormatter implements ResponseFormatter, PokedexServiceConsumer 
{
	private PokedexServiceManager services;
	
	public NatureResponseFormatter(PokedexServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR);
	}

	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder) 
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		Nature nature = (Nature)data.getValue(Nature.class.getName(), 0);
		
		response.addToReply(("**__"+TextUtility.flexFormToProper(nature.getNameInLanguage(lang.getFlexKey()))+"__**"));
		
		builder.addField(NatureField.BUFF_STAT.getFieldTitle(lang), getBuffedStat(nature, lang), true);
		builder.addField(NatureField.NERF_STAT.getFieldTitle(lang), getNerfedStat(nature, lang), true);
		builder.addField(NatureField.FLAVOR_LIKES.getFieldTitle(lang), getFlavorLikes(data, lang), true);
		builder.addField(NatureField.FLAVOR_HATES.getFieldTitle(lang), getFlavorHates(data, lang), true);
		
		builder.setColor(colorService.getPokedexColor());
		response.setEmbed(builder);
		return response;
	}
	
	private String getBuffedStat(Nature nature, Language lang) {
		return nature.getIncreasedStat() == null
				? "-"
				: Statistic.getByAPIKey(nature.getIncreasedStat().getName()).getInLanguage(lang);
	}
	
	private String getNerfedStat(Nature nature, Language lang) {
		return nature.getIncreasedStat() == null
				? "-"
				: Statistic.getByAPIKey(nature.getDecreasedStat().getName()).getInLanguage(lang);
	}
	
	private String getFlavorLikes(MultiMap<IFlexObject> data, Language lang) {
		return data.containsKey(BerryFlavor.class.getName())
				? ((BerryFlavor)data.getValue(BerryFlavor.class.getName(), 0)).getNameInLanguage(lang.getFlexKey())
				: "-";
	}
	
	private String getFlavorHates(MultiMap<IFlexObject> data, Language lang) {
		return data.containsKey(BerryFlavor.class.getName())
				? ((BerryFlavor)data.getValue(BerryFlavor.class.getName(), 1)).getNameInLanguage(lang.getFlexKey())
				: "-";
	}
	
	private enum NatureField
	{
		BUFF_STAT("Increased Stat", "Sube", "Augmenté", "Stat. Migliorata", "Erhöhen", "増加しました", "容易成长的能力", "1.1배 상승"),
		NERF_STAT("Decreased Stat", "Baja", "Diminué", "Stat. Peggiorata", "Senken", "減った", "不容易成长的能力", "0.9배 하락"),
		FLAVOR_LIKES("Favorite Flavor", "Les Gusta", "Goût Préféré", "Sapore Amato", "Mag Geschmack", "好きな味", "喜欢的口味", "좋아하는맛"),
		FLAVOR_HATES("Disliked Flavor", "No Les Gusta", "Goût Détesté", "Sapore Odiato", "Mag Geschmack Nicht", "嫌いな味", "不喜欢的口味", "싫어하는맛"),
		;
		
		private Map<Language, String> titleMap;
		
		private NatureField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean) 
		{
			titleMap = new HashMap<Language, String>();
			titleMap.put(Language.ENGLISH, english);
			titleMap.put(Language.SPANISH, spanish);
			titleMap.put(Language.FRENCH, french);
			titleMap.put(Language.ITALIAN, italian);
			titleMap.put(Language.GERMAN, german);
			titleMap.put(Language.JAPANESE_HIR_KAT, japanese);
			titleMap.put(Language.CHINESE_SIMPMLIFIED, chinese);
			titleMap.put(Language.KOREAN, korean);
		}
		
		public String getFieldTitle(Language lang)
		{
			return titleMap.get(lang);
		}
	}
}
