package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.EmojiService;
import skaro.pokedex.services.IServiceConsumer;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.contest_type.ContestType;
import skaro.pokeflex.objects.move.Image;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.move_damage_class.MoveDamageClass;
import skaro.pokeflex.objects.move_target.MoveTarget;
import skaro.pokeflex.objects.type.Type;

public class MoveResponseFormatter implements ResponseFormatter, IServiceConsumer
{
	private IServiceManager services;
	
	public MoveResponseFormatter(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
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
		Optional<Image> image = move.getImage("en", 7);
		
		response.addToReply("**__"+
				TextUtility.flexFormToProper(move.getNameInLanguage(lang.getFlexKey()))+
				" | " + TextUtility.formatGeneration(move.getGeneration().getName(), lang) + "__**");
		
		if(!move.getDamageClass().getName().equals("status"))
		{
			builder.addField(MoveField.BASE_POWER.getFieldTitle(lang), Integer.toString(move.getPower()), true);
			builder.addField(MoveField.Z_POWER.getFieldTitle(lang), formatZPower(type, move.getZPower()), true);
		}
		
		builder.addField(MoveField.ACCURACY.getFieldTitle(lang), (move.getAccuracy() != 0 ? Integer.toString(move.getAccuracy()) : "-"), true);
		builder.addField(MoveField.CATEGORY.getFieldTitle(lang), formatCategory((MoveDamageClass)data.getValue(MoveDamageClass.class.getName(), 0), lang), true);
		builder.addField(MoveField.TYPE.getFieldTitle(lang), formatType(type, lang), true);
		builder.addField(MoveField.PP.getFieldTitle(lang), formatPP(move), true);
		builder.addField(MoveField.PRIORITY.getFieldTitle(lang), Integer.toString(move.getPriority()), true);
		builder.addField(MoveField.TARGET.getFieldTitle(lang), formatTarget((MoveTarget)data.getValue(MoveTarget.class.getName(), 0), lang), true);
		if(data.containsKey(ContestType.class.getName()))
			builder.addField(MoveField.CONTEST.getFieldTitle(lang), formatContest((ContestType)data.getValue(ContestType.class.getName(), 0), lang), true);
		builder.addField(MoveField.DESC.getFieldTitle(lang), formatDescription(move, lang), false);
		
		if(lang == Language.ENGLISH)
		{
			if(move.getZBoost() != null)
				builder.addField("Z-Boosts", move.getZBoost().toString(), true);
			if(move.getZEffect() != null)
				builder.addField("Z-Effect", move.getZEffect().toString(), true);
			
			builder.addField("Technical Description", move.getLdesc(), false);
			
			if(move.getFlags() != null && !move.getFlags().isEmpty())
				builder.addField("Other Properties", formatFlags(move), false);
		}
		
		if(image.isPresent())
			builder.setImage(image.get().getUrl());
		
		builder.setColor(colorService.getColorForType(move.getType().getName()));
		response.setEmbed(builder);
		return response;
	}
	
	private String formatZPower(Type type, int power)
	{
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		return emojiService.getCrystalEmoji(type.getName()) + " " + power;
	}
	
	private String formatContest(ContestType contest, Language lang)
	{
		if(contest == null)
			return null;
		
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		builder.append(emojiService.getContestEmoji(contest.getName()));
		builder.append(" ");
		builder.append(TextUtility.flexFormToProper(contest.getNameInLanguage(lang.getFlexKey())));
		
		return builder.toString();
	}
	
	private String formatFlags(Move move)
	{
		StringBuilder flagBuilder = new StringBuilder();
		for(String flag : move.getFlags())
			flagBuilder.append(flag + " ");
		
		return flagBuilder.toString();
	}
	
	private String formatDescription(Move move, Language lang)
	{
		Optional<String> desc = move.getFlavorTextEntry(lang.getFlexKey(), "sun-moon");
		
		if(desc.isPresent())
			return desc.get().replaceAll("\\n", " ");
		
		return null;
	}
	
	private String formatTarget(MoveTarget target, Language lang)
	{
		return TextUtility.flexFormToProper(target.getNameInLanguage(lang.getFlexKey()));
	}
	
	private String formatPP(Move move)
	{
		return move.getPp() + " - " + (int)(move.getPp() * 1.6);
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
	
	private String formatCategory(MoveDamageClass category, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		builder.append(emojiService.getDamageEmoji(category.getName()));
		builder.append(" ");
		builder.append(TextUtility.flexFormToProper(category.getNameInLanguage(lang.getFlexKey())));
		
		return builder.toString();
	}

	private enum MoveField
	{
		BASE_POWER("Base Power", "Potencia", "Puissance", "Potenza", "Stärke", "威力", "威力", "위력"),
		Z_POWER("Z Power", "Potencia Z", "Puissance Z", "Potenza Z", "Z-Stärke", "威力-Z", "Z威力", "Z 위력"),
		ACCURACY("Accuracy", "Precisión", "Précision", "Precisione", "Genauigkeit", "命中率", "命中", "명중률"),
		CATEGORY("Category", "Categoría", "Catégorie", "Categoria Danno", "Schadensklasse", "分類", "分類", "분류"),
		TYPE("Type", "Tipo", "Type", "Tipo", "Typ", "タイプ", "屬性", "타입"),
		PP("Power Points", "Puntos de Poder", "Points de Pouvoir", "Punti Potenza", "Angriffspunkte", "パワーポイント", "招式点数", "PP"),
		PRIORITY("Priority", "Prioridad", "Capacité Prioritaire", "Priorità", "Priorität", "優先度", "优先度", "우선도"),
		TARGET("Target", "Blanco", "Cible", "Bersaglio", "Zielerfassung", "範囲", "作用范围", "범위"),
		CONTEST("Contest", "Concurso", "Concours", "Gare", "Wettbewerb", "アピールタイプ", "华丽大赛", "콘테스트"),
		DESC("Description", "Descripción", "Description", "La Description", "Beschreibung", "説明", "描述", "기술"),
		;
		
		private Map<Language, String> titleMap;
		
		MoveField() {}
		MoveField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
