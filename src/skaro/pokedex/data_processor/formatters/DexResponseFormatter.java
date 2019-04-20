package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokedex.services.TextToSpeechService;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import skaro.pokeflex.objects.version.Version;

public class DexResponseFormatter implements ResponseFormatter, PokedexServiceConsumer
{
	private PokedexServiceManager services;

	public DexResponseFormatter(PokedexServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");

		this.services = services;
	}

	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR, ServiceType.TTS);
	}

	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder) 
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		Version version = (Version)data.getValue(Version.class.getName(), 0);
		Language lang = input.getLanguage();

		//Format names of entities
		String pokemonName = TextUtility.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()));
		String versionName = TextUtility.flexFormToProper(version.getNameInLanguage(lang.getFlexKey()));

		//Check if the Pokemon has a Pokedex entry that meets the user criteria
		Optional<String> entry = species.getFlavorTextEntry(lang.getFlexKey(), version.getName());

		if(!entry.isPresent())
		{
			String msg = DexField.getNoEntryMessage("**__"+pokemonName+"__**","**__"+versionName+"__**", lang);
			response.addToReply(msg);
			return response;
		}

		//Format reply
		String replyContent = DexField.getEntryTitle(pokemonName,
				TextUtility.flexFormToProper(species.getGeneraInLanguage(lang.getFlexKey())), lang ) +": " + TextUtility.formatDexEntry(entry.get());

		response.addToReply("**__"+TextUtility.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+" | " 
				+TextUtility.flexFormToProper(version.getNameInLanguage(lang.getFlexKey()))+"__**");

		builder.setDescription(replyContent);
		builder.setColor(colorService.getColorForVersion(input.getArgument(1).getFlexForm().replace("-", "")));

		//Add thumbnail
		builder.setThumbnail(pokemon.getSprites().getFrontDefault());

		//Add audio reply
		TextToSpeechService tts = (TextToSpeechService)services.getService(ServiceType.TTS);
		Optional<AudioInputStream> audioCheck = tts.convertToAudio(lang, replyContent);
		if(audioCheck.isPresent())
			response.setPlayBack(audioCheck.get());

		response.setEmbed(builder);
		return response;
	}

	private enum DexField
	{
		NO_ENTRY("No entry found for <pokemon> in <version>",
				"No se encontró ninguna entrada para <pokemon> en <version>",
				"Aucune entrée trouvée pour <pokemon> dans <version>",
				"Nessuna voce trovata per <pokemon> in <version>",
				"Kein Eintrag für <pokemon> in <version> gefunden",
				"<version>の<pokemon>のエントリが見つかりません",
				"在<version>中找不到<pokemon>的条目",
				"<version>에 <pokemon> 항목이 없습니다"),

		ENTRY_TITLE("<pokemon>, the <genera>",
				"<pokemon>, el <genera>",
				"<pokemon>, le <genera>",
				"<pokemon>, il <genera>",
				"<pokemon>, das <genera>",
				"<pokemon>、<genera>",
				"<pokemon>，<genera>",
				"<pokemon>, <genera>"),
		;

		private Map<Language, String> titleMap;

		DexField() {}
		DexField(String english, String spanish, String french, String italian, String german, String japanese, String chinese, String korean)
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

		public static String getNoEntryMessage(String pokemon, String version, Language lang)
		{
			return NO_ENTRY.titleMap.get(lang).replace("<pokemon>", pokemon).replace("<version>", version);
		}

		public static String getEntryTitle(String pokemon, String genera, Language lang)
		{
			return ENTRY_TITLE.titleMap.get(lang).replace("<pokemon>", pokemon).replace("<genera>", "\"" + genera + "\"");
		}
	}
}
