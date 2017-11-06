package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.ComplexPokemon;
import skaro.pokedex.database_resources.DatabaseService;
import skaro.pokedex.input_processor.Input;

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
		DatabaseService dbi = DatabaseService.getInstance();
		ComplexPokemon poke = dbi.getRandomComplexPokemon();
		Response reply = new Response();
		
		//If data is null, then an error occured
		if(poke.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1002). Please report this (twitter.com/sirskaro))");
			return reply;
		}
		
		int stats[] = poke.getStats();
		
		//Organize the data and add it to the reply
		reply.addToReply(("**"+poke.getSpecies()+"**").intern());
		
		reply.addToReply("\tBase Stats | "+stats[0]+"*/* "+stats[1]+"*/* " +stats[2]+"*/* "+stats[3]+"*/* "
				+stats[4]+"*/* "+stats[5]);
		reply.addToReply("\tAbilities | "+listToItemizedDiscordString(poke.getAbilities()));
		reply.addToReply("\tNational Dex Num | "+poke.getDexNum());
		reply.addToReply("\tTyping | "+ 
				(poke.getType2() == null ? poke.getType1() : poke.getType1()+"*/* "+poke.getType2()));
		reply.addToReply("\tHeight | "+poke.getHeight() + " m");
		reply.addToReply("\tWeight | "+poke.getWeight() + " kg");
		reply.addToReply("\tGender Ratio | " + poke.getDiscordGenderRatio());
		reply.addToReply("\tEgg Groups | "+ listToItemizedDiscordString(poke.getEggGroups()));
		if(poke.getEvolutions() != null)
		{
			reply.addToReply("\tEvolutions | "+ listToItemizedDiscordString(poke.getEvolutions()));
			reply.addToReply("\tEvolution Level | "+poke.getEvoLevel());
		}
		
		//Add images
		reply.addImage(poke.getModel());
		reply.addImage(poke.getShinyModel());
				
		return reply;
	}

	@Override
	public Response twitchReply(Input input) 
	{
		//Set up utility variables
		DatabaseService dbi = DatabaseService.getInstance();
		ComplexPokemon poke = dbi.getRandomComplexPokemon();
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
