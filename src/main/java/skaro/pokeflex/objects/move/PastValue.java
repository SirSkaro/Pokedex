
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
    "pp",
    "power",
    "effect_entries",
    "effect_chance",
    "type",
    "version_group",
    "accuracy"
})
public class PastValue {

    @JsonProperty("pp")
    private Object pp;
    @JsonProperty("power")
    private int power;
    @JsonProperty("effect_entries")
    private List<Object> effectEntries = null;
    @JsonProperty("effect_chance")
    private Object effectChance;
    @JsonProperty("type")
    private Object type;
    @JsonProperty("version_group")
    private VersionGroup__ versionGroup;
    @JsonProperty("accuracy")
    private Object accuracy;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pp")
    public Object getPp() {
        return pp;
    }

    @JsonProperty("pp")
    public void setPp(Object pp) {
        this.pp = pp;
    }

    @JsonProperty("power")
    public int getPower() {
        return power;
    }

    @JsonProperty("power")
    public void setPower(int power) {
        this.power = power;
    }

    @JsonProperty("effect_entries")
    public List<Object> getEffectEntries() {
        return effectEntries;
    }

    @JsonProperty("effect_entries")
    public void setEffectEntries(List<Object> effectEntries) {
        this.effectEntries = effectEntries;
    }

    @JsonProperty("effect_chance")
    public Object getEffectChance() {
        return effectChance;
    }

    @JsonProperty("effect_chance")
    public void setEffectChance(Object effectChance) {
        this.effectChance = effectChance;
    }

    @JsonProperty("type")
    public Object getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Object type) {
        this.type = type;
    }

    @JsonProperty("version_group")
    public VersionGroup__ getVersionGroup() {
        return versionGroup;
    }

    @JsonProperty("version_group")
    public void setVersionGroup(VersionGroup__ versionGroup) {
        this.versionGroup = versionGroup;
    }

    @JsonProperty("accuracy")
    public Object getAccuracy() {
        return accuracy;
    }

    @JsonProperty("accuracy")
    public void setAccuracy(Object accuracy) {
        this.accuracy = accuracy;
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
