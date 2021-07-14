
package skaro.pokeflex.objects.pokemon_shape;

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
@JsonPropertyOrder({
    "pokemon_species",
    "names",
    "id",
    "awesome_names",
    "name"
})
public class PokemonShape implements IFlexObject {

    @JsonProperty("pokemon_species")
    private List<PokemonSpecy> pokemonSpecies = null;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("awesome_names")
    private List<AwesomeName> awesomeNames = null;
    @JsonProperty("name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pokemon_species")
    public List<PokemonSpecy> getPokemonSpecies() {
        return pokemonSpecies;
    }

    @JsonProperty("pokemon_species")
    public void setPokemonSpecies(List<PokemonSpecy> pokemonSpecies) {
        this.pokemonSpecies = pokemonSpecies;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("awesome_names")
    public List<AwesomeName> getAwesomeNames() {
        return awesomeNames;
    }

    @JsonProperty("awesome_names")
    public void setAwesomeNames(List<AwesomeName> awesomeNames) {
        this.awesomeNames = awesomeNames;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
