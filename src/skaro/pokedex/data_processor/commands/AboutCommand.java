package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.Optional;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;

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
		
		staticDiscordReply.addToReply("**Pokedex version "+version+"*");
		staticDiscordReply.addToReply("\tCreated by Benjamin \"Sir Skaro\" Churchill");
		staticDiscordReply.addToReply("\t\thttps://twitter.com/sirskaro");
		staticDiscordReply.addToReply(""); //line break
		staticDiscordReply.addToReply("\tIcon art by Domenic \"Jabberjock\" Serena");
		staticDiscordReply.addToReply("\t\thttps://twitter.com/domenicserena");
		staticDiscordReply.addToReply(""); //line break
		staticDiscordReply.addToReply("\tUtilizes Discord4j, MaryTTS, MySQL, EHCache, and jSpellCorrect");
		staticDiscordReply.addToReply(""); //line break
		staticDiscordReply.addToReply("\tWant this bot in your own Discord servers?");
		staticDiscordReply.addToReply("\t\thttps://goo.gl/Mm5pU7");
		staticDiscordReply.addToReply(""); //line break
		staticDiscordReply.addToReply("Also available on Twitch! Contact me for more information.");
		staticDiscordReply.addToReply(""); //line break
		staticDiscordReply.addToReply("\tSpecial thanks");
		staticDiscordReply.addToReply("\t\tPokeaimMD, Honko, the Pokemon Showdown Dev Team, "
				+ "and the Bulbapedia Community");
		staticDiscordReply.addToReply(""); //line break
		staticDiscordReply.addToReply("\tIf you like the bot, please consider donating!");
		staticDiscordReply.addToReply("\t\thttps://goo.gl/HlrFrD");
		
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
