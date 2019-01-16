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
import skaro.pokedex.data_processor.TypeService;
import skaro.pokedex.input_processor.Input;
import skaro.pokedex.input_processor.Language;
import skaro.pokedex.input_processor.arguments.ArgumentCategory;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.RequestURL;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.type.Type;

public class MoveCommand extends AbstractCommand 
{
	public MoveCommand(IServiceManager services, IDiscordFormatter formatter) throws ServiceConsumerException
	{
		super(services, formatter);
		if(!hasExpectedServices(this.services))
			throw new ServiceConsumerException("Did not receive all necessary services");
		
		commandName = "move".intern();
		argCats.add(ArgumentCategory.MOVE);
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
			
			//Initial data - Move object
			Move move = (Move)factory.createFlexObject(Endpoint.MOVE, input.argsAsList());
			dataMap.put(Move.class.getName(), move);
			
			//Target
			concurrentRequestList.add(new RequestURL(move.getTarget().getUrl(), Endpoint.MOVE_TARGET));
			
			//Contest
			if(move.getContestType() != null)
				concurrentRequestList.add(new RequestURL(move.getContestType().getUrl(), Endpoint.CONTEST_TYPE));
			
			//Damage Class (Category)
			concurrentRequestList.add(new RequestURL(move.getDamageClass().getUrl(), Endpoint.MOVE_DAMAGE_CLASS));
			
			//Make PokeFlex request
			flexData = factory.createFlexObjects(concurrentRequestList);
			
			//Add all data to the map
			for(Object obj : flexData)
				dataMap.add(obj.getClass().getName(), obj);
			
			//Type
			dataMap.add(Type.class.getName(), TypeService.getByName(move.getType().getName()).getType());
			
			this.addRandomExtraMessage(builder);
			return formatter.format(input, dataMap, builder);
		}
		catch(Exception e)
		{
			Response response = new Response();
			this.addErrorMessage(response, input, "1006", e); 
			return Mono.just(response);
		}
	}
	
}
