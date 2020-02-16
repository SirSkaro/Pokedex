package skaro.pokedex.data_processor.commands;

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
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.NatureArgument;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.berry_flavor.BerryFlavor;
import skaro.pokeflex.objects.nature.Nature;

public class NatureCommand extends PokedexCommand {

	public NatureCommand(PokedexServiceManager serviceManager, ResponseFormatter discordFormatter) throws ServiceConsumerException {
		super(serviceManager, discordFormatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "nature";
		
		aliases.put("wesen", Language.GERMAN);
		aliases.put("natura", Language.ITALIAN);
		aliases.put("seonggyeok", Language.KOREAN);
		aliases.put("naturaleza", Language.SPANISH);
		aliases.put("xìnggé", Language.CHINESE_SIMPMLIFIED);
		aliases.put("xingge", Language.CHINESE_SIMPMLIFIED);
		aliases.put("seikaku", Language.JAPANESE_HIR_KAT);
		aliases.put("lanature", Language.FRENCH);
		
		aliases.put("성격", Language.KOREAN);
		aliases.put("性格", Language.JAPANESE_HIR_KAT);
		
		createHelpMessage("Timid", "jolly", "Hardy", "calm");
	}

	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<nature>";}
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX);
	}

	@Override
	public Mono<Response> respondTo(Input input, User author, Guild guild) 
	{
		if(!input.allArgumentValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result;
		String userInput = input.getArgument(0).getFlexForm();
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		
		Request request = new Request(Endpoint.NATURE, userInput);
		result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> request.makeRequest(factory)
						.ofType(Nature.class)
						.doOnNext(nature -> dataMap.put(Nature.class.getName(), nature))
						.flatMap(nature -> getFlavorPreferences(nature)
								.doOnNext(berry -> dataMap.add(BerryFlavor.class.getName(), berry))
								.then(Mono.just(dataMap)))
						.switchIfEmpty(Mono.just(dataMap)));
		
		this.addRandomExtraMessage(builder);
		return result
				.map(dataMap -> formatter.format(input, dataMap, builder))
				.onErrorResume(error -> { error.printStackTrace(); return Mono.just(this.createErrorResponse(input, error)); });
	}
	
	private Flux<IFlexObject> getFlavorPreferences(Nature nature) {
		if(nature.getLikesFlavor() == null)
			return Flux.empty();
		
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		PokeFlexRequest flavorLikes = new RequestURL(nature.getLikesFlavor().getUrl(), Endpoint.BERRY_FLAVOR);
		PokeFlexRequest flavorHates = new RequestURL(nature.getHatesFlavor().getUrl(), Endpoint.BERRY_FLAVOR);
		
		return Flux.just(flavorLikes, flavorHates)
				.flatMap(request -> request.makeRequest(factory));
	}

	@Override
	protected void createArgumentSpecifications() 
	{
		argumentSpecifications.add(new ArgumentSpec(false, NatureArgument.class));
	}

}
