
package skaro.pokeflex.objects.evolution_chain;

import java.util.HashMap;
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
    "baby_trigger_item",
    "id",
    "chain"
})
public class EvolutionChain implements IFlexObject {

    @JsonProperty("baby_trigger_item")
    private Object babyTriggerItem;
    @JsonProperty("id")
    private int id;
    @JsonProperty("chain")
    private Chain chain;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("baby_trigger_item")
    public Object getBabyTriggerItem() {
        return babyTriggerItem;
    }

    @JsonProperty("baby_trigger_item")
    public void setBabyTriggerItem(Object babyTriggerItem) {
        this.babyTriggerItem = babyTriggerItem;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("chain")
    public Chain getChain() {
        return chain;
    }

    @JsonProperty("chain")
    public void setChain(Chain chain) {
        this.chain = chain;
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
