package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.Statistic;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.Stat;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.util.EmbedBuilder;

public class StatsResponseFormatter implements IDiscordFormatter
{

	@Override
	public Response invalidInputResponse(Input input) 
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify exactly one Pokemon as input for this command.".intern());
			break;
			case INVALID_ARGUMENT:
				response.addToReply("\""+ input.getArg(0).getRawInput() +"\" is not a recognized Pokemon");
			break;
			default:
				response.addToReply("A technical error occured (code 101)");
		}
		
		return response;
	}
	
	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		Response response = new Response();
		String type;
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		Language lang = input.getLanguage();
		builder.setLenient(true);
		
		//header
		response.addToReply(("**__"+TextFormatter.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+"__**").intern());
		
		//main content
		builder.withTitle(StatField.BASE_STAT_TOTAL.getFieldTitle(lang) +": "+ getBaseStatTotal(pokemon));
		
		String stats1 = String.format("%s%d\n",
				StringUtils.rightPad(Integer.toString(pokemon.getStat(Statistic.HP.getAPIKey())), 13, " "),
				pokemon.getStat(Statistic.ATK.getAPIKey()));
		String stats2 = String.format("%s%d\n",
						StringUtils.rightPad(Integer.toString(pokemon.getStat(Statistic.DEF.getAPIKey())), 13, " "),
						pokemon.getStat(Statistic.SP_ATK.getAPIKey()));
		String stats3 = String.format("%s%d\n",
						StringUtils.rightPad(Integer.toString(pokemon.getStat(Statistic.SP_DEF.getAPIKey())), 13, " "),
						pokemon.getStat(Statistic.SPE.getAPIKey()));
		
		builder.withDescription("__`"+StatField.STAT_HEADER1.getFieldTitle(lang)+"`__\n`"+stats1+"`"
					+ "\n\n__`"+ StatField.STAT_HEADER2.getFieldTitle(lang)+"`__\n`"+stats2+"`"
					+ "\n\n__`"+ StatField.STAT_HEADER3.getFieldTitle(lang)+"`__\n`"+stats3 +"`");
		
		//Set color
		type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		//Add thumbnail
		builder.withThumbnail(pokemon.getSprites().getFrontDefault());
		
		response.setEmbededReply(builder.build());
		return response;
	}
	
	private int getBaseStatTotal(Pokemon pokemon)
	{
		int total = 0;
		
		for(Stat stat : pokemon.getStats())
			total += stat.getBaseStat();
		
		return total;
	}

	private enum StatField
	{
		
		BASE_STAT_TOTAL("Base Stat Total", "Total", "Somme des Statistiques de Base", "Statistiche Base Totali", "Basiswertsumme", "合計", "总和", "총합"),
		
		STAT_HEADER1(String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.ENGLISH), 13, " "), Statistic.ATTACK.getInLanguage(Language.ENGLISH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.SPANISH), 13, " "), Statistic.ATTACK.getInLanguage(Language.SPANISH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.FRENCH), 13, " "), Statistic.ATTACK.getInLanguage(Language.FRENCH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.ITALIAN), 13, " "), Statistic.ATTACK.getInLanguage(Language.ITALIAN)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.GERMAN), 13, " "), Statistic.ATTACK.getInLanguage(Language.GERMAN)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.JAPANESE_HIR_KAT), 9, " "), Statistic.ATTACK.getInLanguage(Language.JAPANESE_HIR_KAT)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.CHINESE_SIMPMLIFIED), 11, " "), Statistic.ATTACK.getInLanguage(Language.CHINESE_SIMPMLIFIED)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.HIT_POINTS.getInLanguage(Language.KOREAN), 13, " "), Statistic.ATTACK.getInLanguage(Language.KOREAN))
				),
		STAT_HEADER2(String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.ENGLISH), 13, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.ENGLISH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.SPANISH), 13, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.SPANISH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.FRENCH), 13, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.FRENCH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.ITALIAN), 13, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.ITALIAN)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.GERMAN), 13, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.GERMAN)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.JAPANESE_HIR_KAT), 10, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.JAPANESE_HIR_KAT)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.CHINESE_SIMPMLIFIED), 11, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.CHINESE_SIMPMLIFIED)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.DEFENSE.getInLanguage(Language.KOREAN), 11, " "), Statistic.SPECIAL_ATTACK.getInLanguage(Language.KOREAN))
				),
		STAT_HEADER3(String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.ENGLISH), 13, " "), Statistic.SPEED.getInLanguage(Language.ENGLISH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.SPANISH), 13, " "), Statistic.SPEED.getInLanguage(Language.SPANISH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.FRENCH), 13, " "), Statistic.SPEED.getInLanguage(Language.FRENCH)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.ITALIAN), 13, " "), Statistic.SPEED.getInLanguage(Language.ITALIAN)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.GERMAN), 13, " "), Statistic.SPEED.getInLanguage(Language.GERMAN)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.JAPANESE_HIR_KAT), 10, " "), Statistic.SPEED.getInLanguage(Language.JAPANESE_HIR_KAT)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.CHINESE_SIMPMLIFIED), 11, " "), Statistic.SPEED.getInLanguage(Language.CHINESE_SIMPMLIFIED)),
				String.format("%s%s\n", StringUtils.rightPad(Statistic.SPECIAL_DEFENSE.getInLanguage(Language.KOREAN), 10, " "), Statistic.SPEED.getInLanguage(Language.KOREAN))
				),
		;
		
		private Map<Language, String> titleMap;
		
		StatField() {}
		StatField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
