package skaro.pokedex.data_processor.commands;

import java.util.Optional;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;

public class AboutCommand extends PokedexCommand 
{
	private Response staticDiscordReply;
	
	public AboutCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		Optional<ConfigurationService> configurator = ConfigurationService.getInstance();
		String version;
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		
		if(!configurator.isPresent())
			version = "(unspecified)";
		else
			version = configurator.get().getVersion();
		
		commandName = "about".intern();
		orderedArgumentCategories.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		
		staticDiscordReply = new Response();
		
		EmbedCreateSpec builder = new EmbedCreateSpec();	
		builder.setColor(colorService.getPokedexColor());
		builder.setAuthor("Pokedex "+version, null, null);
		setStaticReplyFields(builder);
		
		staticDiscordReply.setEmbed(builder);
		
		this.createHelpMessage("https://i.imgur.com/gC3tMJQ.gif");
	}
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{ 
		return super.hasExpectedServices(services) &&
					services.hasServices(ServiceType.COLOR, ServiceType.CONFIG); 
	}
	
	@Override
	public boolean makesWebRequest() { return false; }
	@Override
	public String getArguments() { return "none"; }
	@Override
	public Mono<Response> discordReply(Input input, User requester) 
	{ return Mono.just(staticDiscordReply); }
	
	private void setStaticReplyFields(EmbedCreateSpec builder)
	{
		builder.addField("Creator", "[Benjamin \"Sir Skaro\" Churchill](https://twitter.com/sirskaro)", true);
		builder.addField("Icon Artist", "[Domenic \"Jabberjock\" Serena](https://twitter.com/domenicserena)", true);
		builder.addField("License","[Attribution-NonCommercial-NoDerivatives 4.0 International](https://creativecommons.org/licenses/by-nc-nd/4.0/)",true);
		builder.addField("Recognitions", "Data provided by PokeAPI and Pokemon Showdown", true);
		builder.addField("Github", "[Pokedex is open source!](https://github.com/SirSkaro/Pokedex)", true);
		builder.addField("Libraries/Services", "Discord4J, MaryTTS, MySQL, Caffine, Bucket4J, Jazzy, PokeAPI", false);
		builder.addField("Pledge on Patron!", "[Support Pokedex and get perks!](https://www.patreon.com/sirskaro)", true);
		builder.addField("Special Thanks", "PokeaimMD, Honko, the Pokemon Showdown Dev Team, "
				+ "and the Bulbapedia Community", false);
		builder.setFooter("Pokémon © 2002-2018 Pokémon. © 1995-2018 Nintendo/Creatures Inc./GAME FREAK inc. TM, ® and Pokémon character names are trademarks of Nintendo. " + 
				"No copyright or trademark infringement is intended.", null);
		
		builder.setThumbnail("https://i.creativecommons.org/l/by-nc-nd/4.0/88x31.png");
	}
}
