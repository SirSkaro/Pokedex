package skaro.pokedex.data_processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TypeTracker {
	
	/**
	 * A function to check defensive type interactions
	 * @param type1: one of two types to check defensively
	 * @param type2: two of two types to check defensively. Can be null
	 * @return An object that wraps all the type interactions
	 */
	public static TypeInteractionWrapper onDefense(TypeData type1, TypeData type2)
	{	
		TypeInteractionWrapper result = new TypeInteractionWrapper();
		Double[] multipliers = {0.0, 0.25, 0.5, 1.0, 2.0, 4.0};
		
		if(type2 == null)
		{
			for(double mult : multipliers)
				for(int i = 0; i < TypeData.effectiveness.length - 1; i++)
					if(TypeData.effectiveness[i][type1.toIndex()] == mult)
						result.addInteraction(mult, TypeData.getByIndex(i));
			result.addType(type1);
		}
		else
		{
			for(double mult : multipliers)
				for(int i = 0; i < TypeData.effectiveness.length - 1; i++)
					if(TypeData.effectiveness[i][type1.toIndex()] * TypeData.effectiveness[i][type2.toIndex()] == mult)
						result.addInteraction(mult, TypeData.getByIndex(i));
			result.addType(type1);
			result.addType(type2);
		}
		
		return result;
	}
	
	/**
	 * A method to check for coverage against other typing. If one of the types is super effective 
	 * against a type then that type is considered to be covered. If all of the inputed types
	 * are not very effective/immune against a type then it is not covered.
	 * @param typeX: a typing to check for type interaction
	 * @return The interaction between these four types and all other types
	 */
	public static TypeInteractionWrapper onOffense(List<TypeData> typeList)
	{
		TypeInteractionWrapper result = new TypeInteractionWrapper();
		Set<TypeData> effective = new HashSet<TypeData>();
		Set<TypeData> neutral = new HashSet<TypeData>();
		Set<TypeData> resist = new HashSet<TypeData>();
		Set<TypeData> immune = new HashSet<TypeData>();
		Iterator<TypeData> iter = typeList.iterator();
		
		immune.addAll(Arrays.asList(TypeData.values()));
		
		while (iter.hasNext())
		{
			TypeData type = iter.next();
			effective.addAll(getTypesWithOffenseEffectiveness(type, 2.0));
			neutral.addAll(getTypesWithOffenseEffectiveness(type, 1.0));
			resist.addAll(getTypesWithOffenseEffectiveness(type, 0.5));
			result.addType(type);
		}
		
		//Remove duplicates with priority: effective > neutral > resist > immune
		neutral.removeAll(effective);
		resist.removeAll(effective);
		immune.removeAll(effective);
		
		resist.removeAll(neutral);
		immune.removeAll(neutral);
		
		immune.removeAll(resist);
		
		//Populate the wrapper
		for(TypeData t : effective)
			result.addInteraction(2.0, t);
		for(TypeData t : neutral)
			result.addInteraction(1.0, t);
		for(TypeData t : resist)
			result.addInteraction(0.5, t);
		for(TypeData t : immune)
			result.addInteraction(0.0, t);
		
		return result;
	}
	
	private static Set<TypeData> getTypesWithOffenseEffectiveness(TypeData atk, double mult)
	{
		Set<TypeData> types = new HashSet<TypeData>(Arrays.asList(TypeData.values()));
		TypeData type;
		
		Iterator<TypeData> iter = types.iterator();
		while (iter.hasNext())
		{
			type = iter.next();
			if(TypeData.effectiveness[atk.toIndex()][type.toIndex()] != mult)
				iter.remove();
		}
			
		return types;
	}
		
}