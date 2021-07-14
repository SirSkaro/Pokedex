
package skaro.pokeflex.objects.type;

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
    "generation",
    "game_index"
})
public class GameIndex {

    @JsonProperty("generation")
    private Generation_ generation;
    @JsonProperty("game_index")
    private int gameIndex;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("generation")
    public Generation_ getGeneration() {
        return generation;
    }

    @JsonProperty("generation")
    public void setGeneration(Generation_ generation) {
        this.generation = generation;
    }

    @JsonProperty("game_index")
    public int getGameIndex() {
        return gameIndex;
    }

    @JsonProperty("game_index")
    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
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
