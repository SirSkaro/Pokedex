
package skaro.pokeflex.objects.location_area;

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
    "version"
})
public class VersionDetail {

    @JsonProperty("rate")
    private int rate;
    @JsonProperty("version")
    private Version version;
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

    @JsonProperty("version")
    public Version getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version version) {
        this.version = version;
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
