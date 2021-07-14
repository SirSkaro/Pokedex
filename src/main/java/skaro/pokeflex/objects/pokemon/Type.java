
package skaro.pokeflex.objects.pokemon;

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
    "type"
})
public class Type {

    @JsonProperty("slot")
    private int slot;
    @JsonProperty("type")
    private Type_ type;
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

    @JsonProperty("type")
    public Type_ getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type_ type) {
        this.type = type;
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
