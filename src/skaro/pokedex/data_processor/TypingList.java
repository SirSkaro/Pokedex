package skaro.pokedex.data_processor;

import skaro.pokeflex.objects.type.Type;

public class TypingList 
{
	Type type1, type2;
	
	public TypingList()
	{	}
	
	public TypingList(Type t1)
	{
		type1 = t1;
	}
	
	public TypingList(Type t1, Type t2)
	{
		type1 = t1;
		type2 = t2;
	}
	
	public boolean isDual() { return type2 == null; }
	public Type getType1() { return type1; }
	public Type getType2() { return type2; }
}
