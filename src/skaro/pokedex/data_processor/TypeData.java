package skaro.pokedex.data_processor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import skaro.pokedex.data_processor.formatters.TextFormatter;
import skaro.pokedex.input_processor.Language;
import skaro.pokeflex.api.Endpoint;
import skaro.pokeflex.api.PokeFlexFactory;
import skaro.pokeflex.api.PokeFlexRequest;
import skaro.pokeflex.api.Request;
import skaro.pokeflex.objects.type.Type;

public enum TypeData 
{	 		
	NORMAL("Normal", 0, 0xA8A77A),
	FIGHTING("Fighting", 1, 0xC22E28),
	FLYING("Flying", 2, 0xA98FF3),
	POISON("Poison", 3, 0xA33EA1),
	GROUND("Ground", 4, 0xE2BF65),
	ROCK("Rock", 5, 0xB6A136),
	BUG("Bug", 6, 0xA6B91A),
	GHOST("Ghost", 7, 0x735797),
	STEEL("Steel", 8, 0xB7B7CE),
	FIRE("Fire", 9, 0xEE8130),
	WATER("Water", 10, 0x6390F0),
	GRASS("Grass", 11, 0x7AC74C),
	ELECTRIC("Electric", 12, 0xF7D02C),
	PSYCHIC("Psychic", 13, 0xF95587),
	ICE("Ice", 14, 0x96D9D6),
	DRAGON("Dragon", 15, 0x6F35FC),
	DARK("Dark", 16, 0x705746),
	FAIRY("Fairy", 17, 0xD685AD),
	;
	
	private final String properName;
	private final int index;
	private final Color color;
	private Type type;
	private final static HashMap<Integer, TypeData> indexMap = new HashMap<Integer, TypeData>();
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
	
	public static void initialize(PokeFlexFactory factory)
	{
		List<PokeFlexRequest> concurrentRequestList = new ArrayList<PokeFlexRequest>();
		List<Object> types = new ArrayList<Object>();
		
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
	}
	
	//Constructors
    private TypeData(final String text, int index, int color) 
    { 
    	this.properName = text;
    	this.index = index;
    	this.color = new Color(color);
    }

    public String toProperName() { return this.properName; }
    public int toIndex() { return this.index; }
    public Color toColor() { return this.color; }
    
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


