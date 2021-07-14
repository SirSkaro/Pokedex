
package skaro.pokeflex.objects.stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "increase",
    "decrease"
})
public class AffectingNatures {

    @JsonProperty("increase")
    private List<Object> increase = null;
    @JsonProperty("decrease")
    private List<Object> decrease = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("increase")
    public List<Object> getIncrease() {
        return increase;
    }

    @JsonProperty("increase")
    public void setIncrease(List<Object> increase) {
        this.increase = increase;
    }

    @JsonProperty("decrease")
    public List<Object> getDecrease() {
        return decrease;
    }

    @JsonProperty("decrease")
    public void setDecrease(List<Object> decrease) {
        this.decrease = decrease;
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
