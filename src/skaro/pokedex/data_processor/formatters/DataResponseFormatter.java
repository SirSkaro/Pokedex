package skaro.pokedex.data_processor.formatters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.Statistic;
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
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.egg_group.EggGroup;
import skaro.pokeflex.objects.evolution_chain.Chain;
import skaro.pokeflex.objects.evolution_chain.EvolutionChain;
import skaro.pokeflex.objects.evolution_chain.EvolutionDetail;
import skaro.pokeflex.objects.evolution_chain.EvolvesTo;
import skaro.pokeflex.objects.growth_rate.GrowthRate;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_form.PokemonForm;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import skaro.pokeflex.objects.type.Type;

public class DataResponseFormatter implements IDiscordFormatter, IServiceConsumer
{
	private IServiceManager services;
	
	public DataResponseFormatter(IServiceManager services) throws ServiceConsumerException
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
	public Response invalidInputResponse(Input input)
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify exactly one Pokemon as input for this command.".intern());
			break;
			case INVALID_ARGUMENT:
				response.addToReply("\""+input.getArgument(0).getRawInput() +"\" is not a recognized Pokemon in " + input.getLanguage().getName());
			break;
			default:
				response.addToReply("A technical error occured (code 102)");
		}
		
		return response;
	}

	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder) 
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		EvolutionChain evoChain = (EvolutionChain)data.getValue(EvolutionChain.class.getName(), 0);
		
		response.addToReply("**__"+
				TextUtility.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+
				" | #" + species.getId() +
				" | " + TextUtility.formatGeneration(species.getGeneration().getName(), lang) + "__**");
		
		builder.addField(DataField.BASE_STATS.getFieldTitle(lang), formatBaseStats(pokemon, lang), true);
		builder.addField(DataField.TYPING.getFieldTitle(lang), formatTypes(data.get(Type.class.getName()), lang), true);
		builder.addField(DataField.ABILITIES.getFieldTitle(lang), formatAbilities(data.get(Ability.class.getName()), lang), true);
		builder.addField(DataField.HIGHT_WEIGHT.getFieldTitle(lang), formatHeightWeight(pokemon), true);
		builder.addField(DataField.EV_YIELD.getFieldTitle(lang), formatEvYield(pokemon, lang), true);
		builder.addField(DataField.GROWTH_CATCH.getFieldTitle(lang), formatGrowthAndCatchRates((GrowthRate)data.getValue(GrowthRate.class.getName(), 0), species.getCaptureRate(), lang), true);
		builder.addField(DataField.GENDER.getFieldTitle(lang), formatGenderRatio(species), true);
		builder.addField(DataField.EGG_GROUP.getFieldTitle(lang), formatEggGroups(data.get(EggGroup.class.getName()),lang), true);
		builder.addField(DataField.HATCH_TIME.getFieldTitle(lang), calcHatchTime(species, lang), true);
		
		if(hasMultipleForms(data.get(PokemonForm.class.getName())))
			builder.addField(DataField.FORMS.getFieldTitle(lang), formatForms(data.get(PokemonForm.class.getName()), species, lang), true);
		if(!isOnlyEvolution(evoChain))
		{
			builder.addField(DataField.EVO_CHAIN.getFieldTitle(lang), formatEvolutionChain(species, data.get(PokemonSpecies.class.getName()), evoChain, lang), true);
			
			String evolutionRequirements = formatEvolutionDetails(evoChain, pokemon.getName());
			if(!evolutionRequirements.isEmpty())
				builder.addField(DataField.EVO_REQ.getFieldTitle(lang), evolutionRequirements, true);
		}
		
		builder.setImage(pokemon.getModel().getUrl());
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.setColor(colorService.getColorForType(type));
		
		response.setEmbed(builder);
		return response;
	}
	
	private String formatEggGroups(List<IFlexObject> groups, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EggGroup tempGroup;
		
		for(Object group : groups)
		{
			tempGroup = (EggGroup)group;
			builder.append(TextUtility.flexFormToProper(tempGroup.getNameInLanguage(lang.getFlexKey()) + "*/* "));
		}
		
		return builder.substring(0, builder.length() - 3);
	}
	
	private boolean hasMultipleForms(List<IFlexObject> forms)
	{
		return forms.size() > 1;
	}
	
	private String formatForms(List<IFlexObject> forms, PokemonSpecies species, Language lang)
	{
		List<String> resultList = new ArrayList<String>();
		
		for(Object form : forms)
		{
			PokemonForm tempForm = (PokemonForm)form;
			String formName = TextUtility.flexFormToProper(tempForm.getFormInLanguage(lang.getFlexKey()));
			if(!resultList.contains(formName))
			{
				if(formName.isEmpty())
					resultList.add(TextUtility.flexFormToProper(species.getNameInLanguage(lang.getFlexKey())));
				else
					resultList.add(formName);
			}
		}
		
		return listToItemizedString(resultList);
	}
	
	private String calcHatchTime(PokemonSpecies species, Language lang)
	{
		return ((species.getHatchCounter() + 1) * 255) + " " + DataField.STEP_TRANSLATION.getFieldTitle(lang);
	}
	
	private String formatGenderRatio(PokemonSpecies speciesData) 
	{
		int femaleInEights = speciesData.getGenderRate();	//The ratio of female in 8ths
		
		if(femaleInEights == -1)
			return "100% ⚲";
		else if(femaleInEights == 8)
			return "100% ♀";
		else if(femaleInEights == 0)
			return "100% ♂";
		else
			return femaleInEights+"/8 ♀";
	}
	
	private String formatGrowthAndCatchRates(GrowthRate growthRate, int catchRate, Language lang)
	{
		return TextUtility.flexFormToProper(growthRate.getNameInLanguage(lang.getFlexKey())) + "*/* " + catchRate;
	}
	
	private boolean isOnlyEvolution(EvolutionChain evolutionData)
	{
		return evolutionData.getChain().getEvolvesTo().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private String formatEvolutionChain(PokemonSpecies thisPokemon, List<?> speciesInLine, EvolutionChain evoData, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		Chain chain = evoData.getChain();
		int nameIndexStart;
		PokemonSpecies tempSpecies;
		
		//first Pokemon name
		for(Object species : speciesInLine)
		{
			tempSpecies = (PokemonSpecies)species;
			if(chain.getSpecies().getName().equals(tempSpecies.getName()))
			{
				builder.append(TextUtility.flexFormToProper(tempSpecies.getNameInLanguage(lang.getFlexKey())));
				builder.append(" ➔ ");
				break;
			}
		}
		
		//recursively parse chain
		formatEvolutionChainResursive(chain.getEvolvesTo(), (List<PokemonSpecies>)speciesInLine, builder, lang);
		
		//decorate the text of this Pokemon
		String thisPokemonName = TextUtility.flexFormToProper(thisPokemon.getNameInLanguage(lang.getFlexKey()));
		nameIndexStart = builder.indexOf(thisPokemonName);
		builder.insert(nameIndexStart + thisPokemonName.length(), "__");
		builder.insert(nameIndexStart, "__");
		
		return  builder.toString();
	}
	
	private void formatEvolutionChainResursive(List<EvolvesTo> evoTo, List<PokemonSpecies> speciesInLine, StringBuilder builder, Language lang)
	{
		String pokemonName = null;
		
		for(EvolvesTo evo : evoTo)
		{
			for(PokemonSpecies species : speciesInLine)
			{
				if(evo.getSpecies().getName().equals(species.getName()))
				{
					pokemonName = species.getNameInLanguage(lang.getFlexKey());
					builder.append(TextUtility.flexFormToProper(pokemonName));
					break;
				}
			}
			
			if(!evo.getEvolvesTo().isEmpty())
			{
				builder.append(" ➔ ");
				formatEvolutionChainResursive(evo.getEvolvesTo(), speciesInLine, builder, lang);
			}
			
			if(evoTo.size() > 1)
				builder.append("/");
		}
		
		if(evoTo.size() > 1)
			builder.deleteCharAt(builder.lastIndexOf("/"));
	}
	
	private String formatEvYield(Pokemon pokemon, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		Statistic statName[] = {Statistic.HP, Statistic.ATK, Statistic.DEF, Statistic.SP_ATK, Statistic.SP_DEF, Statistic.SPE}; 
		int value;
		
		for(Statistic stat : statName)
		{
			value = pokemon.getEffotStat(stat.getAPIKey());
			if(value > 0)
				builder.append(value + " " + stat.getInLanguage(lang) + "*/* ");
		}
		
		return builder.substring(0, builder.length() - 4);
	}
	
	private String formatBaseStats(Pokemon pokemon, Language lang)
	{
		String stats1 = String.format("%s%s%d\n",
									StringUtils.rightPad(Integer.toString(pokemon.getStat(Statistic.HP.getAPIKey())), 9, " "),
									StringUtils.rightPad(Integer.toString(pokemon.getStat(Statistic.ATK.getAPIKey())), 9, " "),
									pokemon.getStat(Statistic.DEF.getAPIKey()));
		String stats2 = String.format("%s%s%d\n",
									StringUtils.rightPad(Integer.toString(pokemon.getStat(Statistic.SP_ATK.getAPIKey())), 9, " "),
									StringUtils.rightPad(Integer.toString(pokemon.getStat(Statistic.SP_DEF.getAPIKey())), 9, " "),
									pokemon.getStat(Statistic.SPE.getAPIKey()));
		String baseStats = "__`"+DataField.STAT_HEADER1.getFieldTitle(lang)+"`__\n`"+stats1+"`"
				+ "\n__`"+ DataField.STAT_HEADER2.getFieldTitle(lang)+"`__\n`"+stats2+"`";
		
		return baseStats;
	}
	
	private String formatHeightWeight(Pokemon pokemon)
	{
		return pokemon.getHeight()/10.0 + " m*/* " + pokemon.getWeight()/10.0 + " kg";
	}
	
	private String formatAbilities(List<IFlexObject> abilities, Language lang)
	{
		List<String> resultList = new ArrayList<String>();
		Ability tempAbility;
		
		for(Object abil : abilities)
		{
			tempAbility = (Ability)abil;
			resultList.add(TextUtility.flexFormToProper(tempAbility.getNameInLanguage(lang.getFlexKey())));
		}
		
		return listToItemizedString(resultList);
	}
	
	private String formatTypes(List<IFlexObject> types, Language lang)
	{
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		StringBuilder builder = new StringBuilder();
		Type tempType;
		
		for(Object type : types)
		{
			tempType = (Type)type;
			builder.append(emojiService.getTypeEmoji(tempType.getName()));
			builder.append(" ");
			builder.append(TextUtility.flexFormToProper(tempType.getNameInLanguage(lang.getFlexKey())) + "\n");
		}
		
		return builder.substring(0, builder.length());
	}
	
	private String formatEvolutionDetails(EvolutionChain evolutionData, String thisPokemon)
	{
		StringBuilder builder = new StringBuilder();
		Chain chain = evolutionData.getChain();
		List<EvolutionDetail> eDetails = null;
		
		if(chain.getSpecies().getName().equals(thisPokemon))
			eDetails = chain.getEvolutionDetails();
		else
			eDetails = extractEvolutionDetailsRecursive(chain.getEvolvesTo(), thisPokemon);
		
		for(EvolutionDetail detail : eDetails)
		{
			builder.append(TextUtility.flexFormToProper(detail.getTrigger().getName())+": ");
			
			if(detail.getMinLevel() != 0)
				builder.append("Min level: "+detail.getMinLevel() + " & ");
			if(detail.getMinBeauty() != 0)
				builder.append("Min beauty: "+detail.getMinBeauty() + " & ");
			if(detail.getTimeOfDay() != null && !detail.getTimeOfDay().isEmpty())
				builder.append("At "+detail.getTimeOfDay()+ "-time & ");
			if(detail.getGender() != 0)
			{
				switch(detail.getGender())
				{
					case 1: builder.append("Must be female & "); break;
					case 2: builder.append("Must be male & "); break;
				}
			}
			if(detail.getRelativePhysicalStats() != 0)
			{
				switch(detail.getRelativePhysicalStats())
				{
					case 0: builder.append("Attack = Defense & "); break;
					case 1: builder.append("Attack > Defense & "); break;
					case -1: builder.append("Attack < Defense & "); break;
				}
			}
			if(detail.isNeedsOverworldRain())
				builder.append("Needs overworld rain & ");
			if(detail.isTurnUpsideDown())
				builder.append("Turn 3DS upside down & ");
			if(detail.getItem() != null)
				builder.append(TextUtility.flexFormToProper(detail.getItem().getName()) +" & ");
			if(detail.getKnownMoveType() != null)
				builder.append("Know "+ TextUtility.flexFormToProper(detail.getKnownMoveType().getName()) +"-type move & ");
			if(detail.getMinAffection() != 0)
				builder.append("Min affection: "+detail.getMinAffection() + " & ");
			if(detail.getPartyType() != null)
				builder.append("With "+ TextUtility.flexFormToProper(detail.getPartyType().getName()) +"-type in party & ");
			if(detail.getTradeSpecies() != null)
				builder.append("Trade for "+ TextUtility.flexFormToProper(detail.getTradeSpecies().getName()) +" & ");
			if(detail.getPartySpecies() != null)
				builder.append("With "+ TextUtility.flexFormToProper(detail.getPartySpecies().getName()) +" as party member & ");
			if(detail.getMinHappiness() != 0)
				builder.append("Min happiness: "+detail.getMinHappiness() + " & ");
			if(detail.getHeldItem() != null)
				builder.append("Holding item "+ TextUtility.flexFormToProper(detail.getHeldItem().getName()) +" & ");
			if(detail.getKnownMove() != null)
				builder.append("Knows move "+ TextUtility.flexFormToProper(detail.getKnownMove().getName()) +" & ");
			if(detail.getLocation() != null)
				builder.append("At location "+ TextUtility.flexFormToProper(detail.getLocation().getName()) +" & ");
			
			if(builder.lastIndexOf("&") != -1)
				builder.deleteCharAt(builder.lastIndexOf("&"));
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	private List<EvolutionDetail> extractEvolutionDetailsRecursive(List<EvolvesTo> evoTo, String thisPokemon)
	{					
		for(EvolvesTo evo : evoTo)
		{
			if(evo.getSpecies().getName().equals(thisPokemon))
			{
				return evo.getEvolutionDetails();
			}
			
			if(!evo.getEvolvesTo().isEmpty())
				return extractEvolutionDetailsRecursive(evo.getEvolvesTo(), thisPokemon);
		}
		
		return new ArrayList<EvolutionDetail>();
	}
	
	private enum DataField
	{
		BASE_STATS("Base Stats", "Estadísticas Base", "Statistique de Base", "Statistiche Base", "Basiswerte", "種族値", "种族值", "종족값"),
		TYPING("Type","Tipos", "Types", "Tipi", "Typen", "タイプ", "属性", "타입"),
		ABILITIES("Ability", "Habilidad", "Talents", "Abilità", "Fähigkeiten", "とくせい", "特性", "특성"),
		HIGHT_WEIGHT("Height & Weight", "Altura & Peso", "Taille & Poids", "Altezza & Peso", "Größe & Gewicht", "たかさ & おもさ", "身高 & 体重", "키 & 몸무게"),
		EV_YIELD("EV Yield","Puntos de Esfuerzo", "Points Effort", "PA Ceduti", "FP", "獲得努力値", "取得基础点数", "노력치"),
		GROWTH_CATCH("Growth & Capture Rates","Ratio de Crecimiento & Captura", "Taux de Capture & Croissance", "Crescita e Tasso di Cattura", "Wachstumsrate & Fangrate", "成長速度 & 捕捉率", "增长率 & 捕获率", "성장률 & 포획률"),
		GENDER("Gender Ratio","Ratio de Género", "Sexe", "Sesso", "Geschlecht", "性別", "性别比例", "성비"),
		EGG_GROUP("Egg Groups", "Grupos Huevos", "Groupe œuf", "Uova", "Ei-Gruppen", "タマゴグループ", "蛋群", "알그룹"),
		HATCH_TIME("Hatch Time", "Pasos para la Eclosión", "Éclosion", "Passi per Tratteggio", "Ei-Schritte", "タマゴの歩数", "孵化周期", "부화 걸음수"),
		FORMS("Forms", "Formas", "Formes", "Forme", "Formwandel", "フォーム", "形态", "폼체인지"),
		EVO_CHAIN("Evolution Line", "Línea Evolución", "Ligne d'évolution", "Evoluzioni", "Entwicklung", "進化", "进化", "진화 단계"),
		EVO_REQ("Evolution Requirements", "Requisitos de Evolución", "Conditions d'évolution", "Requisiti di Evoluzione", "Entwicklungsanforderungen", "進化要件", "进化要求", "진화 요구 사항"),
		
		ERROR(),
		STAT_HEADER1(String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.ENGLISH), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.ENGLISH), 9, " "), Statistic.DEF.getInLanguage(Language.ENGLISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.SPANISH), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.SPANISH), 9, " "), Statistic.DEF.getInLanguage(Language.SPANISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.FRENCH), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.FRENCH), 9, " "), Statistic.DEF.getInLanguage(Language.FRENCH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.ITALIAN), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.ITALIAN), 9, " "), Statistic.DEF.getInLanguage(Language.ITALIAN)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.GERMAN), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.GERMAN), 9, " "), Statistic.DEF.getInLanguage(Language.GERMAN)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.JAPANESE_HIR_KAT), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.JAPANESE_HIR_KAT), 7, " "), Statistic.DEF.getInLanguage(Language.JAPANESE_HIR_KAT)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.CHINESE_SIMPMLIFIED), 7, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.CHINESE_SIMPMLIFIED), 7, " "), Statistic.DEF.getInLanguage(Language.CHINESE_SIMPMLIFIED)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.KOREAN), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.KOREAN), 7, " "), Statistic.DEF.getInLanguage(Language.KOREAN))
				),
		
		STAT_HEADER2(String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.ENGLISH), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.ENGLISH), 9, " "), Statistic.SPE.getInLanguage(Language.ENGLISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.SPANISH), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.SPANISH), 9, " "), Statistic.SPE.getInLanguage(Language.SPANISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.FRENCH), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.FRENCH), 9, " "), Statistic.SPE.getInLanguage(Language.FRENCH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.ITALIAN), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.ITALIAN), 9, " "), Statistic.SPE.getInLanguage(Language.ITALIAN)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.GERMAN), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.GERMAN), 9, " "), Statistic.SPE.getInLanguage(Language.GERMAN)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.JAPANESE_HIR_KAT), 6, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.JAPANESE_HIR_KAT), 7, " "), Statistic.SPE.getInLanguage(Language.JAPANESE_HIR_KAT)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.CHINESE_SIMPMLIFIED), 7, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.CHINESE_SIMPMLIFIED), 7, " "), Statistic.SPE.getInLanguage(Language.CHINESE_SIMPMLIFIED)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.KOREAN), 6, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.KOREAN), 5, " "), Statistic.SPE.getInLanguage(Language.KOREAN))
				),
		
		STEP_TRANSLATION("steps","pasos","pas", "passi", "schritte", "ステップ", "步", "걸음"),
		;
		
		private Map<Language, String> titleMap;
		
		DataField() {}
		DataField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
