package skaro.pokedex.data_processor.commands;

import skaro.pokedex.core.ColorService;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.CommandService;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.input_processor.arguments.NoneArgument;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommand extends AbstractCommand 
{
	Response defaultResponse;
	
	public HelpCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "help".intern();
		argCats.add(ArgumentCategory.ANY_NONE);
		expectedArgRange = new ArgumentRange(0,1);
		defaultResponse = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		builder.setLenient(true);
		builder.withColor(colorService.getPokedexColor());
		
		builder.appendField("Examples", "[click here for examples of every command]"
				+ "(https://discordbots.org/bot/pokedex)", false);
		
		defaultResponse.setEmbededReply(builder.build());
		this.createHelpMessage("https://cdn.bulbagarden.net/upload/c/ce/Helping_Hand_IV.png");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "<command> or none"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COMMAND, ServiceType.COLOR);
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		if(input.getArg(0) instanceof NoneArgument)
			return defaultResponse;
		
		String arg = input.getArg(0).getDbForm();
		Response reply = new Response();
		CommandService commands;
		AbstractCommand command;
		
		try
		{
			commands = (CommandService)services.getService(ServiceType.COMMAND);
			
			if(!commands.hasCommand(arg))
			{
				//reply.addToReply("\""+arg +"\" is not a supported command!");
				return reply;
			}

			command = commands.get(arg);
			reply.addToReply("__**"+TextFormatter.flexFormToProper(command.getCommandName())+" Command**__");
			reply.setEmbededReply(command.getHelpMessage());
			return reply;
		}
		catch(Exception e)
		{
			Response errorResponse = new Response();
			this.addErrorMessage(errorResponse, input, "1014", e); 
			return errorResponse;
		}
	}
	
}
