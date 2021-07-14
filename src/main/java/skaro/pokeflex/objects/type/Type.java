
package skaro.pokeflex.objects.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "generation", "damage_relations", "game_indices", "move_damage_class", "moves", "pokemon",
		"id", "names" })
public class Type implements IFlexObject {

	@JsonProperty("name")
	private String name;
	@JsonProperty("generation")
	private Generation generation;
	@JsonProperty("damage_relations")
	private DamageRelations damageRelations;
	@JsonProperty("game_indices")
	private List<GameIndex> gameIndices = null;
	@JsonProperty("move_damage_class")
	private MoveDamageClass moveDamageClass;
	@JsonProperty("moves")
	private List<Move> moves = null;
	@JsonProperty("pokemon")
	private List<Pokemon> pokemon = null;
	@JsonProperty("id")
	private int id;
	@JsonProperty("names")
	private List<Name> names = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("generation")
	public Generation getGeneration() {
		return generation;
	}

	@JsonProperty("generation")
	public void setGeneration(Generation generation) {
		this.generation = generation;
	}

	@JsonProperty("damage_relations")
	public DamageRelations getDamageRelations() {
		return damageRelations;
	}

	@JsonProperty("damage_relations")
	public void setDamageRelations(DamageRelations damageRelations) {
		this.damageRelations = damageRelations;
	}

	@JsonProperty("game_indices")
	public List<GameIndex> getGameIndices() {
		return gameIndices;
	}

	@JsonProperty("game_indices")
	public void setGameIndices(List<GameIndex> gameIndices) {
		this.gameIndices = gameIndices;
	}

	@JsonProperty("move_damage_class")
	public MoveDamageClass getMoveDamageClass() {
		return moveDamageClass;
	}

	@JsonProperty("move_damage_class")
	public void setMoveDamageClass(MoveDamageClass moveDamageClass) {
		this.moveDamageClass = moveDamageClass;
	}

	@JsonProperty("moves")
	public List<Move> getMoves() {
		return moves;
	}

	@JsonProperty("moves")
	public void setMoves(List<Move> moves) {
		this.moves = moves;
	}

	@JsonProperty("pokemon")
	public List<Pokemon> getPokemon() {
		return pokemon;
	}

	@JsonProperty("pokemon")
	public void setPokemon(List<Pokemon> pokemon) {
		this.pokemon = pokemon;
	}

	@JsonProperty("id")
	public int getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(int id) {
		this.id = id;
	}

	@JsonProperty("names")
	public List<Name> getNames() {
		return names;
	}

	@JsonProperty("names")
	public void setNames(List<Name> names) {
		this.names = names;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public String getNameInLanguage(String lang)
	{
		for(Name nm : this.names)
		{
			if(nm.getLanguage().getName().equals(lang))
				return nm.getName();
		}
		
		return this.name; //Default to English
	}
	
}
