package skaro.pokedex.data_processor.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.ArgumentSpec;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.AnyArgument;
import skaro.pokedex.input_processor.arguments.NoneArgument;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.CommandService;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;

public class HelpCommand extends PokedexCommand 
{
	Response defaultResponse;
	
	public HelpCommand(PokedexServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "help".intern();
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
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COMMAND, ServiceType.COLOR);
	}
	
	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild)
	{ 
		if(input.getArgument(0) instanceof NoneArgument)
			return Mono.just(defaultResponse);
		
		String arg = input.getArgument(0).getDbForm();
		Response reply = new Response();
		CommandService commands;
		PokedexCommand command;
		
		try
		{
			commands = (CommandService)services.getService(ServiceType.COMMAND);
			
			if(!commands.commandOrAliasExists(arg))
			{
				//reply.addToReply("\""+arg +"\" is not a supported command!");
				return Mono.empty();
			}

			command = commands.getByAnyAlias(arg);
			reply.addToReply("__**"+TextUtility.flexFormToProper(command.getCommandName())+" Command**__");
			reply.setEmbed(command.getHelpMessage());
			return Mono.just(reply);
		}
		catch(Exception e)
		{
			return Mono.just(this.createErrorResponse(input, e));
		}
	}
	
	@Override
	protected void createArgumentSpecifications()
	{
		argumentSpecifications.add(new ArgumentSpec(true, AnyArgument.class));
	}
	
}
