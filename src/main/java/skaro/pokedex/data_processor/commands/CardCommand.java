package skaro.pokedex.data_processor.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.URIUtil;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.input_processor.ArgumentSpec;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.AnyArgument;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestQuery;

public class CardCommand extends PokedexCommand {
	private Pattern cardIdPattern;
	
	public CardCommand(PokedexServiceManager services, ResponseFormatter formatter) throws ServiceConsumerException {
		super(services, formatter);
		if(!hasExpectedServices(this.services)) {
			throw new ServiceConsumerException("Did not receive all necessary services");
		}
		
		cardIdPattern = Pattern.compile("[a-zA-Z]+[0-9]+-[0-9A-Za-z]+");
		
		commandName = "card";
		
		aliases.put("cards", Language.ENGLISH);
		aliases.put("tcg", Language.ENGLISH);
		
		createHelpMessage("charizard", "det1-10", "Double Energy", "xy5-148");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<card name>"; }
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) {
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX);
	}

	@Override
	public Mono<Response> respondTo(Input input, User author, Guild guild) {
		if(!input.allArgumentValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result;
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		
		PokeFlexRequest request = createFlexRequest(input.getArgument(0).getRawInput());
		
		result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> request.makeRequest(factory)
						.doOnNext(cardData -> dataMap.add(cardData.getClass().getName(), cardData))
						.then(Mono.just(dataMap)));
		
		return result.flatMap(dataMap -> Mono.fromCallable(() -> formatter.format(input, dataMap, builder)))
				.onErrorResume(error -> { error.printStackTrace(); return Mono.just(this.createErrorResponse(input, error));});
	}

	private PokeFlexRequest createFlexRequest(String rawArgument) {
		Matcher matcher = cardIdPattern.matcher(rawArgument);
		if(matcher.matches()) 
			return new Request(Endpoint.CARD, rawArgument);
		
		String queryArguments = String.format("name:\"%s\"", rawArgument);
		return new RequestQuery(Endpoint.CARDS, "q", URIUtil.encodePath(queryArguments));
	}

	@Override
	protected void createArgumentSpecifications() {
		argumentSpecifications.add(new ArgumentSpec(false, AnyArgument.class));
	}

}
