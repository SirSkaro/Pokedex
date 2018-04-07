package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.database_resources.ComplexAbility;
import skaro.pokedex.database_resources.ComplexPokemon;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.SimpleAbility;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class AbilityCommand implements ICommand 
{
	private static AbilityCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private AbilityCommand(PokeFlexFactory pff)
	{
		commandName = "ability".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKE_ABIL);
		expectedArgRange = new Integer[]{1,1};
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new AbilityCommand(pff);
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
					reply.addToReply("You must specify exactly one Pokemon or Ability as input for this command.".intern());
				break;
				case 2:
					reply.addToReply("\""+input.getArg(0).getRaw() +"\" is not a recognized Pokemon or Ability");
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
		
		Object flexObj;
		
		//Extract data
		if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
		{
			try 
			{
				//Obtain data
				flexObj = factory.createFlexObject(Endpoint.ABILITY, input.argsAsList());
				Ability abil = Ability.class.cast(flexObj);
				
				//format reply
				reply.addToReply(("**__"+TextFormatter.flexFormToProper(abil.getName())+"__**").intern());
				reply.setEmbededReply(formatEmbed(abil));
			} 
			catch (IOException | PokeFlexException e)  { this.addErrorMessage(reply, "1003a", e); }
		}
		else//if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON)
		{
			try 
			{
				//Obtain data
				flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
				Pokemon pokemon = Pokemon.class.cast(flexObj);
				
				//Format reply
				reply.addToReply(("**__"+TextFormatter.flexFormToProper(pokemon.getName())+"__**").intern());
				reply.setEmbededReply(formatEmbed(pokemon));
			}
			catch (IOException | PokeFlexException e) { this.addErrorMessage(reply, "1003b", e); }
		}
		
		return reply;
	}
		
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		for(int slot = 1; slot <= 4; slot++)
			for(skaro.pokeflex.objects.pokemon.Ability abil : pokemon.getAbilities())
			{
				if(slot == abil.getSlot())
				{
					builder.appendField(abil.isIsHidden() ? "Hidden Ability" : "Ability "+ slot,
							TextFormatter.flexFormToProper(abil.getAbility().getName()), true);
				}
			}
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		return builder.build();
	}
	
	private EmbedObject formatEmbed(Ability abil)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		builder.appendField("Debut", TextFormatter.formatGeneration(abil.getGeneration().getName()), true);
		builder.appendField("Smogon Viability", abil.getRating(), true);
		builder.appendField("Pokemon with this Ability", Integer.toString(abil.getPokemon().size()), true);
		builder.appendField("Game Description", abil.getSdesc(), false);
		builder.appendField("Technical Description", abil.getLdesc(), false);
		builder.withColor(ColorTracker.getColorForAbility());
		
		return builder.build();
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
				
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		
		//Extract data from data base
		if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
		{
			ComplexAbility abil = dbi.extractComplexAbilFromDB(input.getArg(0).getDB()+"-a");
	
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
			ComplexPokemon poke = dbi.extractComplexPokeFromDB(input.getArg(0).getDB());
			
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