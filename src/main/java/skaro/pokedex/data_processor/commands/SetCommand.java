package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.input_processor.ArgumentSpec;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.arguments.GenArgument;
import skaro.pokedex.input_processor.arguments.MetaArgument;
import skaro.pokedex.input_processor.arguments.PokemonArgument;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;

public class SetCommand extends PokedexCommand 
{
	public SetCommand(PokedexServiceManager services, ResponseFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "set".intern();
		createHelpMessage("Gengar, OU, 4", "Pikachu, NU, 5", "Groudon, Uber, 6", "tapu lele, ou, 7");
	}
	
	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>, <meta>, <generation>"; }
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK, ServiceType.COLOR);
	}
	
	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild)
	{ 
		if(!input.allArgumentValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		String pokemonName = input.getArgument(0).getFlexForm();
		int generation = Integer.parseInt(input.getArgument(2).getDbForm());
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		
		Mono<MultiMap<IFlexObject>> result = Mono.just(new MultiMap<IFlexObject>())
			.flatMap(dataMap -> Flux.fromIterable(createRequests(pokemonName, generation))
					.parallel()
					.runOn(factory.getScheduler())
					.flatMap(request -> request.makeRequest(factory)
					.doOnNext(flexObject -> dataMap.add(flexObject.getClass().getName(), flexObject)))
					.sequential()
					.then(Mono.just(dataMap)))
			.flatMap(dataMap -> Mono.just(dataMap.getValue(Pokemon.class.getName(), 0))
					.ofType(Pokemon.class)
					.flatMap(pokemon -> this.addAdopter(pokemon, builder))
					.map(pokemon -> dataMap));
		
		this.addRandomExtraMessage(builder);
		return result.flatMap(dataMap -> Mono.fromCallable(() -> formatter.format(input, dataMap, builder)))
				.onErrorResume(error -> { error.printStackTrace(); return Mono.just(this.createErrorResponse(input, error)); });
	}
	
	@Override
	protected void createArgumentSpecifications()
	{
		argumentSpecifications.add(new ArgumentSpec(false, PokemonArgument.class));
		argumentSpecifications.add(new ArgumentSpec(false, MetaArgument.class));
		argumentSpecifications.add(new ArgumentSpec(false, GenArgument.class));
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
	
}
