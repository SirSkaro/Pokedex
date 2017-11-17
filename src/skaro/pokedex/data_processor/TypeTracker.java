package skaro.pokedex.data_processor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeTracker {
	
	private static double[][] effectiveness = new double[/*attacker*/][/*defender*/]{
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
		    			
	private static Map<String, Color> typeColor = new HashMap<>();
	private static Map<String,Integer> typeMapString = new HashMap<>();
	private static Map<Integer,String> typeMapInt = new HashMap<>();
	
	/**
	 * A function to check defensive type interactions
	 * @param type1: one of two types to check defensively
	 * @param type2: two of two types to check defensively. Can be null
	 * @return An object that wraps all the type interactions
	 */
	public static TypeInteractionWrapper onDefense(String type1, String type2)
	{
		if(type1 == null)
			return null;
		
		TypeInteractionWrapper result = new TypeInteractionWrapper();
		
		ArrayList<String> temp = new ArrayList<String>();
		Double[] multiplier = {0.0, 0.25, 0.5, 1.0, 2.0, 4.0};
		type1 = type1.toLowerCase();
		
		if(type2 == null)
		{
			for(int j = 0; j < multiplier.length; j++)
			{
				for(int i = 0; i < 18; i++)
					if(effectiveness[i][typeMapString.get(type1)] == multiplier[j])
						temp.add(typeMapInt.get(i));
			
				result.setInteraction(multiplier[j], temp);
				temp = new ArrayList<String>();
			}
		}
		else
		{
			type2 = type2.toLowerCase();
			for(int j = 0; j < multiplier.length; j++)
			{
				for(int i = 0; i < 18; i++)
					if(effectiveness[i][typeMapString.get(type1)] * effectiveness[i][typeMapString.get(type2)] == multiplier[j])
						temp.add(typeMapInt.get(i));
				
				result.setInteraction(multiplier[j], temp);
				temp = new ArrayList<String>();
			}
			
		}
		
		result.setType1(typeMapInt.get(typeMapString.get(type1))); //Get capitalized name
		if(type2 != null)
			result.setType2(typeMapInt.get(typeMapString.get(type2))); //Get capitalized name
		return result;
	}
	
	/**
	 * A method to check for coverage against other typing. If one of the types is super effective 
	 * against a type then that type is considered to be covered. If all of the inputed types
	 * are not very effective/immune against a type then it is not covered.
	 * @param typeX: a typing to check for type interaction
	 * @return The interaction between these four types and all other types
	 */
	public static TypeInteractionWrapper coverage(String type1, String type2, String type3, String type4)
	{
		TypeInteractionWrapper result = new TypeInteractionWrapper();
		String[] types = {type1, type2, type3, type4};
		Set<String> effective = new HashSet<String>();
		Set<String> resist = new HashSet<String>();
		Set<String> neutral = new HashSet<String>();
		Set<String> immune = new HashSet<String>();
		ArrayList<String> temp = new ArrayList<String>();
		Color currColor = null;
		
		for(int i = 0; i < 4; i++)
		{
			if(types[i] != null)
			{
				types[i] = types[i].toLowerCase();
				result.setType(typeMapInt.get(typeMapString.get(types[i])), i + 1);
				for(int j = 0; j < 18; j++)
				{
					effective.addAll(atk2xEffective(types[i]));
					resist.addAll(atk2xResist(types[i]));
					neutral.addAll(atkNeutral(types[i]));
					immune.addAll(atkImmune(types[i]));
				}
				currColor = blend(getColor(types[i]), currColor);
			}
		}
		
		result.setColor(currColor);
		
		//Add all super effective types
		temp.addAll(effective);
		result.setEx2(temp);
		temp = new ArrayList<String>();
		
		//Add all resistive types
		for (String s : resist) 
		{
		    if(!neutral.contains(s) && !immune.contains(s) && !effective.contains(s))
		    {
		    	temp.add(s);
		    }
		}
		result.setRx2(temp);
		temp = new ArrayList<String>();
		
		//Add all neutral types
		for (String s : neutral) 
		{
		    if(!immune.contains(s) && !effective.contains(s))
		    	temp.add(s);
		}
		result.setN(temp);
		temp = new ArrayList<String>();
		
		//Add all neutral types
		for (String s : immune) 
		{
		    if(!effective.contains(s) && !neutral.contains(s))
		    	temp.add(s);
		}
		result.setImm(temp);
		
		return result;
	}
	
	public static boolean isType(String input)
	{
		input = input.toLowerCase();
		if(typeMapString.get(input) != null)
			return true;
		return false;
	}
	
	public static ArrayList<String> def2xEffective(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[i][typeMapString.get(type)] == 2.0)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	public static ArrayList<String> atk2xResist(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[typeMapString.get(type)][i] == 0.5)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	
	public static ArrayList<String> defImmune(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[i][typeMapString.get(type)] == 0.0)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	public static ArrayList<String> atkImmune(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[typeMapString.get(type)][i] == 0.0)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	public static ArrayList<String> def2xResist(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[i][typeMapString.get(type)] == 0.5)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	public static ArrayList<String> atk2xEffective(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[typeMapString.get(type)][i] == 2.0)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	public static ArrayList<String> atkNeutral(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[typeMapString.get(type)][i] == 1.0)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	public static ArrayList<String> defNeutral(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < 18; i++)
			if(effectiveness[i][typeMapString.get(type)] == 1.0)
				result.add(typeMapInt.get(i));
		
		return result;
	}
	
	public static ArrayList<String> intersection(ArrayList<String> list1, ArrayList<String> list2) 
	{
		ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < list1.size(); i++) 
        {
            if(list2.contains(list1.get(i))) 
            {
                list.add(list1.get(i));
            }
        }

        return list;
    }
	
	public static Color getColor(String type)
	{
		return typeColor.get(type.toLowerCase());
	}
	
	private static Color blend(Color c0, Color c1) 
	{
		if(c1 == null)
			return c0;
		
		double totalAlpha = c0.getAlpha() + c1.getAlpha();
		double weight0 = c0.getAlpha() / totalAlpha;
		double weight1 = c1.getAlpha() / totalAlpha;
		
		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
		double a = Math.max(c0.getAlpha(), c1.getAlpha());
		
		return new Color((int) r, (int) g, (int) b, (int) a);
	}
	
	static
	{
		typeMapString.put("normal".intern(), 0);
		typeMapString.put("fighting".intern(), 1);
		typeMapString.put("flying".intern(), 2);
		typeMapString.put("poison".intern(), 3);
		typeMapString.put("ground".intern(), 4);
		typeMapString.put("rock".intern(), 5);
		typeMapString.put("bug".intern(), 6);
		typeMapString.put("ghost".intern(), 7);
		typeMapString.put("steel".intern(), 8);
		typeMapString.put("fire".intern(), 9);
		typeMapString.put("water".intern(), 10);
		typeMapString.put("grass".intern(), 11);
		typeMapString.put("electric".intern(), 12);
		typeMapString.put("psychic".intern(), 13);
		typeMapString.put("ice".intern(), 14);
		typeMapString.put("dragon".intern(), 15);
		typeMapString.put("dark".intern(), 16);
		typeMapString.put("fairy".intern(), 17);
		typeMapString.put("bird".intern(), 18);
		
		typeMapInt.put(0, "Normal".intern());
		typeMapInt.put(1, "Fighting".intern());
		typeMapInt.put(2, "Flying".intern());
		typeMapInt.put(3, "Poison".intern());
		typeMapInt.put(4, "Ground".intern());
		typeMapInt.put(5, "Rock".intern());
		typeMapInt.put(6, "Bug".intern());
		typeMapInt.put(7, "Ghost".intern());
		typeMapInt.put(8, "Steel".intern());
		typeMapInt.put(9, "Fire".intern());
		typeMapInt.put(10, "Water".intern());
		typeMapInt.put(11, "Grass".intern());
		typeMapInt.put(12, "Electric".intern());
		typeMapInt.put(13, "Psychic".intern());
		typeMapInt.put(14, "Ice".intern());
		typeMapInt.put(15, "Dragon".intern());
		typeMapInt.put(16, "Dark".intern());
		typeMapInt.put(17, "Fairy");
		typeMapInt.put(18, "Bird");
		
		typeColor.put("normal".intern(), Color.decode("0xA8A77A"));
		typeColor.put("fighting".intern(), Color.decode("0xC22E28"));
		typeColor.put("flying".intern(), Color.decode("0xA98FF3"));
		typeColor.put("poison".intern(), Color.decode("0xA33EA1"));
		typeColor.put("ground".intern(), Color.decode("0xE2BF65"));
		typeColor.put("rock".intern(), Color.decode("0xB6A136"));
		typeColor.put("bug".intern(), Color.decode("0xA6B91A"));
		typeColor.put("ghost".intern(), Color.decode("0x735797"));
		typeColor.put("steel".intern(), Color.decode("0xB7B7CE"));
		typeColor.put("fire".intern(), Color.decode("0xEE8130"));
		typeColor.put("water".intern(), Color.decode("0x6390F0"));
		typeColor.put("grass".intern(), Color.decode("0x7AC74C"));
		typeColor.put("electric".intern(), Color.decode("0xF7D02C"));
		typeColor.put("psychic".intern(), Color.decode("0xF95587"));
		typeColor.put("ice".intern(), Color.decode("0x96D9D6"));
		typeColor.put("dragon".intern(), Color.decode("0x6F35FC"));
		typeColor.put("dark".intern(), Color.decode("0x705746"));
		typeColor.put("fairy".intern(), Color.decode("0xD685AD"));
		typeColor.put("bird".intern(), Color.decode("0xA4BBB3"));
	}
		
}
