package skaro.pokedex.input_processor.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import skaro.pokedex.input_processor.CommandArgument;
import skaro.pokedex.input_processor.Language;

public enum ArgumentCategory 
{
	NONE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		NoneArgument result = new NoneArgument();
		result.setUp(null, null);
		return Arrays.asList(result);
	}},
	
	ANY{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(AnyArgument.class, itr, lang));
	}},
	
	POKEMON{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(PokemonArgument.class, itr, lang));
	}},	
	
	ITEM{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(ItemArgument.class, itr, lang));
	}},	
	
	TYPE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(TypeArgument.class, itr, lang));
	}},	
	
	MOVE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(MoveArgument.class, itr, lang));
	}},	
	
	ZMOVE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(ZMoveArgument.class, itr, lang));
	}},	
	
	META{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(MetaArgument.class, itr, lang));
	}},	
	
	ABILITY{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(AbilityArgument.class, itr, lang));
	}},
	
	VERSION{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(VersionArgument.class, itr, lang));
	}},
	
	GEN{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		return Arrays.asList(createArgument(GenArgument.class, itr, lang));
	}},
	
	ANY_NONE{public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		if(!itr.hasNext())
			return NONE.parse(null, null);
		return ANY.parse(itr, lang);
	}},	
	
	POKE_ABIL{@SuppressWarnings("unchecked")
	public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> possibleArguments = createArguments(itr, lang, PokemonArgument.class, AbilityArgument.class);
		return Arrays.asList(chooseBestArgument(possibleArguments));
	}},	
	
	POKE_TYPE{@SuppressWarnings("unchecked")
	public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> possibleArguments = createArguments(itr, lang, PokemonArgument.class, TypeArgument.class);
		return Arrays.asList(chooseBestArgument(possibleArguments));
	}},	
	
	MOVE_TYPE{@SuppressWarnings("unchecked")
	public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> possibleArguments = createArguments(itr, lang, MoveArgument.class, TypeArgument.class);
		return Arrays.asList(chooseBestArgument(possibleArguments));
	}},	
	
	TYPE_ZMOVE{@SuppressWarnings("unchecked")
	public List<CommandArgument> parse(Iterator<String> itr, Language lang)
	{
		List<CommandArgument> possibleArguments = createArguments(itr, lang, TypeArgument.class, ZMoveArgument.class);
		return Arrays.asList(chooseBestArgument(possibleArguments));
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
	
	protected <T extends CommandArgument> CommandArgument createArgument(Class<T> clazz, Iterator<String> itr, Language lang)
	{
		try
		{
			CommandArgument result = clazz.newInstance();
			result.setUp(itr.next(), lang);
			return result;
		} 
		catch (Exception e)
		{
			return new NoneArgument();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends CommandArgument> List<CommandArgument> createArguments(Iterator<String> itr, Language lang, Class<? extends T>... clazz)
	{
		List<CommandArgument> result = new ArrayList<>();
		try
		{
			String rawArgument = itr.next();
			
			for(Class<? extends T> argumentClass : clazz)
			{
				CommandArgument commandArgument = argumentClass.newInstance();
				commandArgument.setUp(rawArgument, lang);
				result.add(commandArgument);
			}
			return result;
		} 
		catch (Exception e)
		{
			return Arrays.asList(new NoneArgument());
		}
	}
	
	private static CommandArgument chooseBestArgument(List<CommandArgument> arguments)
	{
		Optional<CommandArgument> possibleBestArgument = arguments.stream()
				.filter(CommandArgument::isValid)
				.filter(argument -> !argument.isSpellChecked())
				.findFirst();
		
		if(possibleBestArgument.isPresent())
			return possibleBestArgument.get();
		
		possibleBestArgument = arguments.stream()
				.filter(CommandArgument::isValid)
				.findFirst();
		
		if(possibleBestArgument.isPresent())
			return possibleBestArgument.get();
				
		return arguments.get(0);
	}
	
}