package skaro.pokedex.data_processor.formatters;

import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeEfficacyWrapper;
import skaro.pokedex.data_processor.TypeEfficacyWrapper.Efficacy;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.EmojiService;
import skaro.pokedex.services.IServiceConsumer;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.type.Type;

public class CoverageResponseFormatter implements IDiscordFormatter, IServiceConsumer
{
	private IServiceManager services;
	
	public CoverageResponseFormatter(IServiceManager services) throws ServiceConsumerException
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
				response.addToReply("You must specify between 1 to 4 Types or Moves as input for this command "
						+ "(seperated by commas).");
			break;
			case INVALID_ARGUMENT:
				response.addToReply("Could not process your request due to the following problem(s):".intern());
				for(AbstractArgument arg : input.getArgs())
					if(!arg.isValid())
						response.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized Type or Move in "+ input.getLanguage().getName());
				response.addToReply("\n*top suggestion*: did you include commas between inputs?");
			break;
			default:
				response.addToReply("A technical error occured (code 107)");
		}
		
		return response;
	}

	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder) 
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		TypeEfficacyWrapper wrapper = (TypeEfficacyWrapper)data.getValue(TypeEfficacyWrapper.class.getName(), 0);
		Response response = new Response();
		
		response.addToReply(createHeader(wrapper.getTypes(), lang));
		
		String effectiveList = efficacyListToString(wrapper.getInteraction(Efficacy.EFFECTIVE), lang);
		String neutralList = efficacyListToString(wrapper.getInteraction(Efficacy.NEUTRAL), lang);
		String resistList = efficacyListToString(wrapper.getInteraction(Efficacy.RESIST), lang);
		String immuneList = efficacyListToString(wrapper.getInteraction(Efficacy.IMMUNE), lang);
		
		if(!effectiveList.isEmpty())
			builder.addField(CommonData.SUPER_EFFECTIVE.getInLanguage(lang), effectiveList, false);
		if(!neutralList.isEmpty())
			builder.addField(CommonData.NEUTRAL.getInLanguage(lang), neutralList, false);
		if(!resistList.isEmpty())
			builder.addField(CommonData.RESIST.getInLanguage(lang), resistList, false);
		if(!immuneList.isEmpty())
			builder.addField(CommonData.IMMUNE.getInLanguage(lang), immuneList, false);
		
		builder.setColor(colorService.getColorForWrapper(wrapper));
		response.setEmbed(builder);
		return response;
	}
	
	private String createHeader(List<Type> types, Language lang)
	{
		StringBuilder builder = new StringBuilder();
		EmojiService emojiService = (EmojiService)services.getService(ServiceType.EMOJI);
		
		builder.append("**__");
		
		for(Type type : types)
		{
			builder.append(emojiService.getTypeEmoji(type.getName()));
			builder.append(type.getNameInLanguage(lang.getFlexKey()));
			builder.append("/");
		}
		
		return builder.substring(0, builder.length() - 1) + "__**";
	}
	
	private String efficacyListToString(List<Type> types, Language lang)
	{
		if(types.isEmpty())
			return "";
		
		StringBuilder builder = new StringBuilder();
		types.forEach(type -> {
			builder.append(TextFormatter.flexFormToProper(type.getNameInLanguage(lang.getFlexKey())));
			builder.append(", ");
			});
		
		return builder.substring(0, builder.length() - 2);
	}
	
}
