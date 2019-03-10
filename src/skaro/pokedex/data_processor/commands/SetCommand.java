package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TextUtility;
import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.set.Ev;
import skaro.pokeflex.objects.set.Iv;
import skaro.pokeflex.objects.set.Set;
import skaro.pokeflex.objects.set.Set_;

public class SetCommand extends PokedexCommand 
{
	public SetCommand(IServiceManager services) throws ServiceConsumerException
	{
		super(services);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "set".intern();
		orderedArgumentCategories.add(ArgumentCategory.POKEMON);
		orderedArgumentCategories.add(ArgumentCategory.META);
		orderedArgumentCategories.add(ArgumentCategory.GEN);
		expectedArgRange = new ArgumentRange(3,3);
		
		createHelpMessage("Gengar, OU, 4", "Pikachu, NU, 5", "Groudon, Uber, 6", "tapu lele, ou, 7",
				"https://i.imgur.com/SWCCW3H.gif");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>, <meta>, <generation>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK, ServiceType.COLOR);
	}
	
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
					for(CommandArgument arg : input.getArguments())
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
	
	@Override
	public Mono<Response> respondTo(Input input, User requester)
	{ 
		Response response = new Response();

		if(!inputIsValid(response, input))
			return Mono.just(response);
		
		String tier = input.getArgument(1).getDbForm().toUpperCase();
		String pokemonName = input.getArgument(0).getFlexForm();
		int generation = Integer.parseInt(input.getArgument(2).getDbForm());
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		
		Mono<Response> result = Mono.just(new MultiMap<IFlexObject>())
			.flatMap(dataMap -> Flux.fromIterable(createRequests(pokemonName, generation))
					.parallel()
					.runOn(factory.getScheduler())
					.flatMap(request -> request.makeRequest(factory)
					.doOnNext(flexObject -> dataMap.add(flexObject.getClass().getName(), flexObject)))
					.sequential()
					.then(Mono.just(dataMap)))
			.flatMap(dataMap -> Mono.just(dataMap.getValue(Pokemon.class.getName(), 0))
					.ofType(Pokemon.class)
					.flatMap(pokemon -> Mono.just(dataMap.getValue(Set.class.getName(), 0))
							.ofType(Set.class)
							.doOnNext(sets -> formatHeader(response, pokemon, sets, tier, generation))
							.flatMap(sets -> formatEmbed(pokemon, sets, tier))
							.doOnNext(embedBuilder -> response.setEmbed(embedBuilder))))
			.then(Mono.just(response));
		
		return result
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
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
	
	private Mono<EmbedCreateSpec> formatEmbed(Pokemon pokemon, Set sets, String tier)
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		EmbedCreateSpec builder = new EmbedCreateSpec();
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
		
		this.addRandomExtraMessage(builder);		
		return this.addAdopter(pokemon, builder)
				.then(Mono.just(builder));
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
