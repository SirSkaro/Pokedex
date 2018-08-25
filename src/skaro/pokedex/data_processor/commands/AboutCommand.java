package skaro.pokedex.data_processor.commands;

import java.util.Optional;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class AboutCommand extends AbstractCommand 
{
	private Response staticDiscordReply;
	
	public AboutCommand()
	{
		super(null);
		
		Optional<Configurator> configurator = Configurator.getInstance();
		String version;
		
		if(!configurator.isPresent())
			version = "(unspecified)";
		else
			version = configurator.get().getVersion();
		
		commandName = "about".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		
		staticDiscordReply = new Response();
		
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		builder.withColor(0xD60B01);
		builder.withAuthorName("Pokedex "+version);
		setStaticReplyFields(builder);
		
		staticDiscordReply.setEmbededReply(builder.build());
		
		this.createHelpMessage("https://i.imgur.com/gC3tMJQ.gif");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "none"; }
	public Response discordReply(Input input, IUser requester) { return staticDiscordReply; }
	
	private void setStaticReplyFields(EmbedBuilder builder)
	{
		builder.appendField("Creator", "[Benjamin \"Sir Skaro\" Churchill](https://twitter.com/sirskaro)", true);
		builder.appendField("Icon Artist", "[Domenic \"Jabberjock\" Serena](https://twitter.com/domenicserena)", true);
		builder.appendField("License","[Attribution-NonCommercial-NoDerivatives 4.0 International](https://creativecommons.org/licenses/by-nc-nd/4.0/)",true);
		builder.appendField("Recognitions", "Data provided by PokeAPI and Pokemon Showdown", true);
		builder.appendField("Github", "[Pokedex is open source!](https://github.com/SirSkaro/Pokedex)", true);
		builder.appendField("Libraries/Services", "Discord4J, MaryTTS, MySQL, EHCache, Jazzy, PokeAPI", false);
		builder.appendField("Pledge on Patron!", "[Support Pokedex and get perks!](https://www.patreon.com/sirskaro)", true);
		builder.appendField("Special Thanks", "PokeaimMD, Honko, the Pokemon Showdown Dev Team, "
				+ "and the Bulbapedia Community", false);
		builder.withFooterText("Pokémon © 2002-2018 Pokémon. © 1995-2018 Nintendo/Creatures Inc./GAME FREAK inc. TM, ® and Pokémon character names are trademarks of Nintendo. " + 
				"No copyright or trademark infringement is intended.");
		
		builder.withThumbnail("https://i.creativecommons.org/l/by-nc-nd/4.0/88x31.png");
	}
}
