package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.ICommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextFormatter;
import skaro.pokedex.database_resources.ComplexPokemon;
import skaro.pokedex.database_resources.DatabaseInterface;
import skaro.pokedex.input_processor.Input;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexException;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.evolution_chain.Chain;
import skaro.pokeflex.objects.evolution_chain.EvolutionChain;
import skaro.pokeflex.objects.evolution_chain.EvolutionDetail;
import skaro.pokeflex.objects.evolution_chain.EvolvesTo;
import skaro.pokeflex.objects.pokemon.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.Type;
import skaro.pokeflex.objects.pokemon_species.EggGroup;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class DataCommand implements ICommand 
{
	private static DataCommand instance;
	private static Integer[] expectedArgRange;
	private static String commandName;
	private static ArrayList<ArgumentCategory> argCats;
	private static PokeFlexFactory factory;
	
	private DataCommand(PokeFlexFactory pff)
	{
		commandName = "data".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKEMON);
		expectedArgRange = new Integer[]{1,1};
		factory = pff;
	}
	
	public static ICommand getInstance(PokeFlexFactory pff)
	{
		if(instance != null)
			return instance;

		instance = new DataCommand(pff);
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
					reply.addToReply("A technical error occured (code 102)");
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
		
		//Obtain base Pokemon data
		List<Object> baseData;
		Pokemon pokemon = null;
		PokemonSpecies speciesData = null;
		try 
		{
			baseData = getBaseData(input.argsAsList());
			pokemon = Pokemon.class.cast(getDataOfInstance(baseData, Pokemon.class));
			speciesData = PokemonSpecies.class.cast(getDataOfInstance(baseData, PokemonSpecies.class));
		}
		catch (InterruptedException | PokeFlexException e) 
		{
			this.addErrorMessage(reply, "1002a", e);
			return reply;
		}
		
		//Obtain peripheral data
		List<Object> peripheralData;
		try 
		{
			peripheralData = getPeripheralData(speciesData);
		}
		catch (InterruptedException | PokeFlexException e) 
		{
			this.addErrorMessage(reply, "1002b", e);
			return reply;
		}
		
		//Format reply
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		reply.addToReply("**__"+TextFormatter.flexFormToProper(pokemon.getName())+"__**");
		reply.setEmbededReply(formatEmbed(pokemon, speciesData, peripheralData));
				
		return reply;
	}

	private EmbedObject formatEmbed(Pokemon pokemon, PokemonSpecies speciesData, List<Object> peripheralData)
	{
		EmbedBuilder builder = new EmbedBuilder();	
		builder.setLenient(true);
		
		//Format base data - Pokemon
		builder.appendField("Base Stats", formatBaseStats(extractStats(pokemon)), true);
		builder.appendField("Typing", listToItemizedDiscordString(extractTyping(pokemon)), true);
		builder.appendField("Abilities", listToItemizedDiscordString(extractAbilities(pokemon)), true);
		builder.appendField("National Dex Num", Integer.toString(pokemon.getId()), true);
		builder.appendField("Height", pokemon.getHeight()/10.0 + " m", true);
		builder.appendField("Weight", pokemon.getWeight()/10.0 + " kg", true);
		
		//Format base data - PokemonSpecies
		builder.appendField("Gender Ratio", formatGenderRatio(speciesData), true);
		builder.appendField("Egg Groups",listToItemizedDiscordString(formatEggGroup(speciesData)), true);
		
		//Format peripheral data - Evolution Chain
		EvolutionChain evolutionData = EvolutionChain.class.cast(getDataOfInstance(peripheralData, EvolutionChain.class));
		if(!isOnlyEvolution(evolutionData))
		{
			builder.appendField("Evolution Chain", formatEvolutionChain(evolutionData, pokemon.getName()) , true);
			builder.appendField("Evolution Requirements", formatEvolutionDetails(evolutionData, pokemon.getName()), true);
		}
		
		//Footer data
		builder.withFooterText("Note: Shiny Pokemon will return soon!");
		
		//Add images
		builder.withImage(pokemon.getModel().getUrl());
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		return builder.build();
	}
	
	private Object getDataOfInstance(List<Object> data, Class<?> cls) throws IllegalArgumentException
	{
		for(Object obj : data)
			if(cls.isInstance(obj))
				return obj;
		
		throw new IllegalArgumentException("No instance of the specified class could be found.");
	}
	
	private ArrayList<String> extractTyping(Pokemon pokemon)
	{
		List<Type> types = pokemon.getTypes();
		ArrayList<String> result = new ArrayList<String>();
		
		for(Type type : types)
			result.add(TextFormatter.flexFormToProper(type.getType().getName()));
		
		return result;
	}
	
	private ArrayList<String> extractAbilities(Pokemon pokemon)
	{
		List<Ability> abilities = pokemon.getAbilities();
		ArrayList<String> result = new ArrayList<String>();
		
		for(Ability ability : abilities)
			result.add(TextFormatter.flexFormToProper(ability.getAbility().getName()));
		
		return result;
	}
	
	private String formatBaseStats(int[] stats)
	{
		String names1 = String.format("%-9s%-9s%s", "HP", "Atk", "Def").intern();
		String names2 = String.format("%-9s%-9s%s", "Sp.Atk", "Sp.Def", "Spe").intern();
		String stats1 = String.format("%-9d%-9d%d", stats[0], stats[1], stats[2]);
		String stats2 = String.format("%-9d%-9d%d", stats[3], stats[4], stats[5]);
		String baseStats = "__`"+names1+"`__\n`"+stats1+"`"
				+ "\n__`"+ names2+"`__\n`"+stats2+"`";
		
		return baseStats;
	}
	
	private boolean isOnlyEvolution(EvolutionChain evolutionData)
	{
		return evolutionData.getChain().getEvolvesTo().isEmpty();
	}
		
	/**
	 *	Checks the peripheral data to see if this Pokemon evolves.
	 *	@return Optional of the evolution data if it exists and the Pokemon evolves. Empty Optional otherwise
	 **/
	private String formatEvolutionChain(EvolutionChain evolutionData, String thisPokemon)
	{
		StringBuilder builder = new StringBuilder();
		Chain chain = evolutionData.getChain();
		int nameIndexStart;
		
		//first Pokemon name
		builder.append(TextFormatter.flexFormToProper(chain.getSpecies().getName()));
		builder.append(" -> ");
		
		//recursively parse chain
		formatEvolutionChainResursive(chain.getEvolvesTo(), builder);
		
		//decorate the text of this Pokemon
		thisPokemon = TextFormatter.flexFormToProper(thisPokemon);
		nameIndexStart = builder.indexOf(thisPokemon);
		builder.insert(nameIndexStart + thisPokemon.length(), "__");
		builder.insert(nameIndexStart, "__");
		
		return  builder.toString();
	}
	
	private void formatEvolutionChainResursive(List<EvolvesTo> evoTo, StringBuilder builder)
	{
		if(evoTo.size() == 1)
		{
			builder.append(TextFormatter.flexFormToProper(evoTo.get(0).getSpecies().getName()));
			if(!evoTo.get(0).getEvolvesTo().isEmpty())
			{
				builder.append(" -> ");
				formatEvolutionChainResursive(evoTo.get(0).getEvolvesTo(), builder);
			}
		}
		else
		{
			for(EvolvesTo evo : evoTo)
			{
				builder.append(TextFormatter.flexFormToProper(evo.getSpecies().getName()));
				if(!evo.getEvolvesTo().isEmpty())
				{
					builder.append(" -> ");
					formatEvolutionChainResursive(evo.getEvolvesTo(), builder);
				}
				builder.append("/");
			}
			builder.deleteCharAt(builder.lastIndexOf("/"));
		}
	}
	
	private String formatEvolutionDetails(EvolutionChain evolutionData, String thisPokemon)
	{
		StringBuilder builder = new StringBuilder();
		Chain chain = evolutionData.getChain();
		List<EvolutionDetail> eDetails = null;
		
		if(chain.getSpecies().getName().equals(thisPokemon))
			eDetails = chain.getEvolutionDetails();
		else
			eDetails = extractEvolutionDetailsRecursive(chain.getEvolvesTo(), thisPokemon, null);
		
		for(EvolutionDetail detail : eDetails)
		{
			builder.append(TextFormatter.flexFormToProper(detail.getTrigger().getName())+": ");
			
			if(detail.getMinLevel() != 0)
				builder.append("Min level: "+detail.getMinLevel() + " & ");
			if(detail.getMinBeauty() != 0)
				builder.append("Min beauty: "+detail.getMinBeauty() + " & ");
			if(detail.getTimeOfDay() != null && !detail.getTimeOfDay().isEmpty())
				builder.append("At "+detail.getTimeOfDay()+ "-time & ");
			if(detail.getGender() != 0)
			{
				switch(detail.getGender())
				{
					case 1: builder.append("Must be female & "); break;
					case 2: builder.append("Must be male & "); break;
				}
			}
			if(detail.getRelativePhysicalStats() != 0)
			{
				switch(detail.getRelativePhysicalStats())
				{
					case 0: builder.append("Attack = Defense & "); break;
					case 1: builder.append("Attack > Defense & "); break;
					case -1: builder.append("Attack < Defense & "); break;
				}
			}
			if(detail.isNeedsOverworldRain())
				builder.append("Needs overworld rain & ");
			if(detail.isTurnUpsideDown())
				builder.append("Turn 3DS upside down & ");
			if(detail.getItem() != null)
				builder.append("Use "+TextFormatter.flexFormToProper(detail.getItem().getName()) +" & ");
			if(detail.getKnownMoveType() != null)
				builder.append("Know "+ TextFormatter.flexFormToProper(detail.getKnownMoveType().getName()) +"-type move & ");
			if(detail.getMinAffection() != 0)
				builder.append("Min affection: "+detail.getMinAffection() + " & ");
			if(detail.getPartyType() != null)
				builder.append("With "+ TextFormatter.flexFormToProper(detail.getPartyType().getName()) +"-type in party & ");
			if(detail.getTradeSpecies() != null)
				builder.append("Trade for "+ TextFormatter.flexFormToProper(detail.getTradeSpecies().getName()) +" & ");
			if(detail.getPartySpecies() != null)
				builder.append("With "+ TextFormatter.flexFormToProper(detail.getPartySpecies().getName()) +" as party member & ");
			if(detail.getMinHappiness() != 0)
				builder.append("Min happiness: "+detail.getMinHappiness() + " & ");
			if(detail.getHeldItem() != null)
				builder.append("Holding item "+ TextFormatter.flexFormToProper(detail.getHeldItem().getName()) +" & ");
			if(detail.getKnownMove() != null)
				builder.append("Knows move "+ TextFormatter.flexFormToProper(detail.getKnownMove().getName()) +" & ");
			if(detail.getLocation() != null)
				builder.append("At location "+ TextFormatter.flexFormToProper(detail.getLocation().getName()) +" & ");
			
			if(builder.lastIndexOf("&") != -1)
				builder.deleteCharAt(builder.lastIndexOf("&"));
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	private List<EvolutionDetail> extractEvolutionDetailsRecursive(List<EvolvesTo> evoTo, String thisPokemon, List<EvolutionDetail> result)
	{					
		for(EvolvesTo evo : evoTo)
		{
			if(result != null)
				return result; 
			
			if(evo.getSpecies().getName().equals(thisPokemon))
			{
				result = evo.getEvolutionDetails();
				return result;
			}
			
			if(!evo.getEvolvesTo().isEmpty())
				return extractEvolutionDetailsRecursive(evo.getEvolvesTo(), thisPokemon, result);
		}
		
		throw new IllegalArgumentException("Pokemon is not located in chain");
	}
	
	private ArrayList<String> formatEggGroup(PokemonSpecies speciesData)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(EggGroup group : speciesData.getEggGroups())
			result.add(TextFormatter.flexFormToProper(group.getName()));
		
		Collections.replaceAll(result, "No Eggs", "Undiscovered");
		return result;
	}
	
	private String formatGenderRatio(PokemonSpecies speciesData) 
	{
		int femaleInEights = speciesData.getGenderRate();	//The ratio of female in 8ths
		
		if(femaleInEights == -1)
			return "N";
		else if(femaleInEights == 8)
			return "F";
		else if(femaleInEights == 0)
			return "M";
		else
			return femaleInEights+"/8 ♀";
	}
	
	/**
	 * A method to get data from other endpoints relating to this Pokemon object. This data includes:
	 * *Evolution Data
	 * @param pokemon - the Pokemon object to get data about
	 * @return a List of the peripheral data wrapped in an Optional object
	 * @throws InterruptedException
	 * @throws PokeFlexException 
	 */
	private List<Object> getPeripheralData(PokemonSpecies speciesData) throws InterruptedException, PokeFlexException
	{
		List<Request> requests = new ArrayList<Request>();
		List<String> urlParameters = new ArrayList<String>();
		String[] urlComponents;
		
		//Evolution Chain data
		urlComponents = TextFormatter.getURLComponents(speciesData.getEvolutionChain().getUrl());
		urlParameters.add(urlComponents[6]);
		requests.add(new Request(Endpoint.EVOLUTION_CHAIN, urlParameters));
		
		return factory.createFlexObjects(requests);
	}
	
	/**
	 * A method to concurrently get the base data for a Pokemon.
	 * @param urlParameters - the list of parameters from the user - containing the name of the Pokemon
	 * @return A list of Flex objects containing the data
	 * @throws InterruptedException 
	 * @throws PokeFlexException 
	 */
	private List<Object> getBaseData(List<String> urlParameters) throws InterruptedException, PokeFlexException
	{
		List<Request> requests = new ArrayList<Request>();
		requests.add(new Request(Endpoint.POKEMON, urlParameters));
		requests.add(new Request(Endpoint.POKEMON_SPECIES, urlParameters));
		
		return factory.createFlexObjects(requests);
	}
	
	private int[] extractStats(Pokemon pokemon)
	{
		int[] stats = new int[6];
		
		for(int i = 0; i < 6; i++)
			stats[i] = pokemon.getStats().get(i).getBaseStat();
		
		return stats;
	}
	
	@Override
	public Response twitchReply(Input input) 
	{
		Response reply = new Response();
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Extract data from data base
		DatabaseInterface dbi = DatabaseInterface.getInstance();
		ComplexPokemon poke = dbi.extractComplexPokeFromDB(input.getArg(0).getDB());
		
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
