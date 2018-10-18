package skaro.pokedex.data_processor.formatters;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.data_processor.EmojiTracker;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.item.Item;
import skaro.pokeflex.objects.item_category.ItemCategory;
import skaro.pokeflex.objects.type.Type;
import sx.blah.discord.util.EmbedBuilder;

public class ItemResponseFormatter implements IDiscordFormatter 
{

	@Override
	public Response invalidInputResponse(Input input)
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify exactly one Item as input for this command.".intern());
			break;
			case INVALID_ARGUMENT:
				response.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Item in "+input.getLanguage().getName());
			break;
			default:
				response.addToReply("A technical error occured (code 104)");
		}
		
		return response;
	}
	
	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		Response response = new Response();
		Language lang = input.getLanguage();
		builder.setLenient(true);
		Item item = (Item)data.get(Item.class.getName()).get(0);
		Type type = (Type)data.getValue(Type.class.getName(), 0);
		
		//Header
		response.addToReply("**__"+TextFormatter.flexFormToProper(item.getNameInLanguage(lang.getFlexKey()))+"__**");
		
		builder.appendField(ItemField.CATEGORY.getFieldTitle(lang), formatCategory((ItemCategory)data.getValue(ItemCategory.class.getName(), 0), lang), true);
		builder.appendField(ItemField.DEBUT.getFieldTitle(lang), TextFormatter.formatGeneration(item.getDebut(), lang), true);
		
		if(item.getFlingPower() > 0)
			builder.appendField(ItemField.FLING_POWER.getFieldTitle(lang), Integer.toString(item.getFlingPower()), true);
		if(type != null)
		{
			builder.appendField(ItemField.NG_TYPE.getFieldTitle(lang), formatType(type, lang), true);
			builder.appendField(ItemField.NG_POWER.getFieldTitle(lang), Integer.toString(item.getNgPower()), true);
		}
		
		builder.appendField(ItemField.DESC.getFieldTitle(lang), formatDescription(item, lang), false);
		
		//English-only data
		if(lang == Language.ENGLISH)
			builder.appendField("Technical Description", item.getLdesc(), false);
		
		builder.withColor(new Color(0xE89800));
		builder.withThumbnail(item.getSprites().getDefault());
		response.setEmbededReply(builder.build());
		return response;
	}
	
	private String formatDescription(Item item, Language lang)
	{
		Optional<String> desc = item.getFlavorTextEntry(lang.getFlexKey(), "sun-moon");
		
		if(desc.isPresent())
			return desc.get().replaceAll("\\n", " ");
		
		return null;
	}
	
	private String formatType(Type type, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(EmojiTracker.getTypeEmoji(TypeData.getByName(type.getName())));
		builder.append(" ");
		builder.append(TextFormatter.flexFormToProper(type.getNameInLanguage(lang.getFlexKey())));
		
		return builder.toString();
	}
	
	private String formatCategory(ItemCategory category, Language lang)
	{
		return TextFormatter.flexFormToProper(category.getNameInLanguage(lang.getFlexKey()));
	}

	private enum ItemField
	{
		DEBUT("Debut", "Debut", "Début", "Debutto", "Debüt", "デビュー", "出道", "데뷔"),
		DESC("Description", "Descripción", "Description", "La Description", "Beschreibung", "説明", "描述", "기술"),
		CATEGORY("Category", "Categoría", "Catégorie", "Categoria", "Kategorie", "カテゴリー", "类别", "범주"),
		FLING_POWER("Base Power (Fling)", "Potencia (Lanzamiento)", "Puissance (Dégommage)", "Potenza (Lancio)", "Stärke (Schleuder)", "威力 (なげつける)", "威力 (投掷)", "위력 (내던지기)"),
		NG_TYPE("Type (Natural Gift)", "Tipo (Don Natural)", "Type (Don Naturel)", "Tipo (Dononaturale)", "Typ (Beerenkräfte) ", "タイプ (しぜんのめぐみ)", "屬性 (自然之恩)", "타입 (자연의은혜)"),
		NG_POWER("Base Power (Natural Gift)", "Potencia (Don Natural)", "Puissance (Don Naturel)", "Potenza (Dononaturale) ", "Stärke (Beerenkräfte) ", "威力 (しぜんのめぐみ)", "威力 (自然之恩)", "위력 (자연의은혜)"),
		;
		
		private Map<Language, String> titleMap;
		
		ItemField() {}
		ItemField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
