package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;

import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.database_resources.ComplexAbility;
import skaro.pokedex.database_resources.ComplexPokemon;
import skaro.pokedex.database_resources.DatabaseService;
import skaro.pokedex.database_resources.SimpleAbility;
import skaro.pokedex.input_processor.Input;

public class AbilityCommand implements ICommand 
{
	private static AbilityCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	
	private AbilityCommand()
	{
		commandName = "ability".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKE_ABIL);
		expectedArgRange = new Integer[]{1,1};
	}
	
	public static ICommand getInstance()
	{
		if(instance != null)
			return instance;

		instance = new AbilityCommand();
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name] or [ability name]";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("This command must have a Pokemon or Ability as an argument.");
				break;
				case 2:
					reply.addToReply(input.getArg(0).getRaw() +" is not a recognized Pokemon or Ability");
				break;
				default:
					reply.addToReply("A technical error occured (code 103)");
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
		
		DatabaseService dbi = DatabaseService.getInstance();
		
		//Extract data from data base
		if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
		{
			ComplexAbility abil = dbi.getComplexAbility(input.getArg(0).getDB()+"-a");
	
			//If data is null, then an error occured
			if(abil.getName() == null)
			{
				reply.addToReply("A technical error occured (code 1003). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			
			reply.addToReply(("**"+abil.getName()+"**").intern());
			reply.addToReply("\tDescription | "+abil.getShortDesc());
			reply.addToReply("\tTechnical Description | "+abil.getTechDesc());
			reply.addToReply("\tNumber of Pokemon with this Ability | "+abil.getMany());
			reply.addToReply("\tDebut | Generation "+abil.getDebut());
		}
		else if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON)
		{
			ComplexPokemon poke = dbi.getComplexPokemon(input.getArg(0).getDB());
			
			//If data is null, then an error occured
			if(poke.getSpecies() == null)
			{
				reply.addToReply("A technical error occured (code 1004). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			
			ArrayList<SimpleAbility> sAbils = poke.getAbilities();
			
			reply.addToReply(("**"+poke.getSpecies()+"**").intern());
			switch(sAbils.size())
			{
				case 1:
					reply.addToReply("\tAbility 1 | "+sAbils.get(0).getName());
					reply.addToReply("\tAbility 2 | N/A");
					reply.addToReply("\tHidden Ability | N/A");
				break;
				case 2:
					reply.addToReply("\tAbility 1 | "+sAbils.get(0).getName());
					reply.addToReply("\tAbility 2 | N/A");
					reply.addToReply("\tHidden Ability | "+sAbils.get(1).getName());
				break;
				case 3:
					reply.addToReply("\tAbility 1 | "+sAbils.get(0).getName());
					reply.addToReply("\tAbility 2 | "+sAbils.get(1).getName());
					reply.addToReply("\tHidden Ability | "+sAbils.get(2).getName());
				break;
			}
		}
		else //This should never be executed
		{
			reply.addToReply("A technical error occured. Please report code 364 to twitter.com/sirskaro");
		}
		
		return reply;
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
				
		DatabaseService dbi = DatabaseService.getInstance();
		
		//Extract data from data base
		if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
		{
			ComplexAbility abil = dbi.getComplexAbility(input.getArg(0).getDB()+"-a");
	
			//If data is null, then an error occured
			if(abil.getName() == null)
			{
				reply.addToReply("A technical error occured (code 1003). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			
			reply.addToReply("*"+abil.getName()+"*");
			reply.addToReply("Description:"+abil.getShortDesc());
			reply.addToReply("Pokemon with this Ability:"+abil.getMany());
			reply.addToReply("Debut:Gen "+abil.getDebut());
		}
		else if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON)
		{
			ComplexPokemon poke = dbi.getComplexPokemon(input.getArg(0).getDB());
			
			//If data is null, then an error occured
			if(poke.getSpecies() == null)
			{
				reply.addToReply("A technical error occured (code 1004). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			
			ArrayList<SimpleAbility> sAbils = poke.getAbilities();
			
			reply.addToReply("*"+poke.getSpecies()+"*");
			switch(sAbils.size())
			{
				case 1:
					reply.addToReply("Ability 1:"+sAbils.get(0).getName());
					reply.addToReply("Ability 2:N/A");
					reply.addToReply("Hidden Ability:N/A");
				break;
				case 2:
					reply.addToReply("Ability 1:"+sAbils.get(0).getName());
					reply.addToReply("Ability 2:N/A");
					reply.addToReply("Hidden Ability:"+sAbils.get(1).getName());
				break;
				case 3:
					reply.addToReply("Ability 1:"+sAbils.get(0).getName());
					reply.addToReply("Ability 2:"+sAbils.get(1).getName());
					reply.addToReply("Hidden Ability:"+sAbils.get(2).getName());
				break;
			}
		}
		else //This should never be executed
		{
			reply.addToReply("A technical error occured. Please report code 364 to twitter.com/sirskaro");
		}
		
		return reply;
	}
}