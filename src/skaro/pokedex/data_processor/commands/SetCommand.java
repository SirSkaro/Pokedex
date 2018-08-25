package skaro.pokedex.data_processor.commands;

import java.util.Optional;

import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.AbstractArgument;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.objects.set.Evs;
import skaro.pokeflex.objects.set.Ivs;
import skaro.pokeflex.objects.set.Set;
import skaro.pokeflex.objects.set.Set_;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class SetCommand extends AbstractCommand 
{
	public SetCommand(PokeFlexFactory pff)
	{
		super(pff);
		commandName = "set".intern();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.META);
		argCats.add(ArgumentCategory.GEN);
		expectedArgRange = new ArgumentRange(3,3);
		
		createHelpMessage("Gengar, OU, 6", "Pikachu, NU, 5", "Groudon, Uber, 6", "zapdos, ou, 1",
				"https://i.imgur.com/SWCCW3H.gif");
	}
	
	public boolean makesWebRequest() { return false; }
	public String getArguments() { return "<pokemon>, <meta>, <generation> (not updated for gen 7)"; }
	
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
					reply.addToReply("\n*top suggestion*: Only Smogon and VGC metas are supported, and not updated for gen 7. "
							+ "Try an official tier or gens 1-6?");
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
		
		//Check if input is valid
		if(!inputIsValid(reply, input))
			return reply;
		
		//Obtain data
		try 
		{
			Object flexObj = factory.createFlexObject(Endpoint.SET, input.argsAsList());
			Set sets = Set.class.cast(flexObj);
			
			//Format reply
			reply.addToReply(("__**"+sets.getTier()+"** sets for **"+sets.getName()+"** from Generation **"+sets.getGen()+"**__").intern());
			reply.setEmbededReply(formatEmbed(sets));
		} 
		catch (Exception e) { this.addErrorMessage(reply, input, "1007", e); }
		
		return reply;
	}
	
	private EmbedObject formatEmbed(Set sets)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setLenient(true);
		
		for(Set_ set : sets.getSets())
			builder.appendField(set.getTitle(), setToString(sets.getName(), set), true);
		
		builder.appendField("More Info", "[Smogon's "+sets.getName()+" Analysis]("+sets.getUrl()+")", true);
		this.addRandomExtraMessage(builder);
		
		return builder.build();
	}
	
	private String setToString(String name, Set_ set)
	{
		StringBuilder builder = new StringBuilder();
		Optional<String> evs = evsToString(set.getEvs());
		Optional<String> ivs = ivsToString(set.getIvs());
		
		builder.append(name);
		if(set.getItem() != null)
			builder.append(" @ "+set.getItem());
		
		if(set.getAbility() != null)
			builder.append("\nAbility: "+set.getAbility());
		
		if(evs.isPresent())
			builder.append("\nEVs: "+evs.get());
		
		if(set.getNature() != null)
			builder.append("\n"+ set.getNature() +" Nature");
		
		if(ivs.isPresent())
			builder.append("\nIVs: "+ivs.get());
		
		for(String move : set.getMoves())
			builder.append("\n- "+move);
		
		
		return builder.toString();
	}
	
	private Optional<String> evsToString(Evs evs)
	{
		StringBuilder builder = new StringBuilder();
		
		if(evs.getHp() != 0)
			builder.append(evs.getHp() + " HP/ ");
		if(evs.getAtk() != 0)
			builder.append(evs.getAtk() + " Atk/ ");
		if(evs.getDef() != 0)
			builder.append(evs.getDef() + " Def/ ");
		if(evs.getSpatk() != 0)
			builder.append(evs.getSpatk() + " SpA/ ");
		if(evs.getSpdef() != 0)
			builder.append(evs.getSpdef() + " SpD/ ");
		if(evs.getSpd() != 0)
			builder.append(evs.getSpd() + " Spe/ ");
		
		if(builder.length() == 0)
			return Optional.empty();
		
		return Optional.of(builder.substring(0, builder.length() - 2));
	}
	
	private Optional<String> ivsToString(Ivs ivs)
	{
		StringBuilder builder = new StringBuilder();
		
		if(ivs.getHp() == 0 && ivs.getAtk() == 0
				&& ivs.getDef() == 0 && ivs.getSpatk() == 0
				&& ivs.getSpdef() == 0 && ivs.getSpd() == 0)
			return Optional.empty();
		
		if(ivs.getHp() != 31)
			builder.append(ivs.getHp() + " HP/ ");
		if(ivs.getAtk() != 31)
			builder.append(ivs.getAtk() + " Atk/ ");
		if(ivs.getDef() != 31)
			builder.append(ivs.getDef() + " Def/ ");
		if(ivs.getSpatk() != 31)
			builder.append(ivs.getSpatk() + " SpA/ ");
		if(ivs.getSpdef() != 31)
			builder.append(ivs.getSpdef() + " SpD/ ");
		if(ivs.getSpd() != 31)
			builder.append(ivs.getSpd() + " Spe/ ");
		
		if(builder.length() == 0)
			return Optional.empty();
		
		return Optional.of(builder.substring(0, builder.length() - 2));
	}
}
