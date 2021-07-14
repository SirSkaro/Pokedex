
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
    "version_details",
    "location_area"
})
public class EncounterPotential {

    @JsonProperty("version_details")
    private List<VersionDetail> versionDetails = null;
    @JsonProperty("location_area")
    private LocationArea locationArea;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("version_details")
    public List<VersionDetail> getVersionDetails() {
        return versionDetails;
    }

    @JsonProperty("version_details")
    public void setVersionDetails(List<VersionDetail> versionDetails) {
        this.versionDetails = versionDetails;
    }

    @JsonProperty("location_area")
    public LocationArea getLocationArea() {
        return locationArea;
    }

    @JsonProperty("location_area")
    public void setLocationArea(LocationArea locationArea) {
        this.locationArea = locationArea;
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
