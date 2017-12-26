package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
				for(int i = 0; i < Type.effectiveness.length; i++)
					if(Type.effectiveness[i][type1.toIndex()] == mult)
						result.addInteraction(mult, Type.getByIndex(i));
			result.addType(type1);
		}
		else
		{
			for(double mult : multipliers)
				for(int i = 0; i < Type.effectiveness.length; i++)
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
		
		immune.addAll(Arrays.asList(Type.values()));
		immune.remove(Type.BIRD);	//not a conon Type
		
		for(Type type : types)
		{
			effective.addAll(getTypesWithOffenseEffectiveness(type, 2.0));
			neutral.addAll(getTypesWithOffenseEffectiveness(type, 1.0));
			resist.addAll(getTypesWithOffenseEffectiveness(type, 0.5));
		}
		
		//Remove duplicates with priority: effective > neutral > resist > immune
		neutral.removeAll(effective);
		resist.removeAll(effective);
		immune.remove(effective);
		
		resist.removeAll(neutral);
		immune.remove(neutral);
		
		immune.remove(resist);
		
		//Populate the wrapper
		for(Type type : effective)
			result.addInteraction(2.0, type);
		for(Type type : neutral)
			result.addInteraction(1.0, type);
		for(Type type : resist)
			result.addInteraction(0.5, type);
		for(Type type : immune)
			result.addInteraction(0.0, type);
		
		return result;
	}
	
	private static List<Type> getTypesWithOffenseEffectiveness(Type atk, double mult)
	{
		List<Type> types = Arrays.asList(Type.values());
		types.remove(Type.BIRD);		//not a canon typing
		for(Type type : types)
			if(Type.effectiveness[atk.toIndex()][type.toIndex()] != mult)
				types.remove(type);
		
		return types;
	}
		
}