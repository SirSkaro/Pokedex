package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class RandpokeCommand implements ICommand
{
	private static RandpokeCommand instance;
	private static ArgumentRange expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private RandpokeCommand(PokeFlexFactory pff)
	{
		commandName = "randpoke".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new RandpokeCommand(pff);
		return instance;
	}

	public ArgumentRange getExpectedArgumentRange() { return expectedArgRange; }
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
		Response reply = new Response();
		
		//Obtain data
		Random rand = new Random();
		int randDexNum = rand.nextInt(807) + 1;
		List<String> urlParams = new ArrayList<String>();
		urlParams.add(Integer.toString(randDexNum));
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, urlParams);
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			//Format reply
			reply.setEmbededReply(formatEmbed(pokemon));
		} 
		catch (IOException | PokeFlexException e) { this.addErrorMessage(reply, "1002", e); }
				
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		builder.withTitle(TextFormatter.flexFormToProper(pokemon.getName()).intern() + " | #" + Integer.toString(pokemon.getId()));
		
		//Add images
		builder.withImage(pokemon.getModel().getUrl());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		return builder.build();
	}
}
