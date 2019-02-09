package skaro.pokedex.data_processor.formatters;

import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeEfficacyWrapper;
import skaro.pokedex.data_processor.TypeEfficacyWrapper.Efficacy;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.EmojiService;
import skaro.pokedex.services.IServiceConsumer;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import skaro.pokeflex.objects.type.Type;

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
				for(CommandArgument arg : input.getArgs())
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
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder) 
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		PokemonSpecies species = (PokemonSpecies)data.getValue(PokemonSpecies.class.getName(), 0);
		TypeEfficacyWrapper wrapper = (TypeEfficacyWrapper)data.getValue(TypeEfficacyWrapper.class.getName(), 0);
		Response response = new Response();
		
		//Add model and header depending on if the user specified a Pokemon
		if(pokemon != null && species != null)
		{
			builder.setThumbnail(pokemon.getSprites().getFrontDefault());
			response.addToReply(formatHeader(species, wrapper, lang));
		}
		else
			response.addToReply(formatHeader(wrapper, lang));
		
		//Format body
		String weakList = combineEfficaciesToString(wrapper, Efficacy.EFFECTIVE, Efficacy.QUAD_EFFECTIVE, lang);
		String neutralList = efficacyListToString(wrapper.getInteraction(Efficacy.NEUTRAL), "", lang);
		String resistList = combineEfficaciesToString(wrapper, Efficacy.RESIST, Efficacy.QUAD_RESIST, lang);
		String immuneList = efficacyListToString(wrapper.getInteraction(Efficacy.IMMUNE), "", lang);
		
		if(!weakList.isEmpty())
			builder.addField(CommonData.WEAK.getInLanguage(lang), weakList, false);
		if(!neutralList.isEmpty())
			builder.addField(CommonData.NEUTRAL.getInLanguage(lang), neutralList, false);
		if(!resistList.isEmpty())
			builder.addField(CommonData.RESIST.getInLanguage(lang), resistList, false);
		if(!immuneList.isEmpty())
			builder.addField(CommonData.IMMUNE.getInLanguage(lang), immuneList, false);
		
		//Set color
		builder.setColor(colorService.getColorForWrapper(wrapper));
		
		response.setEmbed(builder);
		return response;
	}
	
	private String formatHeader(PokemonSpecies species, TypeEfficacyWrapper wrapper, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		List<Type> types = wrapper.getTypes();
		Type type = types.get(0);
		
		builder.append("**__"+TextFormatter.pokemonFlexFormToProper(species.getNameInLanguage(lang.getFlexKey()))+" ");
		builder.append("("+ emojiService.getTypeEmoji(type.getName()) + type.getNameInLanguage(lang.getFlexKey()));
		
		if(types.size() > 1)
		{
			type = types.get(1);
			builder.append("/"+emojiService.getTypeEmoji(type.getName()) + type.getNameInLanguage(lang.getFlexKey()));
		}
		builder.append(")__**");
		
		return builder.toString();
	}
	
	private String formatHeader(TypeEfficacyWrapper wrapper, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		List<Type> types = wrapper.getTypes();
		Type type = types.get(0);
		
		builder.append("**__" + emojiService.getTypeEmoji(type.getName()) + type.getNameInLanguage(lang.getFlexKey()));
		if(types.size() > 1)
		{
			type = types.get(1);
			builder.append("/"+ emojiService.getTypeEmoji(type.getName()) + type.getNameInLanguage(lang.getFlexKey()));
		}
		builder.append("__**");
		
		return builder.toString();
	}
	
	private String combineEfficaciesToString(TypeEfficacyWrapper wrapper, Efficacy efficacy1, Efficacy efficacy2, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		List<Type> typeList1 = wrapper.getInteraction(efficacy1);
		List<Type> typeList2 = wrapper.getInteraction(efficacy2);
		String efficacy1List = efficacyListToString(typeList1, "", lang);
		String efficacy2List = efficacyListToString(typeList2, "**", lang);
		
		builder.append(efficacy1List);
		if(!efficacy1List.isEmpty() && !efficacy2List.isEmpty())
			builder.append(", ");
		builder.append(efficacy2List);
		
		return builder.toString();
	}
	
	private String efficacyListToString(List<Type> types, String markUp, Language lang)
	{
		if(types.isEmpty())
			return "";
		
		StringBuilder builder = new StringBuilder();
		types.forEach(type -> {
			builder.append(markUp);
			builder.append(TextFormatter.flexFormToProper(type.getNameInLanguage(lang.getFlexKey())));
			builder.append(markUp);
			builder.append(", ");
			});
		
		return builder.substring(0, builder.length() - 2);
	}
	
}
