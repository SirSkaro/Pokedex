package skaro.pokedex.data_processor.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.CommandService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;

public class CommandsCommand extends PokedexCommand 
{
	public CommandsCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "commands".intern();
		aliases.put("cmds", Language.ENGLISH);
		aliases.put("useage", Language.ENGLISH);
		aliases.put("command", Language.ENGLISH);
		
		this.createHelpMessage("https://i.imgur.com/QAMZRcf.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return false; }
	@Override
	public String getArguments() { return "none"; }

	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COMMAND, ServiceType.COLOR);
	}
	
	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild)
	{ 
		CommandService commands;
		ColorService colorService;
		Response response = new Response();
		EmbedCreateSpec builder = new EmbedCreateSpec();
		
		try
		{
			commands = (CommandService)services.getService(ServiceType.COMMAND);
			colorService = (ColorService)services.getService(ServiceType.COLOR);
			
			response.setPrivate(true);
			response.addToReply("Join the Pokedex Support Server!");
			response.addToReply("https://discord.gg/D5CfFkN");
			
			builder.setColor(colorService.getPokedexColor());
			builder.addField("Prefixes", "!command or %command", true);
			builder.addField("Postfix", "command(input)", true);
			builder.addField("Hints",":small_blue_diamond:Use `%help` for examples.\n"
					+ ":small_blue_diamond:__Don't forget your commas!__\n"
					+ ":small_blue_diamond:You don't need to include '[' or '<' characters.", false);
			
			commands.getAllCommands().forEach(command -> {
				builder.addField(formatCommandFieldTitle(command), formatCommandDescription(command), true);
			});
			
			response.setEmbed(builder);
			return Mono.just(response);
		}
		catch(Exception e)
		{
			return Mono.just(this.createErrorResponse(input, e));
		}
	}
	
	@Override
	protected void createArgumentSpecifications()
	{

	}
	
	private String formatCommandFieldTitle(PokedexCommand command)
	{
		return (":small_orange_diamond:"+ command.getCommandName()).intern();
	}
	
	private String formatCommandDescription(PokedexCommand command)
	{
		return ("%"+command.getCommandName() + " ["+ command.getArguments() + "]").intern();
	}

}