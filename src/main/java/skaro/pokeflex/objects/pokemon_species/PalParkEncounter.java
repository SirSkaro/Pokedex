
package skaro.pokeflex.objects.pokemon_species;

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
    "rate",
    "base_score",
    "area"
})
public class PalParkEncounter {

    @JsonProperty("rate")
    private int rate;
    @JsonProperty("base_score")
    private int baseScore;
    @JsonProperty("area")
    private Area area;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("rate")
    public int getRate() {
        return rate;
    }

    @JsonProperty("rate")
    public void setRate(int rate) {
        this.rate = rate;
    }

    @JsonProperty("base_score")
    public int getBaseScore() {
        return baseScore;
    }

    @JsonProperty("base_score")
    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    @JsonProperty("area")
    public Area getArea() {
        return area;
    }

    @JsonProperty("area")
    public void setArea(Area area) {
        this.area = area;
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
