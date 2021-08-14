package skaro.pokedex.data_processor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import skaro.pokedex.input_processor.ArgumentSpec;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.PerkService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.objects.pokemon.Pokemon;

public abstract class PokedexCommand implements PokedexServiceConsumer
{
	protected String commandName;
	protected List<ArgumentSpec> argumentSpecifications;
	protected List<String> extraMessages;
	protected Response helpMessage;
	protected Map<String, Language> aliases;
	protected ResponseFormatter formatter;
	protected PokedexServiceManager services;
	protected final String helpGifPath;
	
	public PokedexCommand(PokedexServiceManager serviceManager) {
		services = serviceManager;
		argumentSpecifications = new ArrayList<>();
		aliases = new HashMap<>();
		populateDefaultExtraMessage();
		createArgumentSpecifications();
		helpGifPath = ((ConfigurationService)services.getService(ServiceType.CONFIG)).getHelpGifPath();
	}
	
	public PokedexCommand(PokedexServiceManager serviceManager, ResponseFormatter discordFormatter)
	{
		services = serviceManager;
		formatter = discordFormatter;
		argumentSpecifications = new ArrayList<>();
		aliases = new HashMap<>();
		populateDefaultExtraMessage();
		createArgumentSpecifications();
		helpGifPath = ((ConfigurationService)services.getService(ServiceType.CONFIG)).getHelpGifPath();
	}
	
	public String getCommandName() { return commandName; }
	public List<ArgumentSpec> getArgumentSpecifications() { return argumentSpecifications; }
	public Map<String, Language> getAliases() { return aliases; }
	public List<String> getExtraMessages() { return extraMessages; }
	public Response getHelpMessage() { return helpMessage; }
	
	public Language getLanguageOfAlias(String alias)
	{
		Language result = aliases.get(alias);
		
		if(result == null)
			return Language.ENGLISH;
		
		return result;
	}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR, ServiceType.CONFIG);
	}
	
	public Response createErrorResponse(Input input, Throwable error) {
		Response response = new Response();
		EmbedCreateSpec builder = new EmbedCreateSpec();
		
		response.addToReply("**Error Report** - "+commandName+" command");
		builder.setDescription("Some error occured while processing your request! Please try again later.\n\n"
				+ "If you think this is a bug, please __screenshot this report__ and post it in the Support Server!");
		builder.addField("Technical Error", error.getClass().getSimpleName(), true);
		builder.addField("User Input", input.argsToString(), true);
		builder.addField("Link to Support Server", "[Support Server link](https://discord.gg/D5CfFkN)", true);
		
		response.setEmbed(builder);
		return response;
	}
	
	abstract public boolean makesWebRequest();
	abstract public String getArguments();
	abstract public Mono<Response> respondTo(Input input, User author, Guild guild);
	abstract protected void createArgumentSpecifications();
	
	protected String listToItemizedString(List<?> list) {
		if(list.isEmpty())
			return "None".intern();
		
		StringBuilder result = new StringBuilder();
		int i;
		for(i = 0; i < list.size() - 1; i++) {
			if(i %2 == 0) {
				result.append(list.get(i).toString() + "*/* ");
			}
			else {
				result.append(list.get(i).toString() + "\n");
			}
		}

		result.append(list.get(i).toString());
		
		return result.toString();
	}
	
	protected void addRandomExtraMessage(EmbedCreateSpec builder) {
		int randNum = ThreadLocalRandom.current().nextInt(1, 5); //1 in 4 chance
		if(randNum == 1) {
			randNum = ThreadLocalRandom.current().nextInt(0, extraMessages.size());
			builder.setFooter(extraMessages.get(randNum), null);
		}
	}
	
	protected void createHelpMessage(String ex1, String ex2, String ex3, String ex4) {
		helpMessage = new Response();
		EmbedCreateSpec builder = new EmbedCreateSpec();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		StringBuilder exampleBuilder = new StringBuilder();
		
		exampleBuilder.append("!"+commandName+" "+ex1+"\n");
		exampleBuilder.append("%"+commandName+" "+ex2+"\n");
		exampleBuilder.append(commandName+"("+ex3+")\n");
		exampleBuilder.append("@Pokedex "+commandName+" "+ex4);
		
		builder.addField("Input", this.getArguments(), true);
		this.addAliasFields(builder);
		builder.addField("Examples", exampleBuilder.toString(), true);
		builder.setThumbnail("https://images.discordapp.net/avatars/206147275775279104/e535e65cef619085c66736d8433ade73.png?size=512");
		builder.setColor(colorService.getPokedexColor());
		
		helpMessage.setEmbed(builder);
	}
	
	protected void createHelpMessage() {
		helpMessage = new Response();
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		EmbedCreateSpec builder = new EmbedCreateSpec();
		
		builder.addField("Input", this.getArguments(), true);
		this.addAliasFields(builder);
		builder.setThumbnail("https://images.discordapp.net/avatars/206147275775279104/e535e65cef619085c66736d8433ade73.png?size=512");
		builder.setColor(colorService.getPokedexColor());
		
		helpMessage.setEmbed(builder);
	}
	
	public void addHelpImage(Response response, EmbedCreateSpec builder) {
		try {
			String fileName = commandName + "-command.gif";
			URL url = new URL( helpGifPath+ "/" + fileName);
			response.addImage(fileName, url.openStream());
			builder.setImage("attachment://"+fileName);
		} catch(IOException e) {
			throw Exceptions.propagate(e);
		}
	}
	
	protected void createNonGifHelpMessage(String imageURL) {
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		EmbedCreateSpec builder = new EmbedCreateSpec();
		
		builder.addField("Input", this.getArguments(), true);
		this.addAliasFields(builder);
		builder.setImage(imageURL);
		builder.setThumbnail("https://images.discordapp.net/avatars/206147275775279104/e535e65cef619085c66736d8433ade73.png?size=512");
		
		builder.setColor(colorService.getPokedexColor());
		
		helpMessage = new Response();
		helpMessage.addToReply("__**"+TextUtility.flexFormToProper(commandName)+" Command**__");
		helpMessage.setEmbed(builder);
	}
	
	protected Mono<Pokemon> addAdopter(Pokemon pokemon, EmbedCreateSpec builder) {
		PerkService checker = (PerkService)services.getService(ServiceType.PERK);
		return Mono.defer(() -> checker.getPokemonsAdopterIfPledged(pokemon.getName()))
				.doOnNext(user -> builder.setAuthor(user.getUsername() + "'s "+ TextUtility.flexFormToProper(pokemon.getName()), null, getPatreonLogo()))
				.then(Mono.just(pokemon));
	}

	protected String getPatreonLogo() {
		return "https://c5.patreon.com/external/logo/downloads_logomark_color_on_coral.png".intern();
	}
	
	protected String getPatreonBanner() {
		return "https://c5.patreon.com/external/logo/become_a_patron_button.png".intern();
	}
	
	private void populateDefaultExtraMessage() {
		extraMessages = new ArrayList<>();
		
		extraMessages.add("If you like Pokedex, consider becoming a Patreon for perks! (%patreon for link)");
		extraMessages.add("Stay up to date with Pokedex: join the support server! (%invite for link)");
		extraMessages.add("Want your name next to a Pokemon? Adopt a Pokemon with Patreon! (%patreon for link)");
	}
	
	private void addAliasFields(EmbedCreateSpec builder) {
		for(Language lang : Language.values()) {
			StringBuilder sBuilder = new StringBuilder();
			for(Entry<String, Language> alias : aliases.entrySet()) {
				if(alias.getValue() == lang)
					sBuilder.append(alias.getKey() + "\n");
			}
			
			if(sBuilder.length() != 0)
				builder.addField(lang.getName() + " aliases", sBuilder.toString(), true);
		}
	}
}
