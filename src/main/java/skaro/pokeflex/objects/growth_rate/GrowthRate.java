
package skaro.pokeflex.objects.growth_rate;

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
@JsonPropertyOrder({ "name", "levels", "descriptions", "pokemon_species", "formula", "id" })
public class GrowthRate implements IFlexObject {

	@JsonProperty("name")
	private String name;
	@JsonProperty("levels")
	private List<Level> levels = null;
	@JsonProperty("descriptions")
	private List<Description> descriptions = null;
	@JsonProperty("pokemon_species")
	private List<PokemonSpecy> pokemonSpecies = null;
	@JsonProperty("formula")
	private String formula;
	@JsonProperty("id")
	private int id;
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

	@JsonProperty("levels")
	public List<Level> getLevels() {
		return levels;
	}

	@JsonProperty("levels")
	public void setLevels(List<Level> levels) {
		this.levels = levels;
	}

	@JsonProperty("descriptions")
	public List<Description> getDescriptions() {
		return descriptions;
	}

	@JsonProperty("descriptions")
	public void setDescriptions(List<Description> descriptions) {
		this.descriptions = descriptions;
	}

	@JsonProperty("pokemon_species")
	public List<PokemonSpecy> getPokemonSpecies() {
		return pokemonSpecies;
	}

	@JsonProperty("pokemon_species")
	public void setPokemonSpecies(List<PokemonSpecy> pokemonSpecies) {
		this.pokemonSpecies = pokemonSpecies;
	}

	@JsonProperty("formula")
	public String getFormula() {
		return formula;
	}

	@JsonProperty("formula")
	public void setFormula(String formula) {
		this.formula = formula;
	}

	@JsonProperty("id")
	public int getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(int id) {
		this.id = id;
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
		for(Description desc : this.getDescriptions())
		{
			if(desc.getLanguage().getName().equals(lang))
				return desc.getDescription();
		}
		
		return this.getName(); //Default to English
	}
}
