
package skaro.pokeflex.objects.stat;

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
    "move",
    "change"
})
public class Increase {

    @JsonProperty("move")
    private Move move;
    @JsonProperty("change")
    private int change;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("move")
    public Move getMove() {
        return move;
    }

    @JsonProperty("move")
    public void setMove(Move move) {
        this.move = move;
    }

    @JsonProperty("change")
    public int getChange() {
        return change;
    }

    @JsonProperty("change")
    public void setChange(int change) {
        this.change = change;
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
