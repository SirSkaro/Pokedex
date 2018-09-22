package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TTSConverter;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.FlavorTextEntry;
import skaro.pokeflex.objects.pokemon_species.Genera;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class DexCommand extends AbstractCommand
{
	private TTSConverter tts;
	
	public DexCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "dex".intern();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.VERSION);
		expectedArgRange = new ArgumentRange(2,2);
		tts = new TTSConverter();
		
		aliases.put("pokedex", Language.ENGLISH);
		aliases.put("entry", Language.ENGLISH);
		
		createHelpMessage("Mew, Red", "kadabra, fire red", "Phantump, y", "Darumaka, white",
				"https://i.imgur.com/AvJMBpR.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>, <version>"; }
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify a Pokemon and a Version as input for this command "
							+ "(seperated by commas).");
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(AbstractArgument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
				break;
				default:
					reply.addToReply("A technical error occured (code 110)");
			}
			return false;
		}
		
		return true;
	}
	
	public Response discordReply2(Input input, IUser requester)
	{
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedBuilder builder = new EmbedBuilder();
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		Request request;
		RequestURL requestURL;
		
		//Obtain data
		try
		{
			//Pokemon
			request = new Request(Endpoint.POKEMON);
			request.addParam(input.getArg(0).getFlexForm());
			concurrentRequestList.add(request);
			
			//Version
			request = new Request(Endpoint.VERSION);
			request.addParam(input.getArg(1).getFlexForm());
			concurrentRequestList.add(request);
			
			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			//PokemonSpecies
			Pokemon pokemon = (Pokemon)dataMap.getValue(Pokemon.class.getName(), 0);
			requestURL = new RequestURL(pokemon.getSpecies().getUrl(), Endpoint.POKEMON_SPECIES);
			PokemonSpecies species = (PokemonSpecies)factory.createFlexObject(requestURL);
			dataMap.put(PokemonSpecies.class.getName(), species);
			
			//Add adopter
			this.addAdopter(pokemon, builder);
			
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			response.addToReply("Get back in there and figure out what you did wrong!");
			e.printStackTrace();
			return response;
		}
	}
	
	public Response discordReply(Input input, IUser requester)
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
			urlParams.add(input.getArg(0).getFlexForm());//Pokemon name
			Object flexObj = factory.createFlexObject(Endpoint.POKEMON, urlParams);
			pokemon = Pokemon.class.cast(flexObj);
			
			urlParams.clear();
			urlParams.add(pokemon.getSpecies().getName());
			flexObj = factory.createFlexObject(Endpoint.POKEMON_SPECIES, urlParams);
			speciesData = PokemonSpecies.class.cast(flexObj);
		} 
		catch(Exception e) { this.addErrorMessage(reply, input, "1010", e); }
		
		//Check if the Pokemon has a Pokedex entry that meets the user criteria
		Optional<FlavorTextEntry> entry = getEntry(speciesData, input.getArg(1).getDbForm());
		
		if(!entry.isPresent())
		{
			reply.addToReply(TextFormatter.flexFormToProper(speciesData.getName())+" does not have a Pokedex entry in "
							+TextFormatter.flexFormToProper(input.getArg(1).getRawInput()) + " version");
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
		builder.withColor(ColorTracker.getColorForVersion(input.getArg(1).getDbForm()));
		
		//Add thumbnail
		builder.withThumbnail(pokemon.getSprites().getFrontDefault());
		
		//Add adopter
		this.addAdopter(pokemon, builder);
		
		reply.setEmbededReply(builder.build());
		
		//Add audio reply
		reply.setPlayBack(tts.convertToAudio(replyContent));
		
		this.addRandomExtraMessage(builder);
		
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
}
