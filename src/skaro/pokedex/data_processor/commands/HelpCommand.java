package skaro.pokedex.data_processor.commands;

import java.awt.Color;
import java.util.HashMap;

import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.input_processor.arguments.NoneArgument;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommand extends AbstractCommand 
{
	private Response staticDiscordReply;
	private HashMap<String, AbstractCommand> library;
	
	public HelpCommand(HashMap<String, AbstractCommand> lib)
	{
		super(null, null);
		commandName = "help".intern();
		argCats.add(ArgumentCategory.ANY_NONE);
		expectedArgRange = new ArgumentRange(0,1);
		staticDiscordReply = new Response();
		library = lib;
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(new Color(0xD60B01));
		
		builder.appendField("Examples", "[click here for examples of every command]"
				+ "(https://discordbots.org/bot/pokedex)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
		this.createHelpMessage("https://cdn.bulbagarden.net/upload/c/ce/Helping_Hand_IV.png");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "<command> or none"; }
	public Response discordReply(Input input, IUser requester)
	{ 
		if(input.getArg(0) instanceof NoneArgument)
			return staticDiscordReply;
		
		String arg = input.getArg(0).getDbForm();
		Response reply = new Response();
		AbstractCommand command;
		
		if(!library.containsKey(arg))
		{
			reply.addToReply("\""+arg +"\" is not a supported command!");
			return reply;
		}

		command = library.get(arg);
		reply.addToReply("__**"+TextFormatter.flexFormToProper(command.getCommandName())+" Command**__");
		reply.setEmbededReply(command.getHelpMessage());
		
		return reply;
	}
}
