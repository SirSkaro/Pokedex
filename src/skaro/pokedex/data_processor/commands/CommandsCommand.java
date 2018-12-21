package skaro.pokedex.data_processor.commands;

import java.util.List;

import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class CommandsCommand extends AbstractCommand 
{
	private Response staticDiscordReply;
	
	public CommandsCommand(List<AbstractCommand> commands)
	{
		super();
		commandName = "commands".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		aliases.put("cmds", Language.ENGLISH);
		aliases.put("useage", Language.ENGLISH);
		aliases.put("command", Language.ENGLISH);
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		staticDiscordReply.setPrivate(true);
		staticDiscordReply.addToReply("Join the Pokedex Support Server!");
		staticDiscordReply.addToReply("https://discord.gg/D5CfFkN");
		builder.withColor(0xD60B01);
		setStaticReplyFields(builder, commands);
		
		staticDiscordReply.setEmbededReply(builder.build());
		this.createHelpMessage("https://i.imgur.com/QAMZRcf.gif");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "none"; }
	public Response discordReply(Input input, IUser requester) { return staticDiscordReply; }

	private void setStaticReplyFields(EmbedBuilder builder, List<AbstractCommand> commands)
	{
		builder.appendField("Prefixes", "!command or %command", true);
		builder.appendField("Postfix", "command(input)", true);
		builder.appendField("Hints",":small_blue_diamond:Use `%help` for examples.\n"
				+ ":small_blue_diamond:__Don't forget your commas!__\n"
				+ ":small_blue_diamond:You don't need to include '[' or '<' characters.", false);
		
		for(AbstractCommand command : commands)
			builder.appendField(":small_orange_diamond:"+command.getCommandName(), ("%"+command.getCommandName() + " ["+ command.getArguments()).intern() + "]", true);
	}
}