package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class StatsCommand extends AbstractCommand  
{	
	private String statHeader1, statHeader2, statHeader3;
	
	public StatsCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "stats".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new ArgumentRange(1,1);
		factory = pff;
		
		statHeader1 = String.format("%s%s\n", StringUtils.rightPad("HP", 12, " "), "Attack");
		statHeader2 = String.format("%s%s\n", StringUtils.rightPad("Defense", 12, " "), "Sp.Attack");
		statHeader3 = String.format("%s%s\n", StringUtils.rightPad("Sp.Defense", 12, " "), "Speed");
		
		createHelpMessage("Darmanitan", "Alolan Sandshrew", "Ninetales Alola", "Mega Venusaur",
				"https://i.imgur.com/svFfe9Q.gif");
	}
	
	public boolean makesWebRequest() { return true; }	
	public String getArguments() { return "<pokemon>"; }
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify exactly one Pokemon as input for this command.".intern());
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("\""+ input.getArg(0).getRawInput() +"\" is not a recognized Pokemon");
				break;
				default:
					reply.addToReply("A technical error occured (code 101)");
			}
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Obtain data
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			//Format reply
			reply.addToReply(("**__"+TextFormatter.flexFormToProper(pokemon.getName())+"__**").intern());
			reply.setEmbededReply(formatEmbed(pokemon));
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1001", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		int stats[] = extractStats(pokemon);
		String type;
		
		String stats1 = String.format("%s%d\n", StringUtils.rightPad(Integer.toString(stats[5]), 12, " "), stats[4]);
		String stats2 = String.format("%s%d\n", StringUtils.rightPad(Integer.toString(stats[3]), 12, " "), stats[2]);
		String stats3 = String.format("%s%d\n", StringUtils.rightPad(Integer.toString(stats[1]), 12, " "), stats[0]);
		
		builder.withDescription("__`"+statHeader1+"`__\n`"+stats1+"`"
								+ "\n\n__`"+ statHeader2+"`__\n`"+stats2+"`"
								+ "\n\n__`"+ statHeader3+"`__\n`"+stats3 +"`");
		
		builder.withTitle("Base Stat Total: "+ getBaseStatTotal(stats));
		
		//Set embed color
		type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		//Add thumbnail
		builder.withThumbnail(pokemon.getSprites().getFrontDefault());
		
		//Add adopter
		this.addAdopter(pokemon, builder);
		
		this.addRandomExtraMessage(builder);
		return builder.build();
	}
	
	private int getBaseStatTotal(int stats[])
	{
		int total = 0;
		
		for(int i = 0; i < stats.length; i++)
			total += stats[i];
		
		return total;
	}
	
	private int[] extractStats(Pokemon poke)
	{
		int[] stats = new int[6];
		
		for(int i = 0; i < 6; i++)
			stats[i] = poke.getStats().get(i).getBaseStat();
		
		return stats;
	}
}