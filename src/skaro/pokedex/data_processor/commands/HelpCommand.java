package skaro.pokedex.data_processor.commands;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.input_processor.arguments.NoneArgument;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.CommandService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;

public class HelpCommand extends PokedexCommand 
{
	Response defaultResponse;
	
	public HelpCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "help".intern();
		orderedArgumentCategories.add(ArgumentCategory.ANY_NONE);
		expectedArgRange = new ArgumentRange(0,1);
		defaultResponse = new Response();
		
		EmbedCreateSpec builder = new EmbedCreateSpec();	
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		builder.setColor(colorService.getPokedexColor());
		
		builder.addField("Examples", "[click here for examples of every command]"
				+ "(https://discordbots.org/bot/pokedex)", false);
		
		defaultResponse.setEmbed(builder);
		this.createHelpMessage("https://cdn.bulbagarden.net/upload/c/ce/Helping_Hand_IV.png");
	}
	
	@Override
	public boolean makesWebRequest() { return false; }
	@Override
	public String getArguments() { return "<command> or none"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COMMAND, ServiceType.COLOR);
	}
	
	@Override
	public Mono<Response> discordReply(Input input, User requester)
	{ 
		if(input.getArg(0) instanceof NoneArgument)
			return Mono.just(defaultResponse);
		
		String arg = input.getArg(0).getDbForm();
		Response reply = new Response();
		CommandService commands;
		PokedexCommand command;
		
		try
		{
			commands = (CommandService)services.getService(ServiceType.COMMAND);
			
			if(!commands.hasCommand(arg))
			{
				//reply.addToReply("\""+arg +"\" is not a supported command!");
				return Mono.empty();
			}

			command = commands.get(arg);
			reply.addToReply("__**"+TextFormatter.flexFormToProper(command.getCommandName())+" Command**__");
			reply.setEmbed(command.getHelpMessage());
			return Mono.just(reply);
		}
		catch(Exception e)
		{
			return Mono.just(this.createErrorResponse(input, e));
		}
	}
	
}
