package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import skaro.pokedex.core.PerkChecker;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.ColorTracker;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.set.Ev;
import skaro.pokeflex.objects.set.Iv;
import skaro.pokeflex.objects.set.Set;
import skaro.pokeflex.objects.set.Set_;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class SetCommand extends AbstractCommand 
{
	public SetCommand(PokeFlexFactory pff, PerkChecker pc)
	{
		super(pff, pc);
		commandName = "set".intern();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.META);
		argCats.add(ArgumentCategory.GEN);
		expectedArgRange = new ArgumentRange(3,3);
		
		createHelpMessage("Gengar, OU, 4", "Pikachu, NU, 5", "Groudon, Uber, 6", "tapu lele, ou, 7",
				"https://i.imgur.com/SWCCW3H.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>, <meta>, <generation>"; }
	
	public boolean inputIsValid(Response reply, Input input)
	{
		if(!input.isValid())
		{
			switch(input.getError())
			{
				case ARGUMENT_NUMBER:
					reply.addToReply("You must specify a Pokemon, a Meta, and a Generation as input for this command "
							+ "(seperated by commas).");
				break;
				case INVALID_ARGUMENT:
					reply.addToReply("Could not process your request due to the following problem(s):".intern());
					for(AbstractArgument arg : input.getArgs())
						if(!arg.isValid())
							reply.addToReply("\t\""+arg.getRawInput()+"\" is not a recognized "+ arg.getCategory());
					reply.addToReply("\n*top suggestion*: Only Smogon metas are supported."
							+ "Try an official tier (Uber, OU, UU, RU, NU, PU, LC)");
				break;
				default:
					reply.addToReply("A technical error occured (code 109)");
			}
			
			return false;
		}
		
		return true;
	}
	
	public Response discordReply(Input input, IUser requester)
	{ 
		Response reply = new Response();
		String tier, pokemon;
		int gen;
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		tier = input.getArg(1).getDbForm().toUpperCase();
		pokemon = input.getArg(0).getFlexForm();
		
		//Obtain data
		try 
		{
			gen = Integer.parseInt(input.getArg(2).getDbForm());
			
			List<Object> flexObj = factory.createFlexObjects(createRequests(pokemon, gen));
			Set sets = Set.class.cast(flexObj.get(0) instanceof Set ? flexObj.get(0) : flexObj.get(1));
			Pokemon pokemonData = Pokemon.class.cast(flexObj.get(0) instanceof Pokemon ? flexObj.get(0) : flexObj.get(1));
			
			//Format reply
			if(!sets.getSets().isEmpty())
			{
				reply.addToReply(("__**"+tier+
						"** sets for **"+TextFormatter.flexFormToProper(pokemon)+
						"** from Generation **"+gen+"**__").intern());
				reply.setEmbededReply(formatEmbed(pokemonData, sets, tier));
			}
			else
				reply.addToReply("Smogon doesn't have any sets for " +TextFormatter.flexFormToProper(pokemon)+ " in generation " + gen);
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1007", e);}
		
		return reply;
	}
	
	private List<PokeFlexRequest> createRequests(String pokemon, int gen)
	{
		List<PokeFlexRequest> result = new ArrayList<PokeFlexRequest>();
		Request request = new Request(Endpoint.SET);
		request.addParam(String.valueOf(gen));
		request.addParam(pokemon.replace("-", "_"));
		result.add(request);
		
		request = new Request(Endpoint.POKEMON);
		request.addParam(pokemon);
		result.add(request);
		
		return result;
	}
	
	private EmbedObject formatEmbed(Pokemon pokemon, Set sets, String tier)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		for(Set_ set : sets.getSets())
		{
			if(set.getFormat().equalsIgnoreCase(tier))
				builder.appendField(set.getName(), setToString(pokemon.getName(), set), true);
		}
		
		if(builder.getFieldCount() == 0)
		{
			builder.withTitle(TextFormatter.flexFormToProper(pokemon.getName()) + " doesn't have any sets in " + tier +" for this generation");
			builder.withDescription("Try another tier. The link below has an exhaustive list");
		}
		
		builder.appendField("Learn more", "[Smogon Analysis]("+sets.getUrl()+")", false);
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.withColor(ColorTracker.getColorForType(type));
		
		//Set thumbnail
		builder.withThumbnail(pokemon.getSprites().getBackDefault());
		
		//Add adopter
		this.addAdopter(pokemon, builder);
		this.addRandomExtraMessage(builder);
		
		return builder.build();
	}
	
	private String setToString(String name, Set_ set)
	{
		StringBuilder builder = new StringBuilder();
		Optional<String> evs = evsToString(set.getEvs());
		Optional<String> ivs = ivsToString(set.getIvs());
		
		builder.append(TextFormatter.flexFormToProper(name));
		if(set.getItems() != null && !set.getItems().isEmpty())
			builder.append(" @ "+set.getItems().get(0));
		
		if(set.getAbilities() != null && !set.getAbilities().isEmpty())
			builder.append("\nAbility: "+set.getAbilities().get(0));
		
		if(evs.isPresent())
			builder.append("\nEVs: "+evs.get());
		
		if(set.getNatures() != null && !set.getNatures().isEmpty())
			builder.append("\n"+ set.getNatures().get(0) +" Nature");
		
		if(ivs.isPresent())
			builder.append("\nIVs: "+ivs.get());
		
		if(set.getMoves() != null && !set.getMoves().isEmpty())
			for(List<String> moves : set.getMoves())
				builder.append("\n- "+moves.get(0));
		
		return builder.toString();
	}
	
	private Optional<String> evsToString(List<Ev> evList)
	{
		if(evList == null || evList.isEmpty())
			return Optional.empty();
		
		StringBuilder builder = new StringBuilder();
		Ev evs = evList.get(0);
		
		if(evs.getHp() != 0)
			builder.append(evs.getHp() + " HP/ ");
		if(evs.getAtk() != 0)
			builder.append(evs.getAtk() + " Atk/ ");
		if(evs.getDef() != 0)
			builder.append(evs.getDef() + " Def/ ");
		if(evs.getSpa() != 0)
			builder.append(evs.getSpa() + " SpA/ ");
		if(evs.getSpd() != 0)
			builder.append(evs.getSpd() + " SpD/ ");
		if(evs.getSpe() != 0)
			builder.append(evs.getSpe() + " Spe/ ");
		
		if(builder.length() == 0)
			return Optional.empty();
		
		return Optional.of(builder.substring(0, builder.length() - 2));
	}
	
	private Optional<String> ivsToString(List<Iv> ivList)
	{
		if(ivList == null || ivList.isEmpty())
			return Optional.empty();
		
		StringBuilder builder = new StringBuilder();
		Iv ivs = ivList.get(0);
		
		if(ivs.getHp() == 0 && ivs.getAtk() == 0
				&& ivs.getDef() == 0 && ivs.getSpa() == 0
				&& ivs.getSpd() == 0 && ivs.getSpd() == 0)
			return Optional.empty();
		
		if(ivs.getHp() != 31)
			builder.append(ivs.getHp() + " HP/ ");
		if(ivs.getAtk() != 31)
			builder.append(ivs.getAtk() + " Atk/ ");
		if(ivs.getDef() != 31)
			builder.append(ivs.getDef() + " Def/ ");
		if(ivs.getSpa() != 31)
			builder.append(ivs.getSpa() + " SpA/ ");
		if(ivs.getSpd() != 31)
			builder.append(ivs.getSpd() + " SpD/ ");
		if(ivs.getSpe() != 31)
			builder.append(ivs.getSpe() + " Spe/ ");
		
		if(builder.length() == 0)
			return Optional.empty();
		
		return Optional.of(builder.substring(0, builder.length() - 2));
	}
}
