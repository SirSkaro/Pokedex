package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skaro.pokedex.core.ICachedData;
import skaro.pokedex.services.PokeFlexService;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.move_learn_method.MoveLearnMethod;

public class LearnMethodData implements ICachedData
{
	private final Map<String, MoveLearnMethod> methodMap;
	
	public LearnMethodData(PokeFlexService factory)
	{
		methodMap = new HashMap<String, MoveLearnMethod>();
		initialize(factory);
	}
	
	@Override
	public MoveLearnMethod getByName(String name)
	{
		return methodMap.get(name);
	}
	
	private void initialize(PokeFlexService factory)
	{
		List<PokeFlexRequest> concurrentRequests = new ArrayList<>();
		
		System.out.println("[LearnMethodData] Getting Method data from external API...");
		
		for(int i = 1; i < 11; i++)
			concurrentRequests.add(new Request(Endpoint.MOVE_LEARN_METHOD, Integer.toString(i)));
		
		factory.createFlexObjects(concurrentRequests, factory.getScheduler())
			.ofType(MoveLearnMethod.class)
			.doOnNext(learnMethod -> methodMap.put(learnMethod.getName(), learnMethod))
		.subscribe(value -> System.out.println("[LearnMethodData] Cached data"), 
					error -> {
						System.out.println("[LearnMethodData] Unable to get all Methods from external API");
						System.exit(1);
					});
	}
}
