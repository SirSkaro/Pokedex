package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.core.IServiceManager;
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
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon.Type;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class WeakCommand extends AbstractCommand 
{
	public WeakCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "weak".intern();
		argCats = new ArrayList<ArgumentCategory>();
		argCats.add(ArgumentCategory.POKE_TYPE_LIST);
		expectedArgRange = new ArgumentRange(1,2);
		
		aliases.put("weakness", Language.ENGLISH);
		aliases.put("debilidad", Language.SPANISH);
		aliases.put("faiblesses", Language.FRENCH);
		aliases.put("debole", Language.ITALIAN);
		aliases.put("schwach", Language.GERMAN);
		aliases.put("yowai", Language.JAPANESE_HIR_KAT);
		aliases.put("ruò", Language.CHINESE_SIMPMLIFIED);
		aliases.put("ruo", Language.CHINESE_SIMPMLIFIED);
		aliases.put("yagjeom", Language.KOREAN);
		
		aliases.put("弱い", Language.JAPANESE_HIR_KAT);
		aliases.put("弱", Language.CHINESE_SIMPMLIFIED);
		aliases.put("약점", Language.KOREAN);
		
		extraMessages.add("You may also like the %coverage command");
		
		createHelpMessage("Ghost, Normal", "Scizor", "Swampert", "Fairy",
				"https://i.imgur.com/E79RCZO.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<pokemon> or <type> or <type>, <type>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK, ServiceType.TYPE);
	}
	
	@Override
	public Mono<Response> discordReply(Input input, User requester)
	{ 
		if(!input.isValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result = Mono.just(new MultiMap<IFlexObject>());
		
		try
		{
			PokeFlexFactory factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			
			if(input.getArg(0).getCategory() == ArgumentCategory.POKEMON) //argument is a Pokemon
			{	
				String pokemonName = input.getArg(0).getFlexForm();
				Request pokemonRequest = new Request(Endpoint.POKEMON, pokemonName);
				result = result.flatMap(dataMap -> pokemonRequest.makeRequest(factory)
							.ofType(Pokemon.class)
							.flatMap(pokemon -> this.addAdopter(pokemon, builder))
							.doOnNext(pokemon -> {
								dataMap.put(Pokemon.class.getName(), pokemon);
								dataMap.put(TypeEfficacyWrapper.class.getName(), createWrapper(pokemon.getTypes()));
							})
							.map(pokemon -> new Request(Endpoint.POKEMON_SPECIES, pokemon.getSpecies().getName()))
							.flatMap(speciesRequest -> speciesRequest.makeRequest(factory))
							.doOnNext(species -> dataMap.put(PokemonSpecies.class.getName(), species))
							.then(Mono.just(dataMap)));
			}
			else //data is a list of types
			{
				result = result.doOnNext(dataMap -> dataMap.put(TypeEfficacyWrapper.class.getName(), createWrapperFromArguments(input.getArgs())));
			}
			
			this.addRandomExtraMessage(builder);
			return result.map(dataMap -> formatter.format(input, dataMap, builder));
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1006", e); 
			return Mono.just(response);
		}
	}
	
	private TypeEfficacyWrapper createWrapper(List<Type> types)
	{
		TypeService typeService = (TypeService)services.getService(ServiceType.TYPE);
		List<String> typeNames = types.stream()
				.map(type -> type.getType().getName())
				.collect(Collectors.toList());
		
		return typeService.getEfficacyOnDefense(typeNames);
	}
	
	private TypeEfficacyWrapper createWrapperFromArguments(List<AbstractArgument> types)
	{
		TypeService typeService = (TypeService)services.getService(ServiceType.TYPE);
		List<String> typeNames = types.stream()
				.map(arg -> arg.getFlexForm())
				.collect(Collectors.toList());
		
		return typeService.getEfficacyOnDefense(typeNames);
	}
}