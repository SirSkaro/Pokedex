
package skaro.pokeflex.objects.berry;

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
    "flavor",
    "potency"
})
public class Flavor {

    @JsonProperty("flavor")
    private Flavor_ flavor;
    @JsonProperty("potency")
    private int potency;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("flavor")
    public Flavor_ getFlavor() {
        return flavor;
    }

    @JsonProperty("flavor")
    public void setFlavor(Flavor_ flavor) {
        this.flavor = flavor;
    }

    @JsonProperty("potency")
    public int getPotency() {
        return potency;
    }

    @JsonProperty("potency")
    public void setPotency(int potency) {
        this.potency = potency;
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
