
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
    "stat",
    "effort",
    "base_stat"
})
public class Stat {

    @JsonProperty("stat")
    private Stat_ stat;
    @JsonProperty("effort")
    private int effort;
    @JsonProperty("base_stat")
    private int baseStat;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("stat")
    public Stat_ getStat() {
        return stat;
    }

    @JsonProperty("stat")
    public void setStat(Stat_ stat) {
        this.stat = stat;
    }

    @JsonProperty("effort")
    public int getEffort() {
        return effort;
    }

    @JsonProperty("effort")
    public void setEffort(int effort) {
        this.effort = effort;
    }

    @JsonProperty("base_stat")
    public int getBaseStat() {
        return baseStat;
    }

    @JsonProperty("base_stat")
    public void setBaseStat(int baseStat) {
        this.baseStat = baseStat;
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
