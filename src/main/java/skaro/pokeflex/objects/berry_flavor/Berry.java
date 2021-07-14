
package skaro.pokeflex.objects.berry_flavor;

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
    "potency",
    "berry"
})
public class Berry {

    @JsonProperty("potency")
    private int potency;
    @JsonProperty("berry")
    private Berry_ berry;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("potency")
    public int getPotency() {
        return potency;
    }

    @JsonProperty("potency")
    public void setPotency(int potency) {
        this.potency = potency;
    }

    @JsonProperty("berry")
    public Berry_ getBerry() {
        return berry;
    }

    @JsonProperty("berry")
    public void setBerry(Berry_ berry) {
        this.berry = berry;
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
