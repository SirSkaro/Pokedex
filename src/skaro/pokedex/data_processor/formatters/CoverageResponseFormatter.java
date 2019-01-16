package skaro.pokedex.data_processor.formatters;

import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.core.ColorService;
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
		return services.hasServices(ServiceType.COLOR);
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

	@SuppressWarnings("unchecked")
	@Override
	public Response format(Input input, MultiMap<Object> data, EmbedCreateSpec builder) 
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		List<TypeService> typeList = (List<TypeService>)(List<?>)data.get(TypeService.class.getName());
		TypeEfficacyWrapper wrapper = TypeTracker.onOffense(typeList);
		Response response = new Response();
		
		//Header
		response.addToReply("**__"+wrapper.typesToString(lang)+"__**");
		
		builder.addField(CommonData.SUPER_EFFECTIVE.getInLanguage(lang), getList(wrapper, 2.0, lang), false);
		builder.addField(CommonData.NEUTRAL.getInLanguage(lang), getList(wrapper, 1.0, lang), false);
		builder.addField(CommonData.RESIST.getInLanguage(lang), getList(wrapper, 0.5, lang), false);
		builder.addField(CommonData.IMMUNE.getInLanguage(lang), getList(wrapper, 0.0, lang), false);
		builder.setColor(colorService.getColorForWrapper(wrapper));
		
		response.setEmbed(builder);
		return response;
	}
	
	private String getList(TypeEfficacyWrapper wrapper, double mult, Language lang)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult, lang);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
	
}
