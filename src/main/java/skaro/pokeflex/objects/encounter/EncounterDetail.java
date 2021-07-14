
package skaro.pokeflex.objects.encounter;

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
    "min_level",
    "max_level",
    "condition_values",
    "chance",
    "method"
})
public class EncounterDetail {

    @JsonProperty("min_level")
    private int minLevel;
    @JsonProperty("max_level")
    private int maxLevel;
    @JsonProperty("condition_values")
    private List<ConditionValue> conditionValues = null;
    @JsonProperty("chance")
    private int chance;
    @JsonProperty("method")
    private Method method;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("min_level")
    public int getMinLevel() {
        return minLevel;
    }

    @JsonProperty("min_level")
    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    @JsonProperty("max_level")
    public int getMaxLevel() {
        return maxLevel;
    }

    @JsonProperty("max_level")
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @JsonProperty("condition_values")
    public List<ConditionValue> getConditionValues() {
        return conditionValues;
    }

    @JsonProperty("condition_values")
    public void setConditionValues(List<ConditionValue> conditionValues) {
        this.conditionValues = conditionValues;
    }

    @JsonProperty("chance")
    public int getChance() {
        return chance;
    }

    @JsonProperty("chance")
    public void setChance(int chance) {
        this.chance = chance;
    }

    @JsonProperty("method")
    public Method getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(Method method) {
        this.method = method;
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
