package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class TypeInteractionWrapper 
{
	private HashMap<Double, ArrayList<TypeData>> interactions;
	private ArrayList<TypeData> types;
	
	//Constructor
	public TypeInteractionWrapper()
	{
		interactions = new HashMap<Double, ArrayList<TypeData>>();
		types = new ArrayList<TypeData>();
		
		Double[] keys = {0.0, 0.25, 0.5, 1.0, 2.0, 4.0};
		for(Double key: keys)
			interactions.put(key, new ArrayList<TypeData>());
	}
	
	//Set and Get methods
	public void addType(TypeData type) { types.add(type); }
	public void addInteraction(double mult, TypeData type)
	{ 
		ArrayList<TypeData> interaction = interactions.get(mult);
		interaction.add(type);
	}
	
	public ArrayList<TypeData> getTypes() { return types; }
	public ArrayList<TypeData> getInteractionByMultiplier(Double mult) { return interactions.get(mult); }
	
	public String typesToString()
	{
		StringBuilder builder = new StringBuilder();
		
		for(TypeData type : types)
			builder.append("/"+type.toProperName());
		
		return builder.substring(1);
	}
	
	public Optional<String> interactionToString(Double mult)
	{
		if(interactions.get(mult).isEmpty())
			return Optional.empty();
		
		StringBuilder builder = new StringBuilder();
		
		for(TypeData type : interactions.get(mult))
			builder.append(", "+type.toProperName());
		
		return Optional.of(builder.substring(2));
	}
}
