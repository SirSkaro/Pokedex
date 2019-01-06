package skaro.pokedex.data_processor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skaro.pokedex.core.IService;
import skaro.pokedex.core.ServiceType;
import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.type.Type;

public enum TypeData implements IService
{	 		
	NORMAL("Normal", 0),
	FIGHTING("Fighting", 1),
	FLYING("Flying", 2),
	POISON("Poison", 3),
	GROUND("Ground", 4),
	ROCK("Rock", 5),
	BUG("Bug", 6),
	GHOST("Ghost", 7),
	STEEL("Steel", 8),
	FIRE("Fire", 9),
	WATER("Water", 10),
	GRASS("Grass", 11),
	ELECTRIC("Electric", 12),
	PSYCHIC("Psychic", 13),
	ICE("Ice", 14),
	DRAGON("Dragon", 15),
	DARK("Dark", 16),
	FAIRY("Fairy", 17),
	;
	
	private final String properName;
	private final int index;
	private Type type;
	private final static Map<Integer, TypeData> indexMap = new HashMap<Integer, TypeData>();
	public static double[][] effectiveness = new double[/*attacker*/][/*defender*/]{
	  //  			   0    1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17	 18
	  /*0Normal*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 0.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
	  /*1Fighting*/ { 2.0, 1.0, 0.5, 0.5, 1.0, 2.0, 0.5, 0.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 2.0, 1.0, 2.0, 0.5, 1.0 },
	  /*2Flying*/  	{ 1.0, 2.0, 1.0, 1.0, 1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
	  /*3Poison*/  	{ 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 1.0, 0.5, 0.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0 },
	  /*4Ground*/  	{ 1.0, 1.0, 0.0, 2.0, 1.0, 2.0, 0.5, 1.0, 2.0, 2.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
	  /*5Rock*/  	{ 1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 2.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0 },
	  /*6Bug*/  	{ 1.0, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 1.0, 2.0, 1.0, 2.0, 1.0, 1.0, 2.0, 0.5, 1.0 },
	  /*7Ghost*/  	{ 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 1.0, 1.0 },
	  /*8Steel*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 0.5, 0.5, 1.0, 0.5, 1.0, 2.0, 1.0, 1.0, 2.0, 1.0 },
	  /*9Fire*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 2.0, 1.0, 2.0, 0.5, 0.5, 2.0, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0 },
	  /*10Water*/  	{ 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0, 1.0, 2.0, 0.5, 0.5, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0 },
	  /*11Grass*/  	{ 1.0, 1.0, 0.5, 0.5, 2.0, 2.0, 0.5, 1.0, 0.5, 0.5, 2.0, 0.5, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0 },
	  /*12Electric*/{ 1.0, 1.0, 2.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 0.5, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0 },
	  /*13Psychic*/ { 1.0, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 0.0, 1.0, 1.0 },
	  /*14Ice*/     { 1.0, 1.0, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5, 2.0, 1.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0 },
	  /*15Dragon*/  { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.0, 1.0 },
	  /*16Dark*/ 	{ 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 0.5, 1.0 },
	  /*17Fairy*/  	{ 1.0, 2.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0 },
	  /*18Bird*/  	{ 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 },
	    			};
	
	@Override
	public ServiceType getServiceType() 
	{
		return ServiceType.TYPE;
	}
	    			
	public static void initialize(PokeFlexFactory factory)
	{
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> types = new ArrayList<Object>();
		
		System.out.println("[TypeData] Getting Type data from external API...");
		
		for(TypeData type : TypeData.values())
		{
			indexMap.put(type.index, type);
			Request request = new Request(Endpoint.TYPE);
			request.addParam(type.name().toLowerCase());
			concurrentRequestList.add(request);
		}
		
		try
		{
			types = factory.createFlexObjects(concurrentRequestList);
			for(Object type : types)
			{
				Type tempType = (Type)type;
				getByName(tempType.getName()).type = tempType;
			}
		}
		catch (Exception e)
		{
			System.out.println("[TypeData] Unable to get all Types from external API");
			System.exit(1);
		}
		
		System.out.println("[TypeData] Done");
	}
	
	//Constructors
    private TypeData(final String text, int index) 
    { 
    	this.properName = text;
    	this.index = index;
    }

    public String toProperName() { return this.properName; }
    public int toIndex() { return this.index; }
    public Type getType() { return this.type; }
    
    public static TypeData getByName(String type)
    {
    	type = type.toUpperCase();
    	return TypeData.valueOf(type); 
    }
    
    public static TypeData getByIndex(int index)
    { 
    	return indexMap.get(index); 
    }
    
    public String getNameInLanguage(Language lang)
    {
    	return TextFormatter.flexFormToProper(type.getNameInLanguage(lang.getFlexKey()));
    }

}