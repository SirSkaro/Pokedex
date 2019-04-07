package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.AbilityList;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.IServiceConsumer;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class AbilityResponseFormatter implements ResponseFormatter, IServiceConsumer
{
	private IServiceManager services;
	
	public AbilityResponseFormatter(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR);
	}
	
	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder)
	{
		Language lang = input.getLanguage();
		
		if(input.getArgument(0).getCategory() == ArgumentCategory.ABILITY)
			return formatFromAbilityArgument(data, lang, builder);
		return formatFromPokemonArgument(data, lang, builder);
	}
	
	@SuppressWarnings("unchecked")
	private Response formatFromPokemonArgument(MultiMap<IFlexObject> data, Language lang, EmbedCreateSpec builder)
	{
		Response response = new Response();
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		AbilityList abilities = new AbilityList((List<Ability>)(List<?>)data.get(Ability.class.getName()), pokemon);
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		
		//Header
		response.addToReply("**__"+
			TextUtility.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+
			" | #" + species.getId() +
			" | " + TextUtility.formatGeneration(species.getGeneration().getName(), lang) + "__**");
		
		builder.addField(AbilityField.SLOT1.getFieldTitle(lang), TextUtility.flexFormToProper(abilities.getSlot1().getNameInLanguage(lang.getFlexKey())), true);
		
		if(abilities.hasSlot2())
			builder.addField(AbilityField.SLOT2.getFieldTitle(lang), TextUtility.flexFormToProper(abilities.getSlot2().getNameInLanguage(lang.getFlexKey())), true);
		
		if(abilities.hasHidden())
			builder.addField(AbilityField.HIDDEN.getFieldTitle(lang), TextUtility.flexFormToProper(abilities.getHidden().getNameInLanguage(lang.getFlexKey())), true);
		
		//Extra
		builder.setThumbnail(pokemon.getSprites().getFrontDefault());
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.setColor(colorService.getColorForType(type));
		
		response.setEmbed(builder);
		return response;
	}
	
	private Response formatFromAbilityArgument(MultiMap<IFlexObject> data, Language lang, EmbedCreateSpec builder)
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Ability abil = (Ability)data.getValue(Ability.class.getName(), 0);
		
		response.addToReply(("**__"+TextUtility.flexFormToProper(abil.getNameInLanguage(lang.getFlexKey()))+"__**").intern());
		
		builder.addField(AbilityField.DEBUT.getFieldTitle(lang), TextUtility.formatGeneration(abil.getGeneration().getName(), lang), true);
		builder.addField(AbilityField.OTHER_WITH.getFieldTitle(lang), Integer.toString(abil.getPokemon().size()), true);
		
		//English-only data
		if(lang == Language.ENGLISH)
		{
			builder.addField("Smogon Viability", abil.getRating(), true);
			builder.addField("Technical Description", abil.getLdesc(), false);
		}
		
		builder.addField(AbilityField.DESC.getFieldTitle(lang), formatDescription(abil, lang), false);
		
		builder.setColor(colorService.getColorForAbility());
		response.setEmbed(builder);
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
