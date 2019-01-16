package skaro.pokedex.data_processor.formatters;

import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.core.ColorService;
import skaro.pokedex.core.EmojiService;
import skaro.pokedex.core.IServiceConsumer;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeService;
import skaro.pokedex.data_processor.TypeEfficacyWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class WeakResponseFormatter implements IDiscordFormatter, IServiceConsumer 
{
	private IServiceManager services;
	
	public WeakResponseFormatter(IServiceManager services) throws ServiceConsumerException
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
				response.addToReply("You must specify 1 Pokemon or between 1 and 2 Types (seperated by commas) "
						+ "as input for this command.");
			break;
			case INVALID_ARGUMENT:
				response.addToReply("Could not process your request due to the following problem(s):".intern());
				for(AbstractArgument arg : input.getArgs())
					if(!arg.isValid())
						response.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
				response.addToReply("\n*top suggestion*: did you include commas between inputs?");
			break;
			default:
				response.addToReply("A technical error occured (code 106)");
		}
		
		return response;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Response format(Input input, MultiMap<Object> data, EmbedCreateSpec builder) 
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		List<TypeService> typeList = (List<TypeService>)(List<?>)data.get(TypeService.class.getName());
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		Response response = new Response();
		
		TypeService type1 = typeList.get(0);
		TypeService type2 = typeList.size() > 1 ? typeList.get(1) : null;
		TypeEfficacyWrapper wrapper = TypeTracker.onDefense(type1, type2);
		
		//Add model and header depending on if the user specified a Pokemon
		if(pokemon != null && species != null)
		{
			builder.setThumbnail(pokemon.getSprites().getFrontDefault());
			response.addToReply(formatHeader(species, type1, type2, lang));
		}
		else
			response.addToReply(formatHeader(type1, type2, lang));
		
		//Format body
		builder.addField(CommonData.WEAK.getInLanguage(lang), combineLists(wrapper, lang, 2.0, 4.0), false);
		builder.addField(CommonData.NEUTRAL.getInLanguage(lang), getList(wrapper, lang, 1.0), false);
		builder.addField(CommonData.RESIST.getInLanguage(lang), combineLists(wrapper, lang, 0.5, 0.25), false);
		builder.addField(CommonData.IMMUNE.getInLanguage(lang), getList(wrapper, lang, 0.0), false);
		
		//Set color
		builder.setColor(colorService.getColorForWrapper(wrapper));
		
		response.setEmbed(builder);
		return response;
	}
	
	private String formatHeader(PokemonSpecies species, TypeService type1, TypeService type2, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		builder.append("**__"+TextFormatter.pokemonFlexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+" ");
		builder.append("("+ emojiService.getTypeEmoji(type1) + type1.getNameInLanguage(lang));
		builder.append(type2 != null 
				? ("/"+emojiService.getTypeEmoji(type2) + type2.getNameInLanguage(lang) +")__**") 
				: ")__**");
		
		return builder.toString();
	}
	
	private String formatHeader(TypeService type1, TypeService type2, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		builder.append("**__" + emojiService.getTypeEmoji(type1) + type1.getNameInLanguage(lang));
		builder.append(type2 != null ? "/"+ emojiService.getTypeEmoji(type2) + type2.getNameInLanguage(lang) +"__**": "__**");
		
		return builder.toString();
	}
	
	private String combineLists(TypeEfficacyWrapper wrapper, Language lang, double mult1, double mult2)
	{
		Optional<String> strCheck;
		String inter1, intern2;
		StringBuilder builder = new StringBuilder();
		
		strCheck = wrapper.interactionToString(mult1, lang);
		inter1 = strCheck.isPresent() ? strCheck.get() : null;
		
		strCheck = wrapper.interactionToString(mult2, lang);
		intern2 = strCheck.isPresent() ? strCheck.get() : null;
		
		if(inter1 == null && intern2 == null)
			return null;
		
		if(inter1 != null)
			builder.append(inter1);
		
		if(inter1 != null && intern2 != null)
			builder.append(", **"+intern2+"**");
		else if(intern2 != null)
			builder.append("**"+intern2+"**");
		
		return builder.toString();
	}
	
	private String getList(TypeEfficacyWrapper wrapper, Language lang, double mult)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult, lang);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
	
	
}
