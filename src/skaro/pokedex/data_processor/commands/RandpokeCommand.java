package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeTracker;
import skaro.pokedex.database_resources.ComplexPokemon;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.input_processor.Input;
import sx.blah.discord.util.EmbedBuilder;

public class RandpokeCommand implements ICommand
{
	private static RandpokeCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private RandpokeCommand()
	{
		commandName = "randpoke".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new Integer[]{0,0};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new RandpokeCommand();
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

	@Override
	public Response discordReply(Input input) 
	{
		//Set up utility variables
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		ComplexPokemon poke = dbi.extractRandomComplexPokeFromDB();
		Response reply = new Response();
		
		//If data is null, then an error occured
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1002). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		//Format reply
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		int stats[] = poke.getStats();
		
		
		String stats1 = String.format("%-10s%-10s%-10s%-10s%-10s%-10s",
				"HP", "Atk", "Def", "SpAtk", "SpDef","Spd").intern();
		String stats2 = String.format("%-10d%-10d%-10d%-10d%-10d%-10d",
				stats[0], stats[1], stats[2], stats[3], stats[4], stats[5]);
		
		reply.addToReply("**__"+poke.getSpecies()+"__**");
		builder.appendField("Base Stats", "`" +stats1 + "`\n`" + stats2+ "`", false);
		builder.appendField("Typing", 
				poke.getType2() == null ? poke.getType1() : poke.getType1()+"*/* "+poke.getType2(), true);
		builder.appendField("Abilities", listToItemizedDiscordString(poke.getAbilities()), true);
		builder.appendField("National Dex Num", Integer.toString(poke.getDexNum()), true);
		builder.appendField("Height", poke.getHeight() + " m", true);
		builder.appendField("Weight", poke.getWeight() + " kg", true);
		builder.appendField("Gender Ratio", poke.getDiscordGenderRatio(), true);
		builder.appendField("Egg Groups",listToItemizedDiscordString(poke.getEggGroups()), true);
		if(poke.getEvolutions() != null)
		{
			builder.appendField("Evolutions", listToItemizedDiscordString(poke.getEvolutions()), true);
			builder.appendField("Evolution Level", poke.getEvoLevel(), true);
		}
		builder.withFooterText("Note: Shiny Pokemon will return soon!");
		
		//Add images
		builder.withImage(poke.getModel());
		
		//Set embed color
		builder.withColor(TypeTracker.getColor(poke.getType1()));
		
		reply.setEmbededReply(builder.build());
				
		return reply;
	}

	@Override
	public Response twitchReply(Input input) 
	{
		//Set up utility variables
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		ComplexPokemon poke = dbi.extractRandomComplexPokeFromDB();
		Response reply = new Response();
		
		//If data is null, then an error occured
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1002). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		int stats[] = poke.getStats();
		
		//Organize the data and add it to the reply
		reply.addToReply("*"+poke.getSpecies()+"*");
		
		reply.addToReply("Base Stats:"+stats[0]+"/"+stats[1]+"/" +stats[2]+"/"+stats[3]+"/"
				+stats[4]+"/"+stats[5]);
		reply.addToReply("Abilities:"+listToItemizedTwitchString(poke.getAbilities()));
		reply.addToReply("Dex Num:"+poke.getDexNum());
		reply.addToReply("Typing:"+ 
				(poke.getType2() == null ? poke.getType1() : poke.getType1()+"/"+poke.getType2()));
		reply.addToReply("Height:"+poke.getHeight() + "m");
		reply.addToReply("Weight:"+poke.getWeight() + "kg");
		reply.addToReply("Gender Ratio:" + poke.getTwitchGenderRatio());
		reply.addToReply("Egg Groups:"+ listToItemizedTwitchString(poke.getEggGroups()));
		if(poke.getEvolutions() != null)
		{
			reply.addToReply("Evolutions:"+ listToItemizedTwitchString(poke.getEvolutions()));
			reply.addToReply("Evolution Level:"+poke.getEvoLevel());
		}
				
		return reply;
	}
}
