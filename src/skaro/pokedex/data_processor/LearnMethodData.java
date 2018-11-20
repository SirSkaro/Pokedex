package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.move_learn_method.MoveLearnMethod;

public enum LearnMethodData 
{
	LEVEL_UP("level-up"),
	EGG("egg"),
	TUTOR("tutor"),
	MACHINE("machine"),
	STADIUM("stadium-surfing-pikachu"),
	LIGHT_BALL("light-ball-egg"),
	COL_PUR("colosseum-purification"),
	XD_SHADOW("xd-shadow"),
	XD_PUR("xd-purification"),
	FORM_CHANGE("form-change"),
	;
	
	private String name;
	private final static Map<String, MoveLearnMethod> methodMap = new HashMap<String, MoveLearnMethod>();
	
	private LearnMethodData(String name)
	{
		this.name = name;
	}
	
	public static MoveLearnMethod getByName(String name)
	{
		return methodMap.get(name);
	}
	
	public static void initialize(PokeFlexFactory factory)
	{
		List<PokeFlexRequest> concurrentRequests = new ArrayList<PokeFlexRequest>();
		List<Object> methods = new ArrayList<Object>();
		
		System.out.println("[LearnMethodData] Getting Method data from external API...");
		
		try
		{
			for(LearnMethodData lmd : LearnMethodData.values())
				concurrentRequests.add(new Request(Endpoint.MOVE_LEARN_METHOD, lmd.name));
			
			methods = factory.createFlexObjects(concurrentRequests);
			for(Object method : methods)
			{
				MoveLearnMethod tempMethod = (MoveLearnMethod)method;
				methodMap.put(tempMethod.getName(), tempMethod);
			}
		}
		catch (Exception e)
		{
			System.out.println("[LearnMethodData] Unable to get all Methods from external API");
			System.exit(1);
		}
		
		System.out.println("[LearnMethodData] Done");
	}
}
