package skaro.pokedex.data_processor.formatters;

import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.ColorService;
import skaro.pokedex.core.IServiceConsumer;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.TypeInteractionWrapper;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import sx.blah.discord.util.EmbedBuilder;

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
	public Response format(Input input, MultiMap<Object> data, EmbedBuilder builder) 
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Language lang = input.getLanguage();
		List<TypeData> typeList = (List<TypeData>)(List<?>)data.get(TypeData.class.getName());
		TypeInteractionWrapper wrapper = TypeTracker.onOffense(typeList);
		Response response = new Response();
		builder.setLenient(true);
		
		//Header
		response.addToReply("**__"+wrapper.typesToString(lang)+"__**");
		
		builder.appendField(CommonData.SUPER_EFFECTIVE.getInLanguage(lang), getList(wrapper, 2.0, lang), false);
		builder.appendField(CommonData.NEUTRAL.getInLanguage(lang), getList(wrapper, 1.0, lang), false);
		builder.appendField(CommonData.RESIST.getInLanguage(lang), getList(wrapper, 0.5, lang), false);
		builder.appendField(CommonData.IMMUNE.getInLanguage(lang), getList(wrapper, 0.0, lang), false);
		builder.withColor(colorService.getColorForWrapper(wrapper));
		
		response.setEmbededReply(builder.build());
		return response;
	}
	
	private String getList(TypeInteractionWrapper wrapper, double mult, Language lang)
	{
		Optional<String> strCheck = wrapper.interactionToString(mult, lang);
		return (strCheck.isPresent() ? strCheck.get() : null);
	}
	
}
