package skaro.pokedex.data_processor.commands;

import java.util.List;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.PokeFlexService;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeEfficacyWrapper;
import skaro.pokedex.data_processor.TypeService;
import skaro.pokedex.input_processor.AbstractArgument;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.move.Move;

public class CoverageCommand extends AbstractCommand 
{
	public CoverageCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "coverage".intern();
		argCats.add(ArgumentCategory.MOVE_TYPE_LIST);
		expectedArgRange = new ArgumentRange(1,4);
		aliases.put("strong", Language.ENGLISH);
		aliases.put("cov", Language.ENGLISH);
		aliases.put("effective", Language.ENGLISH);
		aliases.put("yuhyohan", Language.KOREAN);
		aliases.put("eficaz", Language.SPANISH);
		aliases.put("efficace", Language.FRENCH);
		aliases.put("forte", Language.ITALIAN);
		aliases.put("yǒuxiào", Language.CHINESE_SIMPMLIFIED);
		aliases.put("youxiao", Language.CHINESE_SIMPMLIFIED);
		aliases.put("efekuto", Language.JAPANESE_HIR_KAT);
		aliases.put("wirksam", Language.GERMAN);
		
		aliases.put("有效", Language.CHINESE_SIMPMLIFIED);
		aliases.put("エフェクト", Language.JAPANESE_HIR_KAT);
		aliases.put("유효한", Language.KOREAN);
		
		extraMessages.add("You may also like the %weak command!");
		
		createHelpMessage("ice, electric", "blizzard, thunder", "Ghost, Fire, Vine Whip, Hyper Beam", "Water",
				"https://i.imgur.com/MLIpXYN.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<type/move>,...,<type/move>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.TYPE);
	}
	
	@Override
	public Mono<Response> discordReply(Input input, User requester)
	{ 
		if(!input.isValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result = Mono.just(new MultiMap<IFlexObject>());
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		
		result = result
				.flatMap(dataMap -> Flux.fromIterable(input.getArgs())
				.parallel()
				.runOn(factory.getScheduler())
				.flatMap(userArgument -> getTypeFromArgument(userArgument))
				.sequential()
				.collectList()
				.map(typeNames -> createWrapper(typeNames))
				.doOnNext(typeWrapper -> dataMap.put(TypeEfficacyWrapper.class.getName(), typeWrapper))
				.then(Mono.just(dataMap)));
		
		this.addRandomExtraMessage(builder);
		return result
				.map(dataMap -> formatter.format(input, dataMap, builder))
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
	}
	
	private Mono<String> getTypeFromArgument(AbstractArgument argument)
	{
		Mono<String> result;
		
		if(argument.getCategory() == ArgumentCategory.MOVE)
		{
			PokeFlexFactory factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			
			result = Mono.just(argument)
					.map(moveArgument -> new Request(Endpoint.MOVE, moveArgument.getFlexForm()))
					.flatMap(request -> request.makeRequest(factory))
					.ofType(Move.class)
					.map(move -> move.getType().getName());
		}
		else
		{
			result = Mono.just(argument)
					.map(typeArgument -> typeArgument.getFlexForm());
		}
		
		return result;
	}
	
	private TypeEfficacyWrapper createWrapper(List<String> types)
	{
		TypeService typeService = (TypeService)services.getService(ServiceType.TYPE);
		return typeService.getEfficacyOnOffense(types);
	}
	
}