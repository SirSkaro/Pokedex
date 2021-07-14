
package skaro.pokeflex.objects.machine;

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
    "item",
    "move",
    "id",
    "version_group"
})
public class Machine implements IFlexObject {

    @JsonProperty("item")
    private Item item;
    @JsonProperty("move")
    private Move move;
    @JsonProperty("id")
    private int id;
    @JsonProperty("version_group")
    private VersionGroup versionGroup;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("item")
    public Item getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(Item item) {
        this.item = item;
    }

    @JsonProperty("move")
    public Move getMove() {
        return move;
    }

    @JsonProperty("move")
    public void setMove(Move move) {
        this.move = move;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
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
