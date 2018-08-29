package skaro.pokedex.data_processor.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import skaro.pokedex.core.Configurator;
import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class ShinyCommand extends AbstractCommand 
{
	private String baseModelPath;
	
	public ShinyCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "shiny".intern();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new ArgumentRange(1,1);
		baseModelPath = Configurator.getInstance().get().getModelBasePath();
		
		createHelpMessage("Ponyta", "Solgaleo", "Keldeo resolute", "eevee",
				"https://i.imgur.com/FLBOsD5.gif");
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
					reply.addToReply("\""+input.getArg(0).getRawInput() +"\" is not a recognized Pokemon.");
				break;
				default:
					reply.addToReply("A technical error occured (code 112)");
			}
			return false;
		}
		return true;
	}

	@Override
	public Response discordReply(Input input, IUser requester) 
	{
		//Set up utility variables
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
				
		if(checker.userHasCommandPrivileges(requester))
			formatPrivilegedReply(reply, input);
		else
			formatNonPrivilegedReply(reply, input);
				
		return reply;
	}
	
	private void formatNonPrivilegedReply(Response reply, Input input)
	{
		String path;
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		try
		{
			//format embed
			builder.withImage("attachment://jirachi.gif");
			builder.withColor(ColorTracker.getColorForType("psychic"));
			builder.withThumbnail("https://c5.patreon.com/external/logo/become_a_patron_button.png");
			
			//specify file path
			path = baseModelPath + "/jirachi.gif";
			
			//format reply
			reply.addToReply("Pledge $1/month on Patreon to gain access to all HD shiny Pokemon!");
			builder.appendField("Patreon link", "[Pokedex's Patreon](https://www.patreon.com/sirskaro)", false);
			reply.addImage(new File(path));
			reply.setEmbededReply(builder.build());
		}
		catch (Exception e) { this.addErrorMessage(reply, input, "1012a", e); }
	}
	
	private void formatPrivilegedReply(Response reply, Input input)
	{
		List<String> urlParameters = new ArrayList<String>();
		String path;
		File image;
		
		//Obtain data
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, input.argsAsList());
			Pokemon pokemon = Pokemon.class.cast(flexObj);
			
			urlParameters.add(pokemon.getSpecies().getName());
			flexObj = factory.createFlexObject(Endpoint.POKEMON_SPECIES, urlParameters);
			PokemonSpecies speciesData = PokemonSpecies.class.cast(flexObj);
			
			//Format reply
			reply.addToReply("**__"+TextFormatter.pokemonFlexFormToProper(pokemon.getName())+" | #" + Integer.toString(speciesData.getId()) 
				+ " | " + TextFormatter.formatGeneration(speciesData.getGeneration().getName()) + "__**");
			
			//Upload local file
			path = baseModelPath + "/" + pokemon.getName() + ".gif";
			image = new File(path);
			reply.addImage(image);
			
			reply.setEmbededReply(formatEmbed(pokemon, image));
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1012b", e); }
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon, File image) throws IOException
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		//Add images
		builder.withImage("attachment://"+image.getName());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		//Add adopter
		addAdopter(pokemon, builder);
		
		return builder.build();
	}
}
