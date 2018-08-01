package skaro.pokedex.data_processor.commands;

import java.awt.Color;

import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommand extends AbstractCommand 
{
	private Response staticDiscordReply;
	
	public HelpCommand()
	{
		super(null);
		commandName = "help".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(new Color(0xD60B01));
		
		builder.appendField("Examples", "[click here for examples of every command]"
				+ "(https://discordbots.org/bot/206147275775279104)", false);
		
		staticDiscordReply.setEmbededReply(builder.build());
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "none"; }
	public Response discordReply(Input input, IUser requester){ return staticDiscordReply; }
}
