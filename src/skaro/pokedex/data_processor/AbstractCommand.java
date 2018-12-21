package skaro.pokedex.data_processor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import skaro.pokedex.core.PokedexManager;
import skaro.pokedex.data_processor.commands.ArgumentRange;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public abstract class AbstractCommand 
{
	protected ArgumentRange expectedArgRange;
	protected String commandName;
	protected List<ArgumentCategory> argCats;
	protected List<String> extraMessages;
	protected EmbedObject helpMessage;
	protected Map<String, Language> aliases;
	protected IDiscordFormatter formatter;
	
	public AbstractCommand()
	{
		argCats = new ArrayList<ArgumentCategory>();
		aliases = new HashMap<String, Language>();
		extraMessages = new ArrayList<String>();
		
		extraMessages.add("If you like Pokedex, consider becoming a Patreon for perks! (%patreon for link)");
		extraMessages.add("Stay up to date with Pokedex: join the support server! (%invite for link)");
		extraMessages.add("Want your name next to a Pokemon? Adopt a Pokemon with Patreon! (%patreon for link)");
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public List<ArgumentCategory> getArgumentCats() { return argCats; }
	public Map<String, Language> getAliases() { return aliases; }
	public List<String> getExtraMessages() { return extraMessages; }
	public EmbedObject getHelpMessage() { return helpMessage; }
	
	public Language getLanguageOfAlias(String alias)
	{
		Language result = aliases.get(alias);
		
		if(result == null)
			return Language.ENGLISH;
		
		return result;
	}
	
	abstract public boolean makesWebRequest();
	abstract public String getArguments();
	abstract public Response discordReply(Input input, IUser requester);
	
	protected boolean inputIsValid(Response reply, Input input) { return true; }
	
	protected String listToItemizedString(List<?> list)
	{
		if(list.isEmpty())
			return "None".intern();
		
		StringBuilder result = new StringBuilder();
		int i;
		for(i = 0; i < list.size() - 1; i++)
			if(i %2 == 0)
				result.append(list.get(i).toString() + "*/* ");
			else
				result.append(list.get(i).toString() + "\n");

		result.append(list.get(i).toString());
		
		return result.toString();
	}
	
	protected void addErrorMessage(Response reply, Input input, String errCode, Exception e)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		reply.addToReply("**Error Report**");
		builder.withDesc("Could not get requested data. My external API may be down or may not have your data. Please try again later.\n\n"
				+ "If you think this is a bug, please __screenshot this report__ and post it in the Support Server!");
		builder.appendField("Error Code", errCode, true);
		builder.appendField("Technical Error", e.getClass().getSimpleName(), true);
		builder.appendField("User Input", input.argsToString(), true);
		builder.appendField("Link to Support Server", "[Click here to report](https://discord.gg/D5CfFkN)", true);
		
		reply.setEmbededReply(builder.build());
	}
	
	protected void addRandomExtraMessage(EmbedBuilder builder)
	{
		int randNum = ThreadLocalRandom.current().nextInt(1, 5); //1 in 4 chance
		if(randNum == 1)
		{
			randNum = ThreadLocalRandom.current().nextInt(0, extraMessages.size());
			builder.withFooterText(extraMessages.get(randNum));
		}
	}
	
	protected void createHelpMessage(String ex1, String ex2, String ex3, String ex4, String imageURL)
	{
		EmbedBuilder builder = new EmbedBuilder();
		StringBuilder exampleBuilder = new StringBuilder();
		builder.setLenient(true);
		
		exampleBuilder.append("!"+commandName+" "+ex1+"\n");
		exampleBuilder.append("%"+commandName+" "+ex2+"\n");
		exampleBuilder.append(commandName+"("+ex3+")\n");
		exampleBuilder.append("@Pokedex "+commandName+" "+ex4);
		
		builder.appendField("Input", this.getArguments(), true);
		builder.appendField("Min/Max Inputs", expectedArgRange.getMin()+"/"+expectedArgRange.getMax(), true);
		this.addAliasFields(builder);
		builder.appendField("Examples", exampleBuilder.toString(), true);
		builder.withImage(imageURL);
		builder.withThumbnail("https://images.discordapp.net/avatars/206147275775279104/e535e65cef619085c66736d8433ade73.png?size=512");
		
		builder.withColor(new Color(0xD60B01));
		
		helpMessage = builder.build();
	}
	
	protected void createHelpMessage(String imageURL)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		builder.appendField("Input", this.getArguments(), true);
		this.addAliasFields(builder);
		builder.withImage(imageURL);
		builder.withThumbnail("https://images.discordapp.net/avatars/206147275775279104/e535e65cef619085c66736d8433ade73.png?size=512");
		
		builder.withColor(new Color(0xD60B01));
		
		helpMessage = builder.build();
	}
	
	protected void addAdopter(Pokemon pokemon, EmbedBuilder builder)
	{
		Optional<IUser> adopterCheck = PokedexManager.INSTANCE.PerkService().getPokemonsAdopter(pokemon.getName());
		
		if(adopterCheck.isPresent())
		{
			builder.withAuthorName(adopterCheck.get().getName() + "'s "+TextFormatter.flexFormToProper(pokemon.getName()));
			builder.withAuthorIcon(getPatreonLogo());
		}
	}

	protected String getPatreonLogo()
	{
		return "https://c5.patreon.com/external/logo/downloads_logomark_color_on_coral.png".intern();
	}
	
	protected String getPatreonBanner()
	{
		return "https://c5.patreon.com/external/logo/become_a_patron_button.png".intern();
	}
	
	private void addAliasFields(EmbedBuilder builder)
	{
		for(Language lang : Language.values())
		{
			StringBuilder sBuilder = new StringBuilder();
			for(Entry<String, Language> alias : aliases.entrySet())
			{
				if(alias.getValue() == lang)
					sBuilder.append(alias.getKey() + "\n");
			}
			
			if(sBuilder.length() != 0)
				builder.appendField(lang.getName() + " aliases", sBuilder.toString(), true);
		}
	}
}
