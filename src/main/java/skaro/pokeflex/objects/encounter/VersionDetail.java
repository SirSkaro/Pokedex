
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
    "max_chance",
    "version",
    "encounter_details"
})
public class VersionDetail {

    @JsonProperty("max_chance")
    private int maxChance;
    @JsonProperty("version")
    private Version version;
    @JsonProperty("encounter_details")
    private List<EncounterDetail> encounterDetails = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("max_chance")
    public int getMaxChance() {
        return maxChance;
    }

    @JsonProperty("max_chance")
    public void setMaxChance(int maxChance) {
        this.maxChance = maxChance;
    }

    @JsonProperty("version")
    public Version getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version version) {
        this.version = version;
    }

    @JsonProperty("encounter_details")
    public List<EncounterDetail> getEncounterDetails() {
        return encounterDetails;
    }

    @JsonProperty("encounter_details")
    public void setEncounterDetails(List<EncounterDetail> encounterDetails) {
        this.encounterDetails = encounterDetails;
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
