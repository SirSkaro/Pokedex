package skaro.pokedex.input_processor.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum ArgumentCategory 
{
	NONE{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		NoneArgument argument = new NoneArgument();
		
		argument.setUp(null);
		resultList.add(argument);
		
		return resultList;
	}},
	
	ANY{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		AnyArgument argument = new AnyArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},
	
	POKEMON{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		PokemonArgument argument = new PokemonArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},	
	
	ITEM{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		ItemArgument argument = new ItemArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},	
	
	TYPE{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		TypeArgument argument = new TypeArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},	
	
	MOVE{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		MoveArgument argument = new MoveArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},	
	
	META{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		MetaArgument argument = new MetaArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},	
	
	ABILITY{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		AbilityArgument argument = new AbilityArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},
	
	VERSION{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		VersionArgument argument = new VersionArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},
	
	GEN{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		GenArgument argument = new GenArgument();
		
		argument.setUp(itr.next());
		resultList.add(argument);
		
		return resultList;
	}},
	
	ANY_NONE{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		AbstractArgument argument;
		
		if(!itr.hasNext())
		{
			argument = new NoneArgument();
			argument.setUp(null);
		}
		else
		{
			argument = new AnyArgument();
			argument.setUp(itr.next());
		}
		
		resultList.add(argument);
		return resultList;
	}},	
	
	POKE_ABIL{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		String argToParse = itr.next();
		AbstractArgument pokemonArgument, abilityArgument;
		
		pokemonArgument = new PokemonArgument();
		abilityArgument = new AbilityArgument();
		pokemonArgument.setUp(argToParse);
		abilityArgument.setUp(argToParse);
		
		resultList.add(chooseBestArgument(pokemonArgument, abilityArgument));
		return resultList;
	}},	
	
	POKE_TYPE{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		String argToParse = itr.next();
		AbstractArgument pokemonArgument, typeArgument;
		
		pokemonArgument = new PokemonArgument();
		typeArgument = new TypeArgument();
		pokemonArgument.setUp(argToParse);
		typeArgument.setUp(argToParse);
		
		resultList.add(chooseBestArgument(pokemonArgument, typeArgument));
		return resultList;
	}},	
	
	MOVE_TYPE{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		String argToParse = itr.next();
		AbstractArgument moveArgument, typeArgument;
		
		moveArgument = new MoveArgument();
		typeArgument = new TypeArgument();
		moveArgument.setUp(argToParse);
		typeArgument.setUp(argToParse);
		
		resultList.add(chooseBestArgument(moveArgument, typeArgument));
		return resultList;
	}},	
	
	TYPE_LIST{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		
		while(itr.hasNext())
			resultList.addAll(TYPE.parse(itr));
		
		return resultList;
	}},	
	
	MOVE_LIST{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		
		while(itr.hasNext())
			resultList.addAll(MOVE.parse(itr));
		
		return resultList;
	}},	
	
	POKE_TYPE_LIST{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		
		while(itr.hasNext())
			resultList.addAll(POKE_TYPE.parse(itr));
		
		return resultList;
	}},	
	
	MOVE_TYPE_LIST{public List<AbstractArgument> parse(Iterator<String> itr)
	{
		List<AbstractArgument> resultList = new ArrayList<AbstractArgument>();
		
		while(itr.hasNext())
			resultList.addAll(MOVE_TYPE.parse(itr));
		
		return resultList;
	}};
	
	public abstract List<AbstractArgument> parse(Iterator<String> itr);
	
	private static AbstractArgument chooseBestArgument(AbstractArgument arg1, AbstractArgument arg2)
	{
		// Check if both arguments are not valid and return either one
		if(!arg1.isValid() && !arg2.isValid())
			return arg1;
		
		// Check if only one argument is valid 
		else if(arg1.isValid() && !arg2.isValid())
			return arg1;
		else if(!arg1.isValid() && arg2.isValid())
			return arg2;
		
		// if both arguments are valid, choose one that was not spell checked 
		else if(arg1.isSpellChecked() && !arg2.isSpellChecked())
			return arg2;
		else if(!arg1.isSpellChecked() && arg2.isSpellChecked())
			return arg1;
		
		// if both arguments are valid and both spell checked, return the first argument
		return arg1;
	}
}
