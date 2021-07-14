
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
    "machine",
    "version_group"
})
public class Machine {

    @JsonProperty("machine")
    private Machine_ machine;
    @JsonProperty("version_group")
    private VersionGroup_ versionGroup;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("machine")
    public Machine_ getMachine() {
        return machine;
    }

    @JsonProperty("machine")
    public void setMachine(Machine_ machine) {
        this.machine = machine;
    }

    @JsonProperty("version_group")
    public VersionGroup_ getVersionGroup() {
        return versionGroup;
    }

    @JsonProperty("version_group")
    public void setVersionGroup(VersionGroup_ versionGroup) {
        this.versionGroup = versionGroup;
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
