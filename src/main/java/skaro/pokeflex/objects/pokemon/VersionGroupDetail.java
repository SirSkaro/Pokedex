
package skaro.pokeflex.objects.pokemon;

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
    "move_learn_method",
    "level_learned_at",
    "version_group"
})
public class VersionGroupDetail {

    @JsonProperty("move_learn_method")
    private MoveLearnMethod moveLearnMethod;
    @JsonProperty("level_learned_at")
    private int levelLearnedAt;
    @JsonProperty("version_group")
    private VersionGroup versionGroup;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("move_learn_method")
    public MoveLearnMethod getMoveLearnMethod() {
        return moveLearnMethod;
    }

    @JsonProperty("move_learn_method")
    public void setMoveLearnMethod(MoveLearnMethod moveLearnMethod) {
        this.moveLearnMethod = moveLearnMethod;
    }

    @JsonProperty("level_learned_at")
    public int getLevelLearnedAt() {
        return levelLearnedAt;
    }

    @JsonProperty("level_learned_at")
    public void setLevelLearnedAt(int levelLearnedAt) {
        this.levelLearnedAt = levelLearnedAt;
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
