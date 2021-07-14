
package skaro.pokeflex.objects.pokedex;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "entry_number",
    "pokemon_species"
})
public class PokemonEntry {

    @JsonProperty("entry_number")
    private int entryNumber;
    @JsonProperty("pokemon_species")
    private PokemonSpecies pokemonSpecies;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("entry_number")
    public int getEntryNumber() {
        return entryNumber;
    }

    @JsonProperty("entry_number")
    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    @JsonProperty("pokemon_species")
    public PokemonSpecies getPokemonSpecies() {
        return pokemonSpecies;
    }

    @JsonProperty("pokemon_species")
    public void setPokemonSpecies(PokemonSpecies pokemonSpecies) {
        this.pokemonSpecies = pokemonSpecies;
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
