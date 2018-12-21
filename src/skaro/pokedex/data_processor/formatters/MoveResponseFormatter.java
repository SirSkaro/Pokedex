package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.data_processor.ColorService;
import skaro.pokedex.data_processor.EmojiService;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.contest_type.ContestType;
import skaro.pokeflex.objects.move.Image;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.move_damage_class.MoveDamageClass;
import skaro.pokeflex.objects.move_target.MoveTarget;
import skaro.pokeflex.objects.type.Type;
import sx.blah.discord.util.EmbedBuilder;

public class MoveResponseFormatter implements IDiscordFormatter 
{

	@Override
	public Response invalidInputResponse(Input input)
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify exactly one Move as input for this command.".intern());
			break;
			case INVALID_ARGUMENT:
				response.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Move in "+input.getLanguage().getName());
			break;
			default:
				response.addToReply("A technical error occured (code 106)");
		}
		
		return response;
	}
	
	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		Response response = new Response();
		Language lang = input.getLanguage();
		builder.setLenient(true);
		Move move = (Move)data.get(Move.class.getName()).get(0);
		Type type = (Type)data.getValue(Type.class.getName(), 0);
		Optional<Image> image = move.getImage("en", 7);
		
		//Header
		response.addToReply("**__"+
				TextFormatter.flexFormToProper(move.getNameInLanguage(lang.getFlexKey()))+
				" | " + TextFormatter.formatGeneration(move.getGeneration().getName(), lang) + "__**");
		
		//Data for attacking moves
		if(!move.getDamageClass().getName().equals("status"))
		{
			builder.appendField(MoveField.BASE_POWER.getFieldTitle(lang), Integer.toString(move.getPower()), true);
			builder.appendField(MoveField.Z_POWER.getFieldTitle(lang), formatZPower(type, move.getZPower()), true);
		}
		
		//Data for all Moves
		builder.appendField(MoveField.ACCURACY.getFieldTitle(lang), (move.getAccuracy() != 0 ? Integer.toString(move.getAccuracy()) : "-"), true);
		builder.appendField(MoveField.CATEGORY.getFieldTitle(lang), formatCategory((MoveDamageClass)data.getValue(MoveDamageClass.class.getName(), 0), lang), true);
		builder.appendField(MoveField.TYPE.getFieldTitle(lang), formatType(type, lang), true);
		builder.appendField(MoveField.PP.getFieldTitle(lang), formatPP(move), true);
		builder.appendField(MoveField.PRIORITY.getFieldTitle(lang), Integer.toString(move.getPriority()), true);
		builder.appendField(MoveField.TARGET.getFieldTitle(lang), formatTarget((MoveTarget)data.getValue(MoveTarget.class.getName(), 0), lang), true);
		builder.appendField(MoveField.CONTEST.getFieldTitle(lang), formatContest((ContestType)data.getValue(ContestType.class.getName(), 0), lang), true);
		builder.appendField(MoveField.DESC.getFieldTitle(lang), formatDescription(move, lang), false);
		
		//English-only data
		if(lang == Language.ENGLISH)
		{
			if(move.getZBoost() != null)
				builder.appendField("Z-Boosts", move.getZBoost().toString(), true);
			if(move.getZEffect() != null)
				builder.appendField("Z-Effect", move.getZEffect().toString(), true);
			
			builder.appendField("Technical Description", move.getLdesc(), false);
			
			if(move.getFlags() != null)
				builder.appendField("Other Properties", formatFlags(move), false);
		}
		
		//Image
		if(image.isPresent())
			builder.withImage(image.get().getUrl());
		
		builder.withColor(ColorService.getColorForType(move.getType().getName()));
		response.setEmbededReply(builder.build());
		return response;
	}
	
	private String formatZPower(Type type, int power)
	{
		return EmojiService.getCrystalEmoji(TypeData.getByName(type.getName())) + " " + power;
	}
	
	private String formatContest(ContestType contest, Language lang)
	{
		if(contest == null)
			return null;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(EmojiService.getContestEmoji(contest.getName()));
		builder.append(" ");
		builder.append(TextFormatter.flexFormToProper(contest.getNameInLanguage(lang.getFlexKey())));
		
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
		return TextFormatter.flexFormToProper(target.getNameInLanguage(lang.getFlexKey()));
	}
	
	private String formatPP(Move move)
	{
		return move.getPp() + " - " + (int)(move.getPp() * 1.6);
	}
	
	private String formatType(Type type, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(EmojiService.getTypeEmoji(TypeData.getByName(type.getName())));
		builder.append(" ");
		builder.append(TextFormatter.flexFormToProper(type.getNameInLanguage(lang.getFlexKey())));
		
		return builder.toString();
	}
	
	private String formatCategory(MoveDamageClass category, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(EmojiService.getDamageEmoji(category.getName()));
		builder.append(" ");
		builder.append(TextFormatter.flexFormToProper(category.getNameInLanguage(lang.getFlexKey())));
		
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
