package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
		
		EmbedBuilder builder = new EmbedBuilder();
		Optional<?> flexObj;
		builder.setLenient(true);
		
		//Extract data from data base
		if(input.getArg(0).getCategory() == ArgumentCategory.ABILITY)
		{
			//Obtain data
			flexObj = factory.createFlexObject(Endpoint.ABILITY, input.argsAsList());
	
			//If data is null, then an error occured
			if(!flexObj.isPresent())
			{
				reply.addToReply("A technical error occured (code 1003a). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			
			Ability abil = Ability.class.cast(flexObj.get());
			reply.addToReply(("**__"+TextFormatter.flexFormToProper(abil.getName())+"__**").intern());
			reply.setEmbededReply(formatEmbed(abil));
		}
		else if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON)
		{
			flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
			
			//If data is null, then an error occured
			if(!flexObj.isPresent())
			{
				reply.addToReply("A technical error occured (code 1003b). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			
			Pokemon pokemon = Pokemon.class.cast(flexObj.get());
			List<Optional<?>> flexObjs;
			List<Ability> abilities = new ArrayList<Ability>();
			
			try 
			{
				flexObjs = getAbilityFlexObjs(pokemon);
				abilities = extractAbilitiesFromFlexObjs(flexObjs);
			}
			catch (InterruptedException e) 
			{
				reply.addToReply("A technical error occured (code 1003c). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			catch(IllegalStateException e)
			{
				reply.addToReply("A technical error occured (code 1003d). Please report this (twitter.com/sirskaro))");
				return reply;
			}
			
			reply.addToReply(("**__"+TextFormatter.flexFormToProper(pokemon.getName()+"__**")).intern());
			reply.setEmbededReply(formatEmbed(pokemon, abilities));
		}
		else //This should never be executed
		{
			reply.addToReply("A technical error occured. Please report code 2112 to twitter.com/sirskaro");
		}
		
		return reply;
	}
	
	private List<Ability> extractAbilitiesFromFlexObjs(List<Optional<?>> flexObjs) throws IllegalStateException
	{
		List<Ability> abilities = new ArrayList<Ability>();
		for(Optional<?> obj : flexObjs)
		{
			if(!obj.isPresent())
				throw new IllegalStateException();
			
			abilities.add(Ability.class.cast(obj.get()));
		}
		
		return abilities;
	}
	
	private List<Optional<?>> getAbilityFlexObjs(Pokemon pokemon) throws InterruptedException
	{
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		List<List<String>> args = new ArrayList<List<String>>();
		ArrayList<String> arg;
		String[] urlComponents;
		
		for(skaro.pokeflex.objects.pokemon.Ability abil : pokemon.getAbilities())
		{
			arg = new ArrayList<String>();
			urlComponents = TextFormatter.getURLComponents(abil.getAbility().getUrl());
			arg.add(urlComponents[6]);
			args.add(arg);
			endpoints.add(Endpoint.ABILITY);
		}
		
		return factory.createFlexObjects(endpoints, args);
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon, List<Ability> abilities)
	{
		EmbedBuilder builder = new EmbedBuilder();
		
		switch(abilities.size())
		{
			case 1:
				builder.appendField("Ability 1", TextFormatter.flexFormToProper(abilities.get(0).getName()), true);
				builder.appendField("Ability 2", "N/A", true);
				builder.appendField("Hidden Ability", "N/A", true);
			break;
			case 2:
				builder.appendField("Ability 1",TextFormatter.flexFormToProper(abilities.get(1).getName()), true);
				builder.appendField("Ability 2", "N/A", true);
				builder.appendField("Hidden Ability", TextFormatter.flexFormToProper(abilities.get(0).getName()), true);
			break;
			case 3:
				builder.appendField("Ability 1", TextFormatter.flexFormToProper(abilities.get(2).getName()), true);
				builder.appendField("Ability 2", TextFormatter.flexFormToProper(abilities.get(1).getName()), true);
				builder.appendField("Hidden Ability",TextFormatter.flexFormToProper(abilities.get(0).getName()), true);
			break;
		}
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		return builder.build();
	}
	
	private EmbedObject formatEmbed(Ability abil)
	{
		EmbedBuilder builder = new EmbedBuilder();
		
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