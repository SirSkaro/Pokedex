package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.Optional;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.util.EmbedBuilder;

public class AboutCommand implements ICommand 
{
	private static AboutCommand instance;
	private static ArgumentRange expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply;
	
	private AboutCommand()
	{
		Optional<Configurator> configurator = Configurator.getInstance();
		String version;
		
		if(!configurator.isPresent())
			version = "(unspecified)";
		else
			version = configurator.get().getVersion();
		
		commandName = "about".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(255, 255, 255);
		builder.withAuthorName("Pokedex version "+version);
		builder.appendField("Creator", "Benjamin \"Sir Skaro\" Churchill", true);
		builder.appendField("Twitter","[Follow me on Twitter!](https://twitter.com/sirskaro)",true);
		builder.appendField("Icon Artist", "Domenic \"Jabberjock\" Serena", true);
		builder.appendField("Twitter","[Check out Jabberjock!](https://twitter.com/domenicserena)",true);
		builder.appendField("Libraries", "Discord4j, MaryTTS, MySQL, EHCache, Jazzy", false);
		builder.appendField("Special Thanks", "PokeaimMD, Honko, the Pokemon Showdown Dev Team, "
				+ "and the Bulbapedia Community", false);
		builder.withFooterText("If you like the bot, please consider donating! Use the donate command for a link.");
		
		staticDiscordReply.setEmbededReply(builder.build());
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new AboutCommand();
		return instance;
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "none";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		return true;
	}
	
	public Response discordReply(Input input)
	{ 
		return staticDiscordReply;
	}
}
