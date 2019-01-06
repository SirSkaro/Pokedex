package skaro.pokedex.data_processor.commands;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokedex.core.IServiceManager;
import skaro.pokedex.core.ServiceConsumerException;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.AbstractCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;

public class DexCommand extends AbstractCommand
{
	public DexCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "dex".intern();
		argCats.add(ArgumentCategory.POKEMON);
		argCats.add(ArgumentCategory.VERSION);
		expectedArgRange = new ArgumentRange(2,2);
		
		aliases.put("pokedex", Language.ENGLISH);
		aliases.put("entry", Language.ENGLISH);
		aliases.put("giib", Language.KOREAN);
		aliases.put("entrada", Language.SPANISH);
		aliases.put("iscrizione", Language.ITALIAN);
		aliases.put("eintrag", Language.GERMAN);
		aliases.put("entrée", Language.FRENCH);
		aliases.put("entree", Language.FRENCH);
		aliases.put("tiáomù", Language.CHINESE_SIMPMLIFIED);
		aliases.put("tiaomu", Language.CHINESE_SIMPMLIFIED);
		aliases.put("entori", Language.JAPANESE_HIR_KAT);
		
		aliases.put("条目", Language.CHINESE_SIMPMLIFIED);
		aliases.put("エントリ", Language.JAPANESE_HIR_KAT);
		aliases.put("기입", Language.KOREAN);
		
		extraMessages.add("Connect to a voice channel to hear entries spoken! (English, German, Italian, and French only)");
		
		createHelpMessage("Mew, Red", "kadabra, fire red", "Phantump, y", "Darumaka, white",
				"https://i.imgur.com/AvJMBpR.gif");
		
	}
	
	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<pokemon>, <version>"; }
	
	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK);
	}
	
	@Override
	public Mono<Response> discordReply(Input input, User requester)
	{
		if(!input.isValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		PokeFlexFactory factory;
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result;
		
		//Obtain data
		try
		{
			String pokemonName = input.getArg(0).getFlexForm();
			String versionName = input.getArg(1).getFlexForm();
			Request request = new Request(Endpoint.POKEMON, pokemonName);
			factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			
			result = Mono.just(new MultiMap<IFlexObject>())
						.flatMap(dataMap -> request.makeRequest(factory)//request Pokemon
							.ofType(Pokemon.class)
							.flatMap(pokemon -> this.addAdopter(pokemon, builder))
							.doOnNext(pokemon -> dataMap.put(Pokemon.class.getName(), pokemon))
							.flatMap(pokemon -> Flux.just(new Request(Endpoint.POKEMON_SPECIES, pokemon.getSpecies().getName()))
									.concatWithValues(new Request(Endpoint.VERSION, versionName))
									.flatMap(concurrentRequest -> concurrentRequest.makeRequest(factory))
									.ofType(IFlexObject.class)
									.doOnNext(flexObject -> dataMap.add(flexObject.getClass().getName(), flexObject))
									.then(Mono.just(dataMap))));
			
			this.addRandomExtraMessage(builder);
			return result.map(dataMap -> formatter.format(input, dataMap, builder));
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1010", e); 
			return Mono.just(response);
		}
	}
}
