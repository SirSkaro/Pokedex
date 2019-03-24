package skaro.pokedex.data_processor.formatters;

import java.io.File;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.IServiceConsumer;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.type.Type;

public class ZMoveResponseFormatter implements ResponseFormatter, IServiceConsumer
{
	private IServiceManager services;
	private final String zMoveClipPath;
	
	public ZMoveResponseFormatter(IServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
		zMoveClipPath = ConfigurationService.getInstance().get().getZMoveClipPath();
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
				response.addToReply("You must specify exactly one Type or Z-Move as input for this command.".intern());
			break;
			case INVALID_ARGUMENT:
				response.addToReply("\""+input.getArgument(0).getRawInput() +"\" is not a recognized Type or Z-Move in "+input.getLanguage().getName());
			break;
			default:
				response.addToReply("A technical error occured");
		}
		
		return response;
	}

	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder)
	{
		Response response = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		
		Language lang = input.getLanguage();
		Move move = (Move)data.get(Move.class.getName()).get(0);
		Type type = (Type)data.getValue(Type.class.getName(), 0);
		
		response.addToReply("**__"+TextUtility.flexFormToProper(move.getNameInLanguage(lang.getFlexKey()))+"__**");
		
		//Upload local file
		String path = zMoveClipPath + "/" + move.getName().replace("--physical", "") + ".mp4";
		File image = new File(path);
		response.addImage(image);
		
		builder.setColor(colorService.getColorForType(move.getType().getName()));
		response.setEmbed(builder);
		return response;
	}

}
