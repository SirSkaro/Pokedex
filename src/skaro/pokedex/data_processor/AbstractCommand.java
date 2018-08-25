package skaro.pokedex.data_processor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import skaro.pokedex.data_processor.commands.ArgumentRange;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.PokeFlexFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public abstract class AbstractCommand 
{
	protected ArgumentRange expectedArgRange;
	protected String commandName;
	protected List<ArgumentCategory> argCats;
	protected PokeFlexFactory factory;
	protected List<String> aliases, extraMessages;
	protected EmbedObject helpMessage;
	
	
	public AbstractCommand(PokeFlexFactory pff)
	{
		factory = pff;
		argCats = new ArrayList<ArgumentCategory>();
		aliases = new ArrayList<String>();
		extraMessages = new ArrayList<String>();
		
		extraMessages.add("If you like Pokedex, consider becoming a Patreon for perks! (%patreon for link)");
		extraMessages.add("Stay up to date with Pokedex: join the support server! (%invite for link)");
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public List<ArgumentCategory> getArgumentCats() { return argCats; }
	public List<String> getAliases() { return aliases; }
	public List<String> getExtraMessages() { return extraMessages; }
	public EmbedObject getHelpMessage() { return helpMessage; }
	
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
		StringBuilder aliasBuilder = new StringBuilder();
		StringBuilder exampleBuilder = new StringBuilder();
		builder.setLenient(true);
		
		for(String alias : aliases)
			aliasBuilder.append(alias + "\n");
		
		exampleBuilder.append("!"+commandName+" "+ex1+"\n");
		exampleBuilder.append("%"+commandName+" "+ex2+"\n");
		exampleBuilder.append(commandName+"("+ex3+")\n");
		exampleBuilder.append("@Pokedex "+commandName+" "+ex4);
		
		builder.appendField("Input", this.getArguments(), true);
		builder.appendField("Min/Max Inputs", expectedArgRange.getMin()+"/"+expectedArgRange.getMax(), true);
		builder.appendField("Aliases", aliasBuilder.toString(), true);
		builder.appendField("Examples", exampleBuilder.toString(), true);
		builder.withImage(imageURL);
		builder.withThumbnail("https://images.discordapp.net/avatars/206147275775279104/e535e65cef619085c66736d8433ade73.png?size=512");
		
		builder.withColor(new Color(0xD60B01));
		
		helpMessage = builder.build();
	}
	
	protected void createHelpMessage(String imageURL)
	{
		EmbedBuilder builder = new EmbedBuilder();
		StringBuilder aliasBuilder = new StringBuilder();
		builder.setLenient(true);
		
		for(String alias : aliases)
			aliasBuilder.append(alias + "\n");
		
		builder.appendField("Input", this.getArguments(), true);
		builder.appendField("Aliases", aliasBuilder.toString(), true);
		builder.withImage(imageURL);
		builder.withThumbnail("https://images.discordapp.net/avatars/206147275775279104/e535e65cef619085c66736d8433ade73.png?size=512");
		
		builder.withColor(new Color(0xD60B01));
		
		helpMessage = builder.build();
	}
}
