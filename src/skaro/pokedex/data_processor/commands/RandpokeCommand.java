package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class RandpokeCommand extends AbstractCommand 
{
	public RandpokeCommand(PokeFlexFactory pff)
	{
		super(pff);
		commandName = "randpoke".intern();
		argCats.add(ArgumentCategory.NONE);
		expectedArgRange = new ArgumentRange(0,0);
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "none"; }

	@Override
	public Response discordReply(Input input, IUser requester)
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
		catch (Exception e) { this.addErrorMessage(reply, input, "1002", e); }
				
		return reply;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon)
	{
		int randNum;
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		builder.withTitle(TextFormatter.flexFormToProper(pokemon.getName()) + " | #" + Integer.toString(pokemon.getId()));
		
		//Add images
		builder.withImage(pokemon.getModel().getUrl());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		//Set a footer with a random chance
		randNum = ThreadLocalRandom.current().nextInt(1, 3 + 1); //1 in 3 chance
		if(randNum == 1)
			builder.withFooterText("See the shiny with \"%shiny "+ TextFormatter.flexFormToProper(pokemon.getName()+"\""));
		
		return builder.build();
	}
}
