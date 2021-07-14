
package skaro.pokeflex.objects.ability;

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
    "slot",
    "is_hidden",
    "pokemon"
})
public class Pokemon {

    @JsonProperty("slot")
    private int slot;
    @JsonProperty("is_hidden")
    private boolean isHidden;
    @JsonProperty("pokemon")
    private Pokemon_ pokemon;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("slot")
    public int getSlot() {
        return slot;
    }

    @JsonProperty("slot")
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @JsonProperty("is_hidden")
    public boolean isIsHidden() {
        return isHidden;
    }

    @JsonProperty("is_hidden")
    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    @JsonProperty("pokemon")
    public Pokemon_ getPokemon() {
        return pokemon;
    }

    @JsonProperty("pokemon")
    public void setPokemon(Pokemon_ pokemon) {
        this.pokemon = pokemon;
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
