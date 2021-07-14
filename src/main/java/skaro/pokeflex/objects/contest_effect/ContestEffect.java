
package skaro.pokeflex.objects.contest_effect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "effect_entries",
    "jam",
    "appeal",
    "flavor_text_entries",
    "id"
})
public class ContestEffect implements IFlexObject {

    @JsonProperty("effect_entries")
    private List<EffectEntry> effectEntries = null;
    @JsonProperty("jam")
    private int jam;
    @JsonProperty("appeal")
    private int appeal;
    @JsonProperty("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries = null;
    @JsonProperty("id")
    private int id;
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

    @JsonProperty("jam")
    public int getJam() {
        return jam;
    }

    @JsonProperty("jam")
    public void setJam(int jam) {
        this.jam = jam;
    }

    @JsonProperty("appeal")
    public int getAppeal() {
        return appeal;
    }

    @JsonProperty("appeal")
    public void setAppeal(int appeal) {
        this.appeal = appeal;
    }

    @JsonProperty("flavor_text_entries")
    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    @JsonProperty("flavor_text_entries")
    public void setFlavorTextEntries(List<FlavorTextEntry> flavorTextEntries) {
        this.flavorTextEntries = flavorTextEntries;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
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
