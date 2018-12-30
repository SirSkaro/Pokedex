package skaro.pokedex.data_processor.formatters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sound.sampled.AudioInputStream;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.ColorService;
import skaro.pokedex.core.IServiceConsumer;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.core.TTSConverter;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import skaro.pokeflex.objects.version.Version;
import sx.blah.discord.util.EmbedBuilder;

public class DexResponseFormatter implements IDiscordFormatter, IServiceConsumer
{
	private IServiceManager services;
	
	public DexResponseFormatter(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR, ServiceType.TTS);
	}

	@Override
	public Response invalidInputResponse(Input input) 
	{
		Response response = new Response();
		
		switch(input.getError())
		{
			case ARGUMENT_NUMBER:
				response.addToReply("You must specify a Pokemon and a Version as input for this command "
						+ "(seperated by commas).");
			break;
			case INVALID_ARGUMENT:
				response.addToReply("Could not process your request due to the following problem(s):".intern());
				for(AbstractArgument arg : input.getArgs())
					if(!arg.isValid())
						response.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
			break;
			default:
				response.addToReply("A technical error occured (code 110)");
		}
		
		return response;
	}
	
	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		Version version = (Version)data.getValue(Version.class.getName(), 0);
		Language lang = input.getLanguage();
		Optional<AudioInputStream> audioCheck;
		TTSConverter tts;
		
		//Format names of entities
		String pokemonName = TextFormatter.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()));
		String versionName = TextFormatter.flexFormToProper(version.getNameInLanguage(lang.getFlexKey()));
		
		//Check if the Pokemon has a Pokedex entry that meets the user criteria
		Optional<String> entry = species.getFlavorTextEntry(lang.getFlexKey(), version.getName());
		
		if(!entry.isPresent())
		{
			String msg = DexField.getNoEntryMessage("**__"+pokemonName+"__**","**__"+versionName+"__**", lang);
			response.addToReply(msg);
			return response;
		}
		
		//Format reply
		builder.setLenient(true);
		String replyContent = DexField.getEntryTitle(pokemonName,
				TextFormatter.flexFormToProper(species.getGeneraInLanguage(lang.getFlexKey())), lang ) +": " + TextFormatter.formatDexEntry(entry.get());
		
		response.addToReply("**__"+TextFormatter.flexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+" | " 
				+TextFormatter.flexFormToProper(version.getNameInLanguage(lang.getFlexKey()))+"__**");
		
		builder.withDescription(replyContent);
		builder.withColor(colorService.getColorForVersion(input.getArg(1).getFlexForm().replace("-", "")));
		
		//Add thumbnail
		builder.withThumbnail(pokemon.getSprites().getFrontDefault());
		
		//Add audio reply
		tts = (TTSConverter)services.getService(ServiceType.TTS);
		audioCheck = tts.convertToAudio(lang, replyContent);
		if(audioCheck.isPresent())
			response.setPlayBack(audioCheck.get());
		
		response.setEmbededReply(builder.build());
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
