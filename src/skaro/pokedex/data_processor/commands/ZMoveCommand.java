package skaro.pokedex.data_processor.commands;

import java.io.File;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.ResponseFormatter;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.ColorService;
import skaro.pokedex.services.ConfigurationService;
import skaro.pokedex.services.FlexCacheService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.PerkService;
import skaro.pokedex.services.PerkTier;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokedex.services.FlexCacheService.CachedResource;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.item.Item;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.type.Type;

public class ZMoveCommand extends PokedexCommand
{
	private final String zMoveClipPath;
	private final String defaultZMove; 
	
	public ZMoveCommand(IServiceManager serviceManager, ResponseFormatter discordFormatter) throws ServiceConsumerException
	{
		super(serviceManager, discordFormatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "zmove".intern();
		orderedArgumentCategories.add(ArgumentCategory.TYPE_ZMOVE);
		expectedArgRange = new ArgumentRange(1,1);
		zMoveClipPath = ConfigurationService.getInstance().get().getZMoveClipPath();
		defaultZMove = "extreme-evoboost-3";
		
		aliases.put("z", Language.ENGLISH);
		aliases.put("capacitez", Language.FRENCH);
		aliases.put("capacitéz", Language.FRENCH);
		aliases.put("zattacke", Language.GERMAN);
		aliases.put("mossaz", Language.ITALIAN);
		aliases.put("zgisul", Language.KOREAN);
		aliases.put("movimientoz", Language.SPANISH);
		aliases.put("zwaza", Language.JAPANESE_HIR_KAT);
		aliases.put("zzhāoshì", Language.CHINESE_SIMPMLIFIED);
		aliases.put("zzhaoshì", Language.CHINESE_SIMPMLIFIED);
		
		aliases.put("Ｚワザ", Language.JAPANESE_HIR_KAT);
		aliases.put("zワザ", Language.JAPANESE_HIR_KAT);
		aliases.put("Z기술", Language.KOREAN);
		aliases.put("z기술", Language.KOREAN);
		aliases.put("Ｚ招式", Language.CHINESE_SIMPMLIFIED);
		aliases.put("z招式", Language.CHINESE_SIMPMLIFIED);
	}

	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<type> or <z move>"; }

	@Override
	public boolean hasExpectedServices(IServiceManager services) 
	{
		return super.hasExpectedServices(services) &&
				services.hasServices(ServiceType.POKE_FLEX, ServiceType.CACHE, ServiceType.PERK);
	}
	
	@Override
	public Mono<Response> respondTo(Input input, User author, Guild guild)
	{
		if(!input.isValid())
			return Mono.just(formatter.invalidInputResponse(input));
		
		if(!perkAffordedToUser(author, guild))
		{
			return Mono.just(createNonPrivilegedReply(input))
					.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
		}
		
		EmbedCreateSpec builder = new EmbedCreateSpec();
		Mono<MultiMap<IFlexObject>> result;
		String userInput = input.getArgument(0).getFlexForm();
		PokeFlexService factory = (PokeFlexService)services.getService(ServiceType.POKE_FLEX);
		FlexCacheService flexCache = (FlexCacheService)services.getService(ServiceType.CACHE);
		TypeData cachedTypeData = (TypeData)flexCache.getCachedData(CachedResource.TYPE);
		
		if(input.getArgument(0).getCategory() == ArgumentCategory.TYPE)
			userInput = cachedTypeData.getZMoveByType(userInput);
		
		Request request = new Request(Endpoint.MOVE, userInput);
		result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> request.makeRequest(factory)
						.ofType(Move.class)
						.doOnNext(move -> {
							dataMap.put(Move.class.getName(), move);
							dataMap.put(Type.class.getName(), cachedTypeData.getByName(move.getType().getName()));
						})
						.map(move -> new Request(Endpoint.ITEM, move.getCrystal()))
						.flatMap(itemRequest -> itemRequest.makeRequest(factory))
						.doOnNext(item -> dataMap.put(Item.class.getName(), item))
						.then(Mono.just(dataMap)));
		
		this.addRandomExtraMessage(builder);
		return result
				.map(dataMap -> formatter.format(input, dataMap, builder))
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
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

		builder.setColor(colorService.getColorForPatreon());
		String path = zMoveClipPath + "/"+ defaultZMove +".mp4";
		response.addImage(new File(path));
		builder.setFooter("Pledge $1 to receive this perk!", this.getPatreonLogo());

		builder.setDescription("Pledge $1/month on Patreon to gain access to all Z Move clips!");
		builder.addField("Patreon link", "[Pokedex's Patreon](https://www.patreon.com/sirskaro)", false);
		builder.setThumbnail(this.getPatreonBanner());
		
		response.setEmbed(builder);
		return response;
	}

}
