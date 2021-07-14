
package skaro.pokeflex.objects.growth_rate;

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
    "experience",
    "level"
})
public class Level {

    @JsonProperty("experience")
    private int experience;
    @JsonProperty("level")
    private int level;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("experience")
    public int getExperience() {
        return experience;
    }

    @JsonProperty("experience")
    public void setExperience(int experience) {
        this.experience = experience;
    }

    @JsonProperty("level")
    public int getLevel() {
        return level;
    }

    @JsonProperty("level")
    public void setLevel(int level) {
        this.level = level;
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
