package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TypeTracker {
	
	/**
	 * A function to check defensive type interactions
	 * @param type1: one of two types to check defensively
	 * @param type2: two of two types to check defensively. Can be null
	 * @return An object that wraps all the type interactions
	 */
	public static TypeInteractionWrapper onDefense(Type type1, Type type2)
	{	
		TypeInteractionWrapper result = new TypeInteractionWrapper();
		Double[] multipliers = {0.0, 0.25, 0.5, 1.0, 2.0, 4.0};
		
		if(type2 == null)
		{
			for(double mult : multipliers)
				for(int i = 0; i < Type.effectiveness.length - 1; i++)
					if(Type.effectiveness[i][type1.toIndex()] == mult)
						result.addInteraction(mult, Type.getByIndex(i));
			result.addType(type1);
		}
		else
		{
			for(double mult : multipliers)
				for(int i = 0; i < Type.effectiveness.length - 1; i++)
					if(Type.effectiveness[i][type1.toIndex()] * Type.effectiveness[i][type2.toIndex()] == mult)
						result.addInteraction(mult, Type.getByIndex(i));
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
	public static TypeInteractionWrapper onOffense(ArrayList<Type> types)
	{
		TypeInteractionWrapper result = new TypeInteractionWrapper();
		Set<Type> effective = new HashSet<Type>();
		Set<Type> neutral = new HashSet<Type>();
		Set<Type> resist = new HashSet<Type>();
		Set<Type> immune = new HashSet<Type>();
		Iterator<Type> iter = types.iterator();
		
		immune.addAll(Arrays.asList(Type.values()));
		immune.remove(Type.BIRD);	//not a canon Type
		
		while (iter.hasNext())
		{
			Type type = iter.next();
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
		for(Type t : effective)
			result.addInteraction(2.0, t);
		for(Type t : neutral)
			result.addInteraction(1.0, t);
		for(Type t : resist)
			result.addInteraction(0.5, t);
		for(Type t : immune)
			result.addInteraction(0.0, t);
		
		return result;
	}
	
	private static Set<Type> getTypesWithOffenseEffectiveness(Type atk, double mult)
	{
		Set<Type> types = new HashSet<Type>(Arrays.asList(Type.values()));
		Type type;
		types.remove(Type.BIRD);		//not a canon typing
		
		Iterator<Type> iter = types.iterator();
		while (iter.hasNext())
		{
			type = iter.next();
			if(Type.effectiveness[atk.toIndex()][type.toIndex()] != mult)
				iter.remove();
		}
			
		return types;
	}
		
}