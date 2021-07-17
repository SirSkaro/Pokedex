package skaro.pokedex.data_processor.formatters;

import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.spec.EmbedCreateSpec;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.PokedexServiceConsumer;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.set.Ev;
import skaro.pokeflex.objects.set.Iv;
import skaro.pokeflex.objects.set.Move;
import skaro.pokeflex.objects.set.Set;
import skaro.pokeflex.objects.set.Set_;

public class SetResponseFormatter implements ResponseFormatter, PokedexServiceConsumer
{
	private PokedexServiceManager services;
	
	public SetResponseFormatter(PokedexServiceManager services) throws ServiceConsumerException
	{
		if(!hasExpectedServices(services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		this.services = services;
	}

	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return services.hasServices(ServiceType.COLOR);
	}
	
	@Override
	public Response format(Input input, MultiMap<IFlexObject> data, EmbedCreateSpec builder)
	{
		Response response = new Response();
		Pokemon pokemon = (Pokemon)data.getValue(Pokemon.class.getName(), 0);
		Set sets = (Set)data.getValue(Set.class.getName(), 0);
		String tier = input.getArgument(1).getDbForm().toUpperCase();
		int generation = Integer.parseInt(input.getArgument(2).getDbForm());
		
		formatHeader(response, pokemon, sets, tier, generation);
		formatEmbed(builder, pokemon, sets, tier);
		response.setEmbed(builder);
		return response;
	}
	
	private void formatHeader(Response response, Pokemon pokemon, Set sets, String tier, int generation)
	{
		if(!sets.getSets().isEmpty())
		{
			response.addToReply(("__**"+tier+
					"** sets for **"+TextUtility.flexFormToProper(pokemon.getName())+
					"** from Generation **"+generation+"**__").intern());
		}
		else
			response.addToReply("Smogon doesn't have any sets for " +TextUtility.flexFormToProper(pokemon.getName())+ " in generation " + generation);
	}
	
	private void formatEmbed(EmbedCreateSpec builder, Pokemon pokemon, Set sets, String tier)
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		boolean hasAtLeastOneSet = false;
		
		for(Set_ set : sets.getSets())
		{
			if(set.getFormat().equalsIgnoreCase(tier))
			{
				builder.addField(set.getName(), setToString(pokemon.getName(), set), true);
				hasAtLeastOneSet = true;
			}
		}
		
		if(!hasAtLeastOneSet)
		{
			builder.setTitle(TextUtility.flexFormToProper(pokemon.getName()) + " doesn't have any sets in " + tier +" for this generation");
			builder.setDescription("Try another tier. The link below has an exhaustive list");
		}
		
		builder.addField("Learn more", "[Smogon Analysis]("+sets.getUrl()+")", false);
		
		//Set embed color
		String type = pokemon.getTypes().get(pokemon.getTypes().size() - 1).getType().getName(); //Last type in the list
		builder.setColor(colorService.getColorForType(type));
		
		//Set thumbnail
		builder.setThumbnail(pokemon.getSprites().getBackDefault());
	}
	
	private String setToString(String name, Set_ set)
	{
		StringBuilder builder = new StringBuilder();
		Optional<String> evs = evsToString(set.getEvs());
		Optional<String> ivs = ivsToString(set.getIvs());
		
		builder.append(TextUtility.flexFormToProper(name));
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
			for(List<Move> moves : set.getMoves())
				builder.append("\n- "+moves.get(0).getMove());
		
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
