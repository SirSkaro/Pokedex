
package skaro.pokeflex.objects.move;

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
    "use_after",
    "use_before"
})
public class Normal {

    @JsonProperty("use_after")
    private List<UseAfter> useAfter = null;
    @JsonProperty("use_before")
    private Object useBefore;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("use_after")
    public List<UseAfter> getUseAfter() {
        return useAfter;
    }

    @JsonProperty("use_after")
    public void setUseAfter(List<UseAfter> useAfter) {
        this.useAfter = useAfter;
    }

    @JsonProperty("use_before")
    public Object getUseBefore() {
        return useBefore;
    }

    @JsonProperty("use_before")
    public void setUseBefore(Object useBefore) {
        this.useBefore = useBefore;
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
