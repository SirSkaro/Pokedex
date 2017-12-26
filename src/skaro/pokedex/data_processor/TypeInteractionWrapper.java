package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class TypeInteractionWrapper 
{
	private HashMap<Double, ArrayList<Type>> interactions;
	private ArrayList<Type> types;
	
	//Constructor
	public TypeInteractionWrapper()
	{
		interactions = new HashMap<Double, ArrayList<Type>>();
		types = new ArrayList<Type>();
		
		Double[] keys = {0.0, 0.25, 0.5, 1.0, 2.0, 4.0};
		for(Double key: keys)
			interactions.put(key, new ArrayList<Type>());
	}
	
	//Set and Get methods
	public void addType(Type type) { types.add(type); }
	public void addInteraction(double mult, Type type)
	{ 
		ArrayList<Type> interaction = interactions.get(mult);
		interaction.add(type);
	}
	
	public ArrayList<Type> getTypes() { return types; }
	public ArrayList<Type> getInteractionByMultiplier(Double mult) { return interactions.get(mult); }
	
	public String typesToString()
	{
		StringBuilder builder = new StringBuilder();
		
		for(Type type : types)
			builder.append("/"+type.toProperName());
		
		return builder.substring(1);
	}
	
	public Optional<String> interactionToString(Double mult)
	{
		if(interactions.get(mult).isEmpty())
			return Optional.empty();
		
		StringBuilder builder = new StringBuilder();
		
		for(Type type : interactions.get(mult))
			builder.append(", "+type.toProperName());
		
		return Optional.of(builder.substring(2));
	}
}
