package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TTSConverter;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.database_resources.PokedexEntry;
import skaro.pokedex.input_processor.Argument;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.FlavorTextEntry;
import skaro.pokeflex.objects.pokemon_species.Genera;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.util.EmbedBuilder;

public class DexCommand implements ICommand
{
	private static DexCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private TTSConverter tts;
	private static PokeFlexFactory factory;
	
	private DexCommand(PokeFlexFactory pff)
	{
		commandName = "dex".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.VERSION);
		expectedArgRange = new Integer[]{2,2};
		tts = new TTSConverter();
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new DexCommand(pff);
		return instance;
	}
	
	public Integer[] getExpectedArgNum() { return expectedArgRange; }
	public String getCommandName() { return commandName; }
	public ArrayList<ArgumentCategory> getArgumentCats() { return argCats; }
	
	public String getArguments()
	{
		return "[pokemon name], [game version] (not updated for gen 7)";
	}
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case 1:
					reply.addToReply("You must specify a Pokemon and a Version as input for this command "
							+ "(seperated by commas).");
				break;
				case 2:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(Argument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRaw()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: Not updated for gen 7. Try gens 1-6?");
				break;
				default:
					reply.addToReply("A technical error occured (code 110)");
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
		Pokemon pokemon = null;
		PokemonSpecies speciesData = null;
		try 
		{
			List<String> urlParams = new ArrayList<String>();
			urlParams.add(input.getArg(0).getFlex());//Pokemon name
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, urlParams);
			pokemon = Pokemon.class.cast(flexObj);
			
			urlParams.clear();
			urlParams.add(pokemon.getSpecies().getName());
			flexObj = factory.createFlexObject(Endpoint.POKEMON_SPECIES, urlParams);
			speciesData = PokemonSpecies.class.cast(flexObj);
		} 
		catch (IOException | PokeFlexException e) { this.addErrorMessage(reply, "1010", e); }
		
		//Check if the Pokemon has a Pokedex entry that meets the user criteria
		Optional<FlavorTextEntry> entry = getEntry(speciesData, input.getArg(1).getDB());
		
		if(!entry.isPresent())
		{
			reply.addToReply(TextFormatter.flexFormToProper(speciesData.getName())+" does not have a Pokedex entry in "
							+TextFormatter.flexFormToProper(input.getArg(1).getRaw()) + " version");
			return reply;
		}
		
		//Format reply
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		String replyContent = TextFormatter.flexFormToProper(speciesData.getName())+", the "+ getGenera(speciesData)+": "
				+ TextFormatter.formatDexEntry(entry.get().getFlavorText());
		
		reply.addToReply("Pokedex entry for **"+TextFormatter.flexFormToProper(speciesData.getName())+"** from **" 
				+TextFormatter.flexFormToProper(entry.get().getVersion().getName())+"**:");
		
		builder.withDescription(replyContent);
		builder.withColor(ColorTracker.getColorForVersion(input.getArg(1).getDB()));
		reply.setEmbededReply(builder.build());
		
		//Add audio reply
		reply.setPlayBack(tts.convertToAudio(replyContent));
		
		return reply;
	}
	
	private Optional<FlavorTextEntry> getEntry(PokemonSpecies speciesData, String version)
	{
		for(FlavorTextEntry entry : speciesData.getFlavorTextEntries())
			if(TextFormatter.flexToDBForm(entry.getVersion().getName()).equals(version) 
					&& entry.getLanguage().getName().equals("en"))
				return Optional.of(entry);
		
		return Optional.empty();
	}
	
	private String getGenera(PokemonSpecies speciesData)
	{
		for(Genera genera : speciesData.getGenera())
			if(genera.getLanguage().getName().equals("en"))
				return genera.getGenus();
		
		throw new IllegalStateException("No genera could be found!");
	}
	
	public Response twitchReply(Input input)
	{ 
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Utility variables
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		PokedexEntry entry = dbi.extractDexEntryFromDB(input.getArg(0).getDB(), input.getArg(1).getDB());
		
		if(entry.getSpecies() == null)
		{
			reply.addToReply("A technical error occured (code 1010). Please report this (twitter.com/sirskaro))");
			return reply;
		}
				
		if(entry.getEntry() == null)
		{
			reply.addToReply(entry.getSpecies()+" does not have a pokedex entry in "
							+input.getArg(1).getRaw());
			return reply;
		}
		
		reply.addToReply("Pokedex entry for "+entry.getSpecies()+" from " 
						+entry.getVersion()+":");
		reply.addToReply(entry.getEntry());
		
		return reply;
	}
}
