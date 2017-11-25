package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.Optional;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class AboutCommand implements ICommand 
{
	private static AboutCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static Response staticDiscordReply, staticTwitchReply;
	
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
		expectedArgRange = new Integer[]{0,0};
		staticDiscordReply = new Response();
		staticTwitchReply = new Response();
		
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
		
		staticTwitchReply.addToReply("*Pokedex version "+version+"*");
		staticTwitchReply.addToReply("Created by Benjamin \"Sir Skaro\" Churchill");
		staticTwitchReply.addToReply("\thttps://twitter.com/sirskaro");
		staticTwitchReply.addToReply("Availlable in Discord! https://goo.gl/Mm5pU7");
		staticTwitchReply.addToReply("Contact me if you want this bot in your own Twitch chat.");
		staticTwitchReply.addToReply("\tIf you like the bot, please consider donating!");
		staticTwitchReply.addToReply("\t\thttps://goo.gl/HlrFrD");
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new AboutCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
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
	
	public Response twitchReply(Input input)
	{ 
		return staticTwitchReply;
	}
}
