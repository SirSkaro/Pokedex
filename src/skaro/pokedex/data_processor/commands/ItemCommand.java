package skaro.pokedex.data_processor.commands;

import org.eclipse.jetty.util.MultiMap;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import skaro.pokedex.data_processor.PokedexCommand;
import skaro.pokedex.data_processor.IDiscordFormatter;
import skaro.pokedex.data_processor.Response;
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokedex.services.FlexCacheService;
import skaro.pokedex.services.IServiceManager;
import skaro.pokedex.services.ServiceConsumerException;
import skaro.pokedex.services.ServiceType;
import skaro.pokedex.services.FlexCacheService.CachedResource;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.item.Item;
import skaro.pokeflex.objects.item_category.ItemCategory;
import skaro.pokeflex.objects.type.Type;

public class ItemCommand extends PokedexCommand
{
	public ItemCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "item".intern();
		orderedArgumentCategories.add(ArgumentCategory.ITEM);
		expectedArgRange = new ArgumentRange(1,1);
		
		aliases.put("itm", Language.ENGLISH);
		aliases.put("getragenes", Language.GERMAN);
		aliases.put("strumento", Language.ITALIAN);
		aliases.put("jinin", Language.KOREAN);
		aliases.put("wùpǐn", Language.CHINESE_SIMPMLIFIED);
		aliases.put("wupin", Language.CHINESE_SIMPMLIFIED);
		aliases.put("objeto", Language.SPANISH);
		aliases.put("dōgu", Language.JAPANESE_HIR_KAT);
		aliases.put("dogu", Language.JAPANESE_HIR_KAT);
		aliases.put("objet", Language.FRENCH);
		
		aliases.put("ツール", Language.JAPANESE_HIR_KAT);
		aliases.put("도구", Language.KOREAN);
		aliases.put("物品", Language.CHINESE_SIMPMLIFIED);
		
		createHelpMessage("Life Orb", "leftovers", "Choice Band", "eviolite",
				"https://i.imgur.com/B1NlcYh.gif");
	}
	
	@Override
	public boolean makesWebRequest() { return true; }
	@Override
	public String getArguments() { return "<item>"; }
	
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
		
		Mono<MultiMap<IFlexObject>> result;
		EmbedCreateSpec builder = new EmbedCreateSpec();
		String itemName = input.getArgument(0).getFlexForm();
		
		PokeFlexFactory factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
		FlexCacheService flexCache = (FlexCacheService)services.getService(ServiceType.CACHE);
		TypeData cachedTypeData = (TypeData)flexCache.getCachedData(CachedResource.TYPE);

		Request request = new Request(Endpoint.ITEM, itemName);
		result = Mono.just(new MultiMap<IFlexObject>())
				.flatMap(dataMap -> request.makeRequest(factory)
					.ofType(Item.class)
					.doOnNext(item -> {
						dataMap.put(Item.class.getName(), item);
						if(item.getNgType() != null)
							dataMap.put(Type.class.getName(), cachedTypeData.getByName(item.getNgType().toLowerCase()));
					})
					.map(item -> new RequestURL(item.getCategory().getUrl(), Endpoint.ITEM_CATEGORY))
					.flatMap(itemCategoryRequest -> itemCategoryRequest.makeRequest(factory))
					.ofType(ItemCategory.class)
					.doOnNext(itemCategory -> dataMap.put(ItemCategory.class.getName(), itemCategory))
					.then(Mono.just(dataMap)));
		
		this.addRandomExtraMessage(builder);
		return result
				.map(dataMap -> formatter.format(input, dataMap, builder))
				.onErrorResume(error -> Mono.just(this.createErrorResponse(input, error)));
	}
}
