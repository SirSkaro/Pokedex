package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.database_resources.DatabaseResourcePool;
import skaro.pokedex.database_resources.SimplePokemon;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class StatsCommand implements ICommand 
{	
	private static StatsCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private StatsCommand(PokeFlexFactory pff)
	{
		commandName = "stats".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new Integer[]{1,1};
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new StatsCommand(pff);
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("You must specify exactly one Pokemon as input for this command.".intern());
				break;
				case 2:
					reply.addToReply("\""+ input.getArg(0).getRaw() +"\" is not a recognized Pokemon");
				break;
				default:
					reply.addToReply("A technical error occured (code 101)");
			}
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input)
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
		catch (IOException | PokeFlexException e) { this.addErrorMessage(reply, "1001", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		int stats[] = extractStats(pokemon);
		String type;
		
		String names1 = String.format("%-12s%s", "HP", "Attack").intern();
		String names2 = String.format("%-12s%s", "Defense", "Sp. Attack").intern();
		String names3 = String.format("%-12s%s", "Sp. Defense", "Speed").intern();
		String stats1 = String.format("%-12d%d", stats[5], stats[4]);
		String stats2 = String.format("%-12d%d", stats[3], stats[2]);
		String stats3 = String.format("%-12d%d", stats[1], stats[0]);
		
		builder.withDescription("__`"+names1+"`__\n`"+stats1+"`"
								+ "\n\n__`"+ names2+"`__\n`"+stats2+"`"
								+ "\n\n__`"+ names3+"`__\n`"+stats3 +"`");
		
		//Set embed color
		type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		return builder.build();
	}
	
	private int[] extractStats(Pokemon poke)
	{
		int[] stats = new int[6];
		
		for(int i = 0; i < 6; i++)
			stats[i] = poke.getStats().get(i).getBaseStat();
		
		return stats;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseResourcePool dbi = DatabaseResourcePool.getInstance();
		SimplePokemon poke = dbi.extractSimplePokeFromDB(input.getArg(0).getDB());
		
		//If data is null, then an error occurred
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1001). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		int stats[] = poke.getStats();
		
		//Organize the data and add it to the reply
		reply.addToReply("*"+poke.getSpecies()+"*");
		
		reply.addToReply("HP:"+stats[1]);
		reply.addToReply("Atk:"+stats[2]);
		reply.addToReply("Def:"+stats[3]);
		reply.addToReply("SpAtk:"+stats[4]);
		reply.addToReply("SpDef:"+stats[5]);
		reply.addToReply("Speed:"+stats[6]);
		
		return reply;
	}
}