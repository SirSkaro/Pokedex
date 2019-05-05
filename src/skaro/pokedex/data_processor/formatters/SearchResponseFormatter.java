package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.SearchCriteriaFilter;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.EmojiService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import skaro.pokeflex.objects.type.Type;

public class SearchResponseFormatter implements ResponseFormatter, PokedexServiceConsumer
{
	private PokedexServiceManager services;
	
	public SearchResponseFormatter(PokedexServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
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
		List<Ability> abilities = (List<Ability>)(List<?>)data.get(Ability.class.getName());
		List<Type> types = (List<Type>)(List<?>)data.get(Type.class.getName());
		List<Move> moves = (List<Move>)(List<?>)data.get(Move.class.getName());
		List<PokemonSpecies> species = (List<PokemonSpecies>)(List<?>)data.get(PokemonSpecies.class.getName());
		SearchCriteriaFilter filter = (SearchCriteriaFilter)data.getValue(SearchCriteriaFilter.class.getName(), 0);
		
		response.addToReply(SearchField.SEARCH_RESULT.getFieldTitle(lang));
		
		if(abilities != null && !abilities.isEmpty())
			builder.addField(SearchField.ABILITIY.getFieldTitle(lang), formatAbilities(abilities, lang), true);
		
		if(types != null && !types.isEmpty())
			builder.addField(SearchField.TYPE.getFieldTitle(lang), formatTypes(types, lang), true);
		
		if(moves != null && !moves.isEmpty())
			builder.addField(SearchField.MOVE.getFieldTitle(lang), formatMoves(moves, lang), true);
		
		builder.setDescription(formatPokemon(species, filter, lang));
		
		builder.setColor(colorService.getPokedexColor());
		response.setEmbed(builder);
		
		return response;
	}
	
	private String formatAbilities(List<Ability> abilities, Language lang)
	{
		return abilities.stream()
				.map(ability -> ability.getNameInLanguage(lang.getFlexKey()))
				.map(abilityName -> TextUtility.flexFormToProper(abilityName))
				.collect(Collectors.joining("\n"));
	}
	
	private String formatTypes(List<Type> types, Language lang)
	{
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		return types.stream()
				.map(type -> emojiService.getTypeEmoji(type.getName()) + TextUtility.flexFormToProper(type.getNameInLanguage(lang.getFlexKey())))
				.collect(Collectors.joining("\n"));
	}
	
	private String formatMoves(List<Move> moves, Language lang)
	{
		return moves.stream()
				.map(move -> move.getNameInLanguage(lang.getFlexKey()))
				.map(moveName -> TextUtility.flexFormToProper(moveName))
				.collect(Collectors.joining("\n"));
	}
	
	private String formatPokemon(List<PokemonSpecies> species, SearchCriteriaFilter filter, Language lang)
	{
		if(species == null || species.isEmpty())
			return SearchField.NO_RESULT.getFieldTitle(lang);
		
		StringBuilder builder = new StringBuilder();
		
		if(filter.hasMoreResultsThan(10))
			builder.append("Your search had too many results. Here are the first 10 results.")
					.append("\n\n");
			
		String pokemon = species.stream()
				.map(specie -> specie.getNameInLanguage(lang.getFlexKey()))
				.map(specieName -> TextUtility.flexFormToProper(specieName))
				.collect(Collectors.joining("\n"));
		
		builder.append(pokemon);
		return builder.toString();
	}
	
	private enum SearchField
	{
		TYPE("Type", "Tipo", "Type", "Tipo", "Typ", "タイプ", "屬性", "타입"),
		ABILITIY("Ability", "Habilidad", "Talents", "Abilità", "Fähigkeiten", "とくせい", "特性", "특성"),
		MOVE("Move", "Movimiento", "Attaque", "Mossa", "Attacke", "わざ", "招式", "기술"),
		SEARCH_RESULT("Search Result", "Resultado de Búsqueda", "Résultat de la Recherche", 
				"Risultato della Ricerca", "Suchergebnis", "検索結果", "搜索结果", "검색 결과"),
		NO_RESULT("No Result", "No Hay Resultados", "Aucun Résultat", "Nessun Risultato", "Keine Ergebnisse", "結果がありません", "没有结果", "결과 없음"),
		;
		
		private Map<Language, String> titleMap;
		
		SearchField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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
