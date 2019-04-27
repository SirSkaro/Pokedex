package skaro.pokedex.data_processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import skaro.pokeflex.api.IFlexObject;
import skaro.pokeflex.objects.ability.Ability;
import skaro.pokeflex.objects.move.Move;
import skaro.pokeflex.objects.type.Type;

public class SearchCriteriaFilter implements IFlexObject
{
	List<Move> learnableMoves;
	List<Type> types;
	List<Ability> learnableAbilities;
	Set<String> pokemon;
	
	private SearchCriteriaFilter(SearchCriteriaBuilder builder)
	{
		learnableAbilities = builder.learnableAbilities;
		learnableMoves = builder.learnableMoves;
		types = builder.types;
	}
	
	public Set<String> getPokemonThatMeetCriteria()
	{
		if(pokemon != null)
			return pokemon;
		
		pokemon = filterForPokemon();
		return pokemon;
	}
	
	public boolean hasMoreResultsThan(int amount)
	{
		if(pokemon == null)
			pokemon = filterForPokemon();
		
		return pokemon.size() > amount;
	}
	
	private Set<String> filterForPokemon()
	{
		Set<String> result = new HashSet<>();
		if(!types.isEmpty())
			result.addAll(filterByTypeCriteria());
		if(!learnableAbilities.isEmpty())
			result.retainAll(filterByAbilityCriteria());
		
		return result;
	}
	
	private Set<String> filterByTypeCriteria()
	{
		Set<String> result = new HashSet<>();
		
		if(!types.isEmpty())
			result.addAll(getPokemon(types.get(0)));
		
		for(int i = 1; i < types.size(); i++)
		{
			List<String> pokemonWithType = getPokemon(types.get(i));
			result.retainAll(pokemonWithType);
		}
		
		return result;
	}
	
	private Set<String> filterByAbilityCriteria()
	{
		Set<String> result = new HashSet<>();
		
		if(!learnableAbilities.isEmpty())
			result.addAll(getPokemon(learnableAbilities.get(0)));
		
		for(int i = 1; i < learnableAbilities.size(); i++)
		{
			List<String> pokemonWithAbility = getPokemon(learnableAbilities.get(i));
			result.retainAll(pokemonWithAbility);
		}
		
		return result;
	}
	
	private List<String> getPokemon(Ability ability)
	{
		return ability.getPokemon()
				.stream()
				.map(pokemon -> pokemon.getPokemon().getName())
				.collect(Collectors.toList());
	}
	
	private List<String> getPokemon(Type type)
	{
		return type.getPokemon()
				.stream()
				.map(pokemon -> pokemon.getPokemon().getName())
				.collect(Collectors.toList());
	}
	
	public static class SearchCriteriaBuilder
	{
		List<Move> learnableMoves;
		List<Type> types;
		List<Ability> learnableAbilities;
		
		public static SearchCriteriaBuilder newInstance()
		{
			return new SearchCriteriaBuilder();
		}
		
		public SearchCriteriaBuilder()
		{
			learnableMoves = new ArrayList<>();
			types = new ArrayList<>();
			learnableAbilities = new ArrayList<>();
		}
		
		public SearchCriteriaBuilder withMoves(List<Move> moves)
		{
			learnableMoves.addAll(moves);
			return this;
		}
		
		public SearchCriteriaBuilder withTypes(List<Type> types)
		{
			this.types.addAll(types);
			return this;
		}
		
		public SearchCriteriaBuilder withAbilities(List<Ability> abilities)
		{
			learnableAbilities.addAll(abilities);
			return this;
		}
		
		public SearchCriteriaFilter build()
		{
			return new SearchCriteriaFilter(this);
		}
		
	}
}
