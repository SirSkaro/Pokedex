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
import skaro.pokeflex.objects.type.Type;

public class TypeData implements ICachedData
{
	private final Map<String, Type> typeMap;
	private final Map<String, String> zMoveMap;
	
	public TypeData(PokeFlexService factory)
	{
		typeMap = new HashMap<>();
		zMoveMap = new HashMap<>();
		initialize(factory);
	}
	
	@Override
	public Type getByName(String name)
	{
		return typeMap.get(name);
	}
	
	public String getZMoveByType(String typeName)
	{
		return zMoveMap.get(typeName);
	}
	
	public List<Type> getAllTypes()
	{
		return new ArrayList<Type>(typeMap.values());
	}
	
	private void initialize(PokeFlexService factory)
	{
		List<PokeFlexRequest> concurrentRequests = new ArrayList<>();
		
		System.out.println("[TypeData] Getting Type data from external API...");
		
		for(int i = 1; i < 19; i++)
			concurrentRequests.add(new Request(Endpoint.TYPE, Integer.toString(i)));
		
		factory.createFlexObjects(concurrentRequests, factory.getScheduler())
			.ofType(Type.class)
			.doOnNext(type -> typeMap.put(type.getName(), type))
			.doOnNext(type -> mapZMoveToType(type))
		.subscribe(value -> System.out.println("[TypeData] Cached data"), 
					error -> {
						System.out.println("[TypeData] Unable to get all Types from external API");
						System.exit(1);
					});
	}
	
	private void mapZMoveToType(Type type)
	{
		type.getMoves().stream()
			.filter(move -> move.getName().contains("--physical"))
			.findFirst()
			.ifPresent(zMove -> zMoveMap.put(type.getName(), zMove.getName()));
	}
}
