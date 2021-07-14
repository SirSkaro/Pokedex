
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
    "effect_entries",
    "version_group"
})
public class EffectChange {

    @JsonProperty("effect_entries")
    private List<EffectEntry> effectEntries = null;
    @JsonProperty("version_group")
    private VersionGroup versionGroup;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("effect_entries")
    public List<EffectEntry> getEffectEntries() {
        return effectEntries;
    }

    @JsonProperty("effect_entries")
    public void setEffectEntries(List<EffectEntry> effectEntries) {
        this.effectEntries = effectEntries;
    }

    @JsonProperty("version_group")
    public VersionGroup getVersionGroup() {
        return versionGroup;
    }

    @JsonProperty("version_group")
    public void setVersionGroup(VersionGroup versionGroup) {
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
