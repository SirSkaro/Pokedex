package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.util.ArrayList;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class ShinyCommand implements ICommand
{
	private static ShinyCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private ShinyCommand(PokeFlexFactory pff)
	{
		commandName = "shiny".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new Integer[]{1,1};
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new ShinyCommand(pff);
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
					reply.addToReply("\""+input.getArg(0).getRaw() +"\" is not a recognized Pokemon.");
				break;
				default:
					reply.addToReply("A technical error occured (code 112)");
			}
			return false;
		}
		return true;
	}

	@Override
	public Response discordReply(Input input) 
	{
		//Set up utility variables
		Response reply = new Response();
		
		//Obtain data
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			//Format reply
			reply.setEmbededReply(formatEmbed(pokemon));
		} 
		catch (IOException | PokeFlexException e) { this.addErrorMessage(reply, "1012", e); }
				
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		builder.withTitle(TextFormatter.flexFormToProper(pokemon.getName()).intern() + " | #" + Integer.toString(pokemon.getId()));
		
		//Add images
		builder.withImage(pokemon.getShinyModel().getUrl());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		return builder.build();
	}

	@Override
	public Response twitchReply(Input input) 
	{
		return null;
	}
}
