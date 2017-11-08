package skaro.pokedex.database_resources;

import skaro.pokedex.data_processor.ICommand.ArgumentCategory;

public interface PokemonDataAccessor {
	
	public boolean inMoveSet(String move, String pokemonId);
	public boolean resourceExists(ArgumentCategory ac, String resource);
	public boolean isPokemon(String maybePoke);
	public boolean isItem(String s);
	public boolean isAbility(String s);
	public boolean isMove(String s);
	public boolean isVersion(String s);
	public boolean isMeta(String s);
	public boolean isType(String s);
	public boolean isRegion(String s);
	public boolean isGen(String s);
	
	public ComplexPokemon getComplexPokemon(String id);
	public ComplexPokemon getRandomComplexPokemon();
	public SimplePokemon getSimplePokemon(String id);
	public SimplePokemon getRandomSimplePokemon();
	
	public SimpleAbility getSimpleAbility(String id);
	public ComplexAbility getComplexAbility(String id);
	
	public ComplexItem getComplexItem(String id);
	public ComplexMove getComplexMove(String id);
	public SimpleMove getSimpleMove(String id);
	
	public PokedexEntry getDexEntry(String pokemonId, String versionId);
	public SetGroup getSetsForPokemon(String pokemonId, String tierId, int genId);
	
	public LocationGroup getLocation(String pokemon, String version);
	
	//TODO: take this out of here
	public String formatForDatabase(String s);
}
