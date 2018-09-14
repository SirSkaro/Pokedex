package skaro.pokedex.data_processor.formatters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
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
import sx.blah.discord.util.EmbedBuilder;

public class DataResponseFormatter implements IDiscordFormatter 
{
	public DataResponseFormatter()
	{
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
				response.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Pokemon.");
			break;
			default:
				response.addToReply("A technical error occured (code 102)");
		}
		
		return response;
	}

	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		Response response = new Response();
		Language lang = input.getLanguage();
		Pokemon pokemon = (Pokemon) (data.get(Pokemon.class.getName()).get(0));
		PokemonSpecies species = (PokemonSpecies)data.get(PokemonSpecies.class.getName()).get(0);
		EvolutionChain evoChain = (EvolutionChain)data.get(EvolutionChain.class.getName()).get(0);
		builder.setLenient(true);
		
		//Header
		response.addToReply("**__"+
				TextFormatter.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+
				" | #" + species.getId() +
				" | " + TextFormatter.formatGeneration(species.getGeneration().getName(), lang) + "__**");
		
		//Body
		builder.appendField(DataField.BASE_STATS.getFieldTitle(lang), formatBaseStats(pokemon, lang), true);
		builder.appendField(DataField.TYPING.getFieldTitle(lang), formatTypes(data.get(Type.class.getName()), lang), true);
		builder.appendField(DataField.ABILITIES.getFieldTitle(lang), formatAbilities(data.get(Ability.class.getName()), lang), true);
		builder.appendField(DataField.HIGHT_WEIGHT.getFieldTitle(lang), formatHeightWeight(pokemon), true);
		builder.appendField(DataField.EV_YIELD.getFieldTitle(lang), formatEvYield(pokemon, lang), true);
		builder.appendField(DataField.GROWTH_CATCH.getFieldTitle(lang), formatGrowthAndCatchRates((GrowthRate)data.get(GrowthRate.class.getName()).get(0), species.getCaptureRate(), lang), true);
		builder.appendField(DataField.GENDER.getFieldTitle(lang), formatGenderRatio(species), true);
		builder.appendField(DataField.EGG_GROUP.getFieldTitle(lang), formatEggGroups(data.get(EggGroup.class.getName()),lang), true);
		builder.appendField(DataField.HATCH_TIME.getFieldTitle(lang), calcHatchTime(species, lang), true);
		
		//Optional data
		if(hasMultipleForms(data.get(PokemonForm.class.getName())))
			builder.appendField(DataField.FORMS.getFieldTitle(lang), formatForms(data.get(PokemonForm.class.getName()), species, lang), true);
		if(!isOnlyEvolution(evoChain))
		{
			builder.appendField(DataField.EVO_CHAIN.getFieldTitle(lang), formatEvolutionChain(species, data.get(PokemonSpecies.class.getName()), evoChain, lang), true);
			builder.appendField(DataField.EVO_REQ.getFieldTitle(lang), formatEvolutionDetails(evoChain, pokemon.getName()), true);
		}
		
		//Extra
		builder.withImage(pokemon.getModel().getUrl());
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		
		response.setEmbededReply(builder.build());
		return response;
	}
	
	private String formatEggGroups(List<Object> groups, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EggGroup tempGroup;
		
		for(Object group : groups)
		{
			tempGroup = (EggGroup)group;
			builder.append(tempGroup.getNameInLanguage(lang.getFlexKey()) + "*/* ");
		}
		
		return builder.substring(0, builder.length() - 4);
	}
	
	private boolean hasMultipleForms(List<Object> forms)
	{
		return forms.size() > 1;
	}
	
	private String formatForms(List<Object> forms, PokemonSpecies species, Language lang)
	{
		List<String> resultList = new ArrayList<String>();
		PokemonForm tempForm;
		String formName;
		
		for(Object form : forms)
		{
			tempForm = (PokemonForm)form;
			formName = TextFormatter.flexFormToProper(tempForm.getFormInLanguage(lang.getFlexKey()));
			if(!resultList.contains(formName))
			{
				if(formName.isEmpty())
					resultList.add(TextFormatter.flexFormToProper(species.getNameInLanguage(lang.getFlexKey())));
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
		return TextFormatter.flexFormToProper(growthRate.getNameInLanguage(lang.getFlexKey())) + "*/* " + catchRate;
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
				builder.append(TextFormatter.flexFormToProper(tempSpecies.getNameInLanguage(lang.getFlexKey())));
				builder.append(" -> ");
				break;
			}
		}
		
		//recursively parse chain
		formatEvolutionChainResursive(chain.getEvolvesTo(), (List<PokemonSpecies>)speciesInLine, builder, lang);
		
		//decorate the text of this Pokemon
		String thisPokemonName = TextFormatter.flexFormToProper(thisPokemon.getNameInLanguage(lang.getFlexKey()));
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
					builder.append(TextFormatter.flexFormToProper(pokemonName));
					break;
				}
			}
			
			if(!evo.getEvolvesTo().isEmpty())
			{
				builder.append(" -> ");
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
		
		for(int i = 0; i < 6; i++)
		{
			int stat = pokemon.getStats().get(5-i).getEffort();
			if(stat != 0)
				builder.append(stat + " " + statName[i].getInLanguage(lang) + "*/* ");
		}
		
		return builder.substring(0, builder.length() - 4);
	}
	
	private String formatBaseStats(Pokemon pokemon, Language lang)
	{
		int[] stats = new int[6];
		
		for(int i = 0; i < 6; i++)
			stats[5-i] = pokemon.getStats().get(i).getBaseStat();
		
		String stats1 = String.format("%s%s%d\n", StringUtils.rightPad(Integer.toString(stats[0]), 9, " "), StringUtils.rightPad(Integer.toString(stats[1]), 9, " "), stats[2]);
		String stats2 = String.format("%s%s%d\n", StringUtils.rightPad(Integer.toString(stats[3]), 9, " "), StringUtils.rightPad(Integer.toString(stats[4]), 9, " "), stats[5]);
		String baseStats = "__`"+DataField.STAT_HEADER1.getFieldTitle(lang)+"`__\n`"+stats1+"`"
				+ "\n__`"+ DataField.STAT_HEADER2.getFieldTitle(lang)+"`__\n`"+stats2+"`";
		
		return baseStats;
	}
	
	private String formatHeightWeight(Pokemon pokemon)
	{
		return pokemon.getHeight()/10.0 + " m*/* " + pokemon.getWeight()/10.0 + " kg";
	}
	
	private String formatAbilities(List<Object> abilities, Language lang)
	{
		List<String> resultList = new ArrayList<String>();
		Ability tempAbility;
		
		for(Object abil : abilities)
		{
			tempAbility = (Ability)abil;
			resultList.add(TextFormatter.flexFormToProper(tempAbility.getNameInLanguage(lang.getFlexKey())));
		}
		
		return listToItemizedString(resultList);
	}
	
	private String formatTypes(List<Object> types, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		Type tempType;
		
		for(Object type : types)
		{
			tempType = (Type)type;
			builder.append(tempType.getNameInLanguage(lang.getFlexKey()) + "*/* ");
		}
		
		return builder.substring(0, builder.length() - 4);
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
			builder.append(TextFormatter.flexFormToProper(detail.getTrigger().getName())+": ");
			
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
				builder.append(TextFormatter.flexFormToProper(detail.getItem().getName()) +" & ");
			if(detail.getKnownMoveType() != null)
				builder.append("Know "+ TextFormatter.flexFormToProper(detail.getKnownMoveType().getName()) +"-type move & ");
			if(detail.getMinAffection() != 0)
				builder.append("Min affection: "+detail.getMinAffection() + " & ");
			if(detail.getPartyType() != null)
				builder.append("With "+ TextFormatter.flexFormToProper(detail.getPartyType().getName()) +"-type in party & ");
			if(detail.getTradeSpecies() != null)
				builder.append("Trade for "+ TextFormatter.flexFormToProper(detail.getTradeSpecies().getName()) +" & ");
			if(detail.getPartySpecies() != null)
				builder.append("With "+ TextFormatter.flexFormToProper(detail.getPartySpecies().getName()) +" as party member & ");
			if(detail.getMinHappiness() != 0)
				builder.append("Min happiness: "+detail.getMinHappiness() + " & ");
			if(detail.getHeldItem() != null)
				builder.append("Holding item "+ TextFormatter.flexFormToProper(detail.getHeldItem().getName()) +" & ");
			if(detail.getKnownMove() != null)
				builder.append("Knows move "+ TextFormatter.flexFormToProper(detail.getKnownMove().getName()) +" & ");
			if(detail.getLocation() != null)
				builder.append("At location "+ TextFormatter.flexFormToProper(detail.getLocation().getName()) +" & ");
			
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
		BASE_STATS("Base Stats", "Estadísticas Base", "Statistique de Base", "Statistiche Base"),
		TYPING("Typing","Tipos", "Types", "Tipi"),
		ABILITIES("Ability", "Habilidad", "Talents", "Abilità"),
		HIGHT_WEIGHT("Height & Weight", "Altura & Peso", "Taille & Poids", "Altezza & Peso"),
		EV_YIELD("EV Yield","Puntos de Esfuerzo", "Points Effort", "PA Ceduti"),
		GROWTH_CATCH("Growth & Capture Rates","Ratio de Crecimiento & Captura", "Taux de Capture & Croissance", "Crescita e Tasso di Cattura"),
		GENDER("Gender Ratio","Ratio de Género", "Sexe", "Sesso"),
		EGG_GROUP("Egg Groups", "Grupos Huevos", "Groupe œuf", "Uova"),
		HATCH_TIME("Hatch Time", "Pasos para la Eclosión", "Éclosion", "Passi per Tratteggio"),
		FORMS("Forms", "Formas", "Formes", "Forme"),
		EVO_CHAIN("Evolution Chain", "Línea Evolución", "Ligne d'évolution", "Evoluzioni"),
		EVO_REQ("Evolution Requirements", "Requisitos de Evolución", "Conditions d'évolution", "Requisiti di Evoluzione"),
		
		ERROR(),
		STAT_HEADER1(String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.ENGLISH), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.ENGLISH), 9, " "), Statistic.DEF.getInLanguage(Language.ENGLISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.SPANISH), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.SPANISH), 9, " "), Statistic.DEF.getInLanguage(Language.SPANISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.FRENCH), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.FRENCH), 9, " "), Statistic.DEF.getInLanguage(Language.FRENCH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.HP.getInLanguage(Language.ITALIAN), 9, " "), StringUtils.rightPad(Statistic.ATK.getInLanguage(Language.ITALIAN), 9, " "), Statistic.DEF.getInLanguage(Language.ITALIAN))
				),
		
		STAT_HEADER2(String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.ENGLISH), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.ENGLISH), 9, " "), Statistic.SPE.getInLanguage(Language.ENGLISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.SPANISH), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.SPANISH), 9, " "), Statistic.SPE.getInLanguage(Language.SPANISH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.FRENCH), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.FRENCH), 9, " "), Statistic.SPE.getInLanguage(Language.FRENCH)),
				String.format("%s%s%s\n", StringUtils.rightPad(Statistic.SP_ATK.getInLanguage(Language.ITALIAN), 9, " "), StringUtils.rightPad(Statistic.SP_DEF.getInLanguage(Language.ITALIAN), 9, " "), Statistic.SPE.getInLanguage(Language.ITALIAN))
				),
		
		STEP_TRANSLATION("steps","pasos","pas", "passi"),
		;
		
		private Map<Language, String> titleMap;
		
		DataField() {}
		DataField(String english, String spanish, String french, String italian)
		{
			titleMap = new HashMap<Language, String>();
			titleMap.put(Language.ENGLISH, english);
			titleMap.put(Language.SPANISH, spanish);
			titleMap.put(Language.FRENCH, french);
			titleMap.put(Language.ITALIAN, italian);
		}
		
		public String getFieldTitle(Language lang)
		{
			return titleMap.get(lang);
		}
	}
}
