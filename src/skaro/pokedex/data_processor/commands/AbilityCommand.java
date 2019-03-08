package skaro.pokedex.data_processor.commands;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.pokemon.Pokemon;

public class AbilityCommand extends PokedexCommand 
{	
	public AbilityCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");

		commandName = "ability".intern();
		orderedArgumentCategories.add(ArgumentCategory.POKE_ABIL);
		expectedArgRange = new ArgumentRange(1,1);

		aliases.put("ab", Language.ENGLISH);
		aliases.put("abil", Language.ENGLISH);
		aliases.put("habilidad", Language.SPANISH);
		aliases.put("habil", Language.SPANISH);
		aliases.put("talents", Language.FRENCH);
		aliases.put("talent", Language.FRENCH);
		aliases.put("abilità", Language.ITALIAN);
		aliases.put("abilita", Language.ITALIAN);
		aliases.put("fähigkeiten", Language.GERMAN);
		aliases.put("fahigkeiten", Language.GERMAN);
		aliases.put("tokusei", Language.JAPANESE_HIR_KAT);
		aliases.put("toku", Language.JAPANESE_HIR_KAT);
		aliases.put("tèxìng", Language.CHINESE_SIMPMLIFIED);
		aliases.put("texing", Language.CHINESE_SIMPMLIFIED);
		aliases.put("teugseong", Language.KOREAN);

		aliases.put("特性", Language.JAPANESE_HIR_KAT);
		aliases.put("특성", Language.KOREAN);
		aliases.put("特技", Language.CHINESE_SIMPMLIFIED);

		createHelpMessage("Starmie", "Flash Fire", "celebi", "natural cure",
				"https://i.imgur.com/biWBKIL.gif");
	}

	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<pokemon> or <ability>"; }

	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK);
	}

	@Override
	public Mono<Response> prepareResponse(Input input, User requester)
	{
		if(!input.isValid())
			return Mono.just(formatter.invalidInputResponse(input));

		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result;
		String userInput = input.getArgument(0).getFlexForm();

		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);

		if(input.getArgument(0).getCategory() == ArgumentCategory.ABILITY)
		{
			Request request = new Request(Endpoint.ABILITY, userInput);
			result = Mono.just(new MultiMap<IFlexObject>())
					.flatMap(dataMap -> request.makeRequest(factory)
							.doOnNext(abil -> dataMap.put(Ability.class.getName(), abil))
							.then(Mono.just(dataMap)));
		}
		else
		{
			Request request = new Request(Endpoint.POKEMON, userInput);
			result = Mono.just(new MultiMap<IFlexObject>())
					.flatMap(dataMap -> request.makeRequest(factory)
							.ofType(Pokemon.class)
							.flatMap(pokemon -> this.addAdopter(pokemon, builder))
							.doOnNext(pokemon -> dataMap.put(Pokemon.class.getName(), pokemon))
							.flatMap(pokemon -> Flux.fromIterable(pokemon.getAbilities())
									.map(ability -> new RequestURL(ability.getAbility().getUrl(), Endpoint.ABILITY)) 
									.ofType(PokeFlexRequest.class)
									.concatWithValues(new Request(Endpoint.POKEMON_SPECIES, pokemon.getSpecies().getName()))
									.parallel()
									.runOn(factory.getScheduler())
									.flatMap(concurrentRequest -> concurrentRequest.makeRequest(factory))
									.doOnNext(flexObject -> dataMap.add(flexObject.getClass().getName(), flexObject))
									.sequential()
									.then(Mono.just(dataMap))));
		}

		this.addRandomExtraMessage(builder);
		return result
				.map(dataMap -> formatter.format(input, dataMap, builder))
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
	}

}