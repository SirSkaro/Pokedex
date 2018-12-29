package skaro.pokedex.data_processor.commands;

import skaro.pokedex.core.ColorService;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.CommandService;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class CommandsCommand extends AbstractCommand 
{
	public CommandsCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "commands".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		aliases.put("cmds", Language.ENGLISH);
		aliases.put("useage", Language.ENGLISH);
		aliases.put("command", Language.ENGLISH);
		
		this.createHelpMessage("https://i.imgur.com/QAMZRcf.gif");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "none"; }

	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.COMMAND, ServiceType.COLOR);
	}
	
	public Response discordReply(Input input, IUser requester) 
	{ 
		CommandService commands;
		ColorService colorService;
		Response response = new Response();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		try
		{
			commands = (CommandService)services.getService(ServiceType.COMMAND);
			colorService = (ColorService)services.getService(ServiceType.COLOR);
			
			response.setPrivate(true);
			response.addToReply("Join the Pokedex Support Server!");
			response.addToReply("https://discord.gg/D5CfFkN");
			
			builder.withColor(colorService.getPokedexColor());
			builder.appendField("Prefixes", "!command or %command", true);
			builder.appendField("Postfix", "command(input)", true);
			builder.appendField("Hints",":small_blue_diamond:Use `%help` for examples.\n"
					+ ":small_blue_diamond:__Don't forget your commas!__\n"
					+ ":small_blue_diamond:You don't need to include '[' or '<' characters.", false);
			
			commands.getCacheAsMap().forEach((commandName, command) -> {
				builder.appendField(":small_orange_diamond:"+ commandName, ("%"+commandName + " ["+ command.getArguments()).intern() + "]", true);
			});
			
			response.setEmbededReply(builder.build());
			return response;
		}
		catch(Exception e)
		{
			Response errorResponse = new Response();
			this.addErrorMessage(errorResponse, input, "1013", e); 
			return errorResponse;
		}
	}

}