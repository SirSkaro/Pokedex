package skaro.pokedex.data_processor.commands;

import java.util.ArrayList;
import java.util.List;

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
import skaro.pokedex.data_processor.TypeData;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.item.Item;
import skaro.pokeflex.objects.type.Type;

public class ItemCommand extends AbstractCommand
{
	public ItemCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "item".intern();
		argCats.add(ArgumentCategory.ITEM);
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
				services.hasServices(ServiceType.POKE_FLEX);
	}
	
	@Override
	public Mono<Response> discordReply(Input input, User requester)
	{
		if(!input.isValid())
			return formatter.invalidInputResponse(input);
		
		PokeFlexFactory factory;
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> flexData = new ArrayList<Object>();
		MultiMap<Object> dataMap = new MultiMap<Object>();
		EmbedCreateSpec builder = new EmbedCreateSpec();
		
		try
		{
			factory = (PokeFlexFactory)services.getService(ServiceType.POKE_FLEX);
			
			//Initial data - Item object
			Item item = (Item)factory.createFlexObject(Endpoint.ITEM, input.argsAsList());
			dataMap.put(Item.class.getName(), item);
			
			//item category
			concurrentRequestList.add(new RequestURL(item.getCategory().getUrl(), Endpoint.ITEM_CATEGORY));
			
			//type
			if(item.getNgType() != null)
			{
				dataMap.add(Type.class.getName(), TypeData.getByName(item.getNgType().toLowerCase()).getType());
			}
			
			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1004", e); 
			return Mono.just(response);
		}
	}
}
