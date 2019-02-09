package skaro.pokedex.input_processor.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public enum ArgumentCategory 
{
	NONE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		NoneArgument argument = new NoneArgument();
		
		argument.setUp(null, null);
		resultList.add(argument);
		
		return resultList;
	}},
	
	ANY{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		AnyArgument argument = new AnyArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},
	
	POKEMON{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		PokemonArgument argument = new PokemonArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},	
	
	ITEM{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		ItemArgument argument = new ItemArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},	
	
	TYPE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		TypeArgument argument = new TypeArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},	
	
	MOVE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		MoveArgument argument = new MoveArgument();
		
		argument.setUp(itr.next(),lang);
		resultList.add(argument);
		
		return resultList;
	}},	
	
	META{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		MetaArgument argument = new MetaArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},	
	
	ABILITY{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		AbilityArgument argument = new AbilityArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},
	
	VERSION{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		VersionArgument argument = new VersionArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},
	
	GEN{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		GenArgument argument = new GenArgument();
		
		argument.setUp(itr.next(), lang);
		resultList.add(argument);
		
		return resultList;
	}},
	
	ANY_NONE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		CommandArgument argument;
		
		if(!itr.hasNext())
		{
			argument = new NoneArgument();
			argument.setUp(null, null);
		}
		else
		{
			argument = new AnyArgument();
			argument.setUp(itr.next(), lang);
		}
		
		resultList.add(argument);
		return resultList;
	}},	
	
	POKE_ABIL{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		String argToParse = itr.next();
		CommandArgument pokemonArgument, abilityArgument;
		
		pokemonArgument = new PokemonArgument();
		abilityArgument = new AbilityArgument();
		pokemonArgument.setUp(argToParse, lang);
		abilityArgument.setUp(argToParse, lang);
		
		resultList.add(chooseBestArgument(pokemonArgument, abilityArgument));
		return resultList;
	}},	
	
	POKE_TYPE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		String argToParse = itr.next();
		CommandArgument pokemonArgument, typeArgument;
		
		pokemonArgument = new PokemonArgument();
		typeArgument = new TypeArgument();
		pokemonArgument.setUp(argToParse, lang);
		typeArgument.setUp(argToParse, lang);
		
		resultList.add(chooseBestArgument(pokemonArgument, typeArgument));
		return resultList;
	}},	
	
	MOVE_TYPE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		String argToParse = itr.next();
		CommandArgument moveArgument, typeArgument;
		
		moveArgument = new MoveArgument();
		typeArgument = new TypeArgument();
		moveArgument.setUp(argToParse, lang);
		typeArgument.setUp(argToParse, lang);
		
		resultList.add(chooseBestArgument(moveArgument, typeArgument));
		return resultList;
	}},	
	
	TYPE_LIST{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		
		while(itr.hasNext())
			resultList.addAll(TYPE.parse(itr, lang));
		
		return resultList;
	}},	
	
	MOVE_LIST{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		
		while(itr.hasNext())
			resultList.addAll(MOVE.parse(itr, lang));
		
		return resultList;
	}},	
	
	POKE_TYPE_LIST{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		
		while(itr.hasNext())
			resultList.addAll(POKE_TYPE.parse(itr, lang));
		
		return resultList;
	}},	
	
	MOVE_TYPE_LIST{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> resultList = new ArrayList<CommandArgument>();
		
		while(itr.hasNext())
			resultList.addAll(MOVE_TYPE.parse(itr, lang));
		
		return resultList;
	}};
	
	public abstract List<CommandArgument> parse(Iterator<String> itr, Language lang);
	
	private static CommandArgument chooseBestArgument(CommandArgument arg1, CommandArgument arg2)
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
