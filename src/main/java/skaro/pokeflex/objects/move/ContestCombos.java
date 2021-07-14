
package skaro.pokeflex.objects.move;

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
    "super",
    "normal"
})
public class ContestCombos {

    @JsonProperty("super")
    private Super _super;
    @JsonProperty("normal")
    private Normal normal;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("super")
    public Super getSuper() {
        return _super;
    }

    @JsonProperty("super")
    public void setSuper(Super _super) {
        this._super = _super;
    }

    @JsonProperty("normal")
    public Normal getNormal() {
        return normal;
    }

    @JsonProperty("normal")
    public void setNormal(Normal normal) {
        this.normal = normal;
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
