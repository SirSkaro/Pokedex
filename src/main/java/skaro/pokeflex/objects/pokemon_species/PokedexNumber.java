
package skaro.pokeflex.objects.pokemon_species;

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
    "pokedex"
})
public class PokedexNumber {

    @JsonProperty("entry_number")
    private int entryNumber;
    @JsonProperty("pokedex")
    private Pokedex pokedex;
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

    @JsonProperty("pokedex")
    public Pokedex getPokedex() {
        return pokedex;
    }

    @JsonProperty("pokedex")
    public void setPokedex(Pokedex pokedex) {
        this.pokedex = pokedex;
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
