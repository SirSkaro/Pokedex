
package skaro.pokeflex.objects.pokemon;

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
    "version_group_details",
    "move"
})
public class Move {

    @JsonProperty("version_group_details")
    private List<VersionGroupDetail> versionGroupDetails = null;
    @JsonProperty("move")
    private Move_ move;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("version_group_details")
    public List<VersionGroupDetail> getVersionGroupDetails() {
        return versionGroupDetails;
    }

    @JsonProperty("version_group_details")
    public void setVersionGroupDetails(List<VersionGroupDetail> versionGroupDetails) {
        this.versionGroupDetails = versionGroupDetails;
    }

    @JsonProperty("move")
    public Move_ getMove() {
        return move;
    }

    @JsonProperty("move")
    public void setMove(Move_ move) {
        this.move = move;
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
