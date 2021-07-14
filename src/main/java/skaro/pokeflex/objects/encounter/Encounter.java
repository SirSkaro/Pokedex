
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

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "encounter_potential"
})
public class Encounter implements IFlexObject{

    @JsonProperty("encounter_potential")
    private List<EncounterPotential> encounterPotential = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("encounter_potential")
    public List<EncounterPotential> getEncounterPotential() {
        return encounterPotential;
    }

    @JsonProperty("encounter_potential")
    public void setEncounterPotential(List<EncounterPotential> encounterPotential) {
        this.encounterPotential = encounterPotential;
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
