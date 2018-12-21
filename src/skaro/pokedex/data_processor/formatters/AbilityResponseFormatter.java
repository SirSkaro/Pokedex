package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.data_processor.AbilityList;
import skaro.pokedex.data_processor.ColorService;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.util.EmbedBuilder;

public class AbilityResponseFormatter implements IDiscordFormatter 
{

	@Override
	public Response invalidInputResponse(Input input)
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify exactly one Pokemon or Ability as input for this command.".intern());
			break;
			case INVALID_ARGUMENT:
				response.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Pokemon or Ability in " + input.getLanguage().getName());
			break;
			default:
				response.addToReply("A technical error occured (code 103)");
		}
		
		return response;
	}
	
	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder)
	{
		Language lang = input.getLanguage();
		builder.setLenient(true);
		
		if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
			return formatFromAbilityArgument(data, lang, builder);
		return formatFromPokemonArgument(data, lang, builder);
	}
	
	@SuppressWarnings("unchecked")
	private Response formatFromPokemonArgument(MultiMap<Object> data, Language lang, EmbedBuilder builder)
	{
		Response response = new Response();
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		AbilityList abilities = new AbilityList((List<Ability>)(List<?>)data.get(Ability.class.getName()), pokemon);
		
		//Header
		response.addToReply("**__"+
			TextFormatter.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+
			" | #" + species.getId() +
			" | " + TextFormatter.formatGeneration(species.getGeneration().getName(), lang) + "__**");
		
		builder.appendField(AbilityField.SLOT1.getFieldTitle(lang), TextFormatter.flexFormToProper(abilities.getSlot1().getNameInLanguage(lang.getFlexKey())), true);
		
		if(abilities.hasSlot2())
			builder.appendField(AbilityField.SLOT2.getFieldTitle(lang), TextFormatter.flexFormToProper(abilities.getSlot2().getNameInLanguage(lang.getFlexKey())), true);
		
		if(abilities.hasHidden())
			builder.appendField(AbilityField.HIDDEN.getFieldTitle(lang), TextFormatter.flexFormToProper(abilities.getHidden().getNameInLanguage(lang.getFlexKey())), true);
		
		//Extra
		builder.withThumbnail(pokemon.getSprites().getFrontDefault());
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorService.getColorForType(type));
		
		response.setEmbededReply(builder.build());
		return response;
	}
	
	private Response formatFromAbilityArgument(MultiMap<Object> data, Language lang, EmbedBuilder builder)
	{
		Response response = new Response();
		
		Ability abil = (Ability)data.getValue(Ability.class.getName(), 0);
		
		response.addToReply(("**__"+TextFormatter.flexFormToProper(abil.getNameInLanguage(lang.getFlexKey()))+"__**").intern());
		
		builder.appendField(AbilityField.DEBUT.getFieldTitle(lang), TextFormatter.formatGeneration(abil.getGeneration().getName(), lang), true);
		builder.appendField(AbilityField.OTHER_WITH.getFieldTitle(lang), Integer.toString(abil.getPokemon().size()), true);
		
		//English-only data
		if(lang == Language.ENGLISH)
		{
			builder.appendField("Smogon Viability", abil.getRating(), true);
			builder.appendField("Technical Description", abil.getLdesc(), false);
		}
		
		builder.appendField(AbilityField.DESC.getFieldTitle(lang), formatDescription(abil, lang), false);
		
		builder.withColor(ColorService.getColorForAbility());
		response.setEmbededReply(builder.build());
		return response;
	}

	private String formatDescription(Ability abil, Language lang)
	{
		Optional<String> desc = abil.getFlavorTextEntry(lang.getFlexKey(), "sun-moon");
		
		if(desc.isPresent())
			return desc.get().replaceAll("\\n", " ");
		
		return null;
	}
	
	private enum AbilityField
	{
		SLOT1("Ability 1", "Habilidad 1", "Talent n° 1", "Prima Abilità", "Primäre Fähigkeit", "特性1", "第一特性", "숨겨진 특성 1"),
		SLOT2("Ability 2", "Habilidad 2", "Talent n° 2", "Seconda Abilità", "Sekundäre Fähigkeit", "特性2", "第二特性", "숨겨진 특성 2"),
		HIDDEN("Hidden Ability", "Habilidad Oculta", "Talent Caché", "Abilità Nascosta", "Versteckte Fähigkeit", "かくれとくせい", "隱藏特性", "숨겨진 특성"),
		
		DEBUT("Debut", "Debut", "Début", "Debutto", "Debüt", "デビュー", "出道", "데뷔"),
		OTHER_WITH("Pokemon with this Ability", "Pokémon con esta Habilidad", "Pokémon avec cette Talents", "Pokémon con questa Abilità", "Pokemon mit dieser Fähigkeit", "所有ポケモン：そのポケモンの別のとくせい", "具有該特性的寶可夢", "이 특성인 포켓몬"),
		DESC("Description", "Descripción", "Description", "La Description", "Beschreibung", "説明", "描述", "기술"),
		;
		
		private Map<Language, String> titleMap;
		
		AbilityField() {}
		AbilityField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
