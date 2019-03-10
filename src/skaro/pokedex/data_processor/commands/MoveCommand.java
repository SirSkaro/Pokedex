package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.FlexCacheService;
import skaro.pokedex.services.FlexCacheService.CachedResource;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.type.Type;

public class MoveCommand extends PokedexCommand 
{
	public MoveCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "move".intern();
		orderedArgumentCategories.add(ArgumentCategory.MOVE);
		expectedArgRange = new ArgumentRange(1,1);
		
		aliases.put("mv", Language.ENGLISH);
		aliases.put("moves", Language.ENGLISH);
		aliases.put("attack", Language.ENGLISH);
		aliases.put("attacke", Language.GERMAN);
		aliases.put("movimiento", Language.SPANISH);
		aliases.put("capacite", Language.FRENCH);
		aliases.put("capacité", Language.FRENCH);
		aliases.put("attaque", Language.FRENCH);
		aliases.put("mossa", Language.ITALIAN);
		aliases.put("waza", Language.JAPANESE_HIR_KAT);
		aliases.put("zhāoshì", Language.CHINESE_SIMPMLIFIED);
		aliases.put("zhaoshi", Language.CHINESE_SIMPMLIFIED);
		aliases.put("gisul", Language.KOREAN);
		
		aliases.put("わざ", Language.JAPANESE_HIR_KAT);
		aliases.put("招式", Language.CHINESE_SIMPMLIFIED);
		aliases.put("기술", Language.KOREAN);
		
		createHelpMessage("Ember", "dragon ascent", "aeroblast", "Blast Burn",
				"https://i.imgur.com/B3VtWyg.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<move>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.CACHE);
	}
	
	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild)
	{
		if(!input.isValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result;
		String moveName = input.getArgument(0).getFlexForm();
		
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		FlexCacheService flexCache = (FlexCacheService)services.getService(ServiceType.CACHE);
		TypeData cachedTypeData = (TypeData)flexCache.getCachedData(CachedResource.TYPE);
		Request initialRequest = new Request(Endpoint.MOVE, moveName);
		
		result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> initialRequest.makeRequest(factory)
					.ofType(Move.class)
					.doOnNext(move -> {
						dataMap.put(Move.class.getName(), move);
						dataMap.put(Type.class.getName(), cachedTypeData.getByName(move.getType().getName()));
					})
					.flatMap(move -> Flux.just(createPeripheralRequests(move))
							.parallel()
							.runOn(factory.getScheduler())
							.flatMap(request -> request.makeRequest(factory))
							.doOnNext(flexObject -> dataMap.put(flexObject.getClass().getName(), flexObject))
							.sequential()
							.then(Mono.just(dataMap))));
		
		this.addRandomExtraMessage(builder);
		return result
				.map(dataMap -> formatter.format(input, dataMap, builder))
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
	}
	
	private RequestURL[] createPeripheralRequests(Move move)
	{
		List<RequestURL> result = new ArrayList<>();
		result.add(new RequestURL(move.getDamageClass().getUrl(), Endpoint.MOVE_DAMAGE_CLASS));
		result.add(new RequestURL(move.getTarget().getUrl(), Endpoint.MOVE_TARGET));
		
		if(move.getContestType() != null)
			result.add(new RequestURL(move.getContestType().getUrl(), Endpoint.CONTEST_TYPE));
		
		
		return result.toArray(new RequestURL[result.size()]);
	}
	
}
