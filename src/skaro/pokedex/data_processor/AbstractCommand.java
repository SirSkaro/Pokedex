package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.List;

import skaro.pokedex.data_processor.commands.ArgumentRange;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.PokeFlexFactory;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public abstract class AbstractCommand 
{
	protected ArgumentRange expectedArgRange;
	protected String commandName;
	protected List<ArgumentCategory> argCats;
	protected PokeFlexFactory factory;
	protected int id;
	protected List<String> aliases;
	
	public AbstractCommand(PokeFlexFactory pff)
	{
		factory = pff;
		argCats = new ArrayList<ArgumentCategory>();
		aliases = new ArrayList<String>();
	}
	
	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public List<ArgumentCategory> getArgumentCats() { return argCats; }
	public List<String> getAliases() { return aliases; }
	
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
}
