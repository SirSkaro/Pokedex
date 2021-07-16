package skaro.pokedex.data_processor.formatters;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.Exceptions;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.EmojiService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.item.Item;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.type.Type;

public class ZMoveResponseFormatter implements ResponseFormatter, PokedexServiceConsumer
{
	private PokedexServiceManager services;
	private final String zMoveClipPath;
	
	public ZMoveResponseFormatter(PokedexServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
		zMoveClipPath = ((ConfigurationService)services.getService(ServiceType.CONFIG)).getZMoveClipPath();
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services)
	{
		return services.hasServices(ServiceType.COLOR, ServiceType.EMOJI);
	}

	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder)
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		Move move = (Move)data.get(Move.class.getName()).get(0);
		Type type = (Type)data.getValue(Type.class.getName(), 0);
		Item crystal = (Item)data.getValue(Item.class.getName(), 0);
		
		builder.setTitle("**__"+TextUtility.flexFormToProper(move.getNameInLanguage(lang.getFlexKey()))+"__**");
		builder.addField(ZMoveField.TYPE.getFieldTitle(lang), formatType(type, lang), true);
		builder.addField(ZMoveField.Z_CRYSTAL.getFieldTitle(lang), formatCrystal(crystal, lang), true);
		builder.setThumbnail(move.getImages().get(0).getUrl());
		
		addZmoveClip(response, move, builder);
		
		builder.setColor(colorService.getColorForType(move.getType().getName()));
		response.setEmbed(builder);
		return response;
	}
	
	private void addZmoveClip(Response response, Move move, EmbedCreateSpec builder) {
		try {
			String fileName = move.getName().replace("--physical", "") + ".mp4";
			URL url = new URL(zMoveClipPath + "/" + fileName);
			response.addImage(fileName, url.openStream());
		} catch(IOException e) {
			throw Exceptions.propagate(e);
		}
	}
	
	private String formatCrystal(Item crystal, Language lang) {
		return TextUtility.flexFormToProper(crystal.getNameInLanguage(lang.getFlexKey()));
	}
	
	private String formatType(Type type, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		builder.append(emojiService.getTypeEmoji((type.getName())));
		builder.append(" ");
		builder.append(TextUtility.flexFormToProper(type.getNameInLanguage(lang.getFlexKey())));
		
		return builder.toString();
	}

	private enum ZMoveField
	{
		TYPE("Type", "Tipo", "Type", "Tipo", "Typ", "タイプ", "屬性", "타입"),
		Z_CRYSTAL("Z Crystal", "Cristal Z", "Cristaux Z" ,"Cristallo Z" ,"Z-Kristall" ,"Ｚクリスタル " ,"Ｚ纯晶", "Z크리스탈");
		
		private Map<Language, String> titleMap;
		
		ZMoveField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
