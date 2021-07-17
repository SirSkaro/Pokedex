package skaro.pokedex.data_processor.commands;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.input_processor.ArgumentSpec;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.PokemonArgument;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.PerkService;
import skaro.pokedex.services.PerkTier;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.PokedexServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.pokemon.Pokemon;
import skaro.pokeflex.objects.pokemon_species.PokemonSpecies;

public class ShinyCommand extends PokedexCommand 
{
	private final String baseModelPath;
	private final String defaultPokemon;
	
	public ShinyCommand(PokedexServiceManager services, ResponseFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "shiny";
		baseModelPath = ((ConfigurationService)services.getService(ServiceType.CONFIG)).getModelBasePath();
		defaultPokemon = "jirachi";
		
		aliases.put("schillerndes", Language.GERMAN);
		aliases.put("fāguāng", Language.CHINESE_SIMPMLIFIED);
		aliases.put("faguang", Language.CHINESE_SIMPMLIFIED);
		aliases.put("chromatique", Language.FRENCH);
		aliases.put("cromatico", Language.ITALIAN);
		aliases.put("irochi", Language.JAPANESE_HIR_KAT);
		aliases.put("irochigai", Language.JAPANESE_HIR_KAT);
		aliases.put("bichnaneun", Language.KOREAN);
		aliases.put("variocolor", Language.SPANISH);
		aliases.put("vario", Language.SPANISH);
		
		aliases.put("빛나는", Language.KOREAN);
		aliases.put("色違い", Language.JAPANESE_HIR_KAT);
		aliases.put("发光", Language.CHINESE_SIMPMLIFIED);

		createHelpMessage("Ponyta", "Solgaleo", "Keldeo resolute", "eevee");
	}

	public boolean makesWebRequest() { return true; }
	public String getArguments() { return "<pokemon>"; }
	
	@Override
	public boolean hasExpectedServices(PokedexServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.PERK, ServiceType.COLOR, ServiceType.CONFIG);
	}

	@Override
	public Mono<Response> respondTo(Input input, User requester, Guild guild)
	{
		if(!input.allArgumentValid())
			return Mono.just(formatter.invalidInputResponse(input));

		if(!perkAffordedToUser(requester, guild))
		{
			return Mono.fromCallable(() -> createNonPrivilegedReply(input))
					.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
		}
		
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		EmbedCreateSpec builder = new EmbedCreateSpec();
		String pokemonName = input.getArgument(0).getFlexForm();
		Mono<MultiMap<IFlexObject>> result;
		
		Request request = new Request(Endpoint.POKEMON, pokemonName);
		result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> request.makeRequest(factory)
					.ofType(Pokemon.class)
					.flatMap(pokemon -> this.addAdopter(pokemon, builder))
					.doOnNext(pokemon -> dataMap.put(Pokemon.class.getName(), pokemon))
					.map(pokemon -> new Request(Endpoint.POKEMON_SPECIES, pokemon.getSpecies().getName()))
					.flatMap(speciesRequest -> speciesRequest.makeRequest(factory))
					.doOnNext(species -> dataMap.put(PokemonSpecies.class.getName(), species))
					.then(Mono.just(dataMap)));
		
		this.addRandomExtraMessage(builder);
		return result.flatMap(dataMap -> Mono.fromCallable(() -> formatter.format(input, dataMap, builder)))
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
	}
	
	@Override
	protected void createArgumentSpecifications()
	{
		argumentSpecifications.add(new ArgumentSpec(false, PokemonArgument.class));
	}
	
	private boolean perkAffordedToUser(User user, Guild guild)
	{
		PerkService perkService = (PerkService)services.getService(ServiceType.PERK);
		
		return (perkService.userHasPerksForTier(user, PerkTier.YOUNGSTER_LASS).block() 
				|| perkService.ownerOfGuildHasPerksForTier(guild, PerkTier.CHAMPION).block());
	}

	private Response createNonPrivilegedReply(Input input)
	{
		ColorService colorService = (ColorService)services.getService(ServiceType.COLOR);
		Response response = new Response();
		EmbedCreateSpec builder = new EmbedCreateSpec();

		//Easter egg: if the user specifies the default non-privilaged Pokemon, use the Patreon logo instead
		builder.setColor(colorService.getColorForPatreon());
		builder.setFooter("Pledge $1 to receive this perk!", this.getPatreonLogo());
		if(!input.getArgument(0).getDbForm().equals(defaultPokemon)) {
			addDefaultImageToResponse(response, builder);
		}
		else {
			builder.setImage(this.getPatreonLogo());
		}
		
		//format reply
		response.addToReply("Pledge $1/month on Patreon to gain access to all HD shiny Pokemon!");
		builder.addField("Patreon link", "[Pokedex's Patreon](https://www.patreon.com/sirskaro)", false);
		builder.setThumbnail(this.getPatreonBanner());
		
		response.setEmbed(builder);
		return response;
	}
	
	private void addDefaultImageToResponse(Response response, EmbedCreateSpec builder) {
		String fileName = defaultPokemon +".gif";
		try {
			builder.setImage("attachment://" + fileName);
			URL url =  new URL(baseModelPath + "/"+ fileName);
			response.addImage(fileName, url.openStream());
		} catch(IOException e) {
			throw Exceptions.propagate(e);
		}
	}

}
