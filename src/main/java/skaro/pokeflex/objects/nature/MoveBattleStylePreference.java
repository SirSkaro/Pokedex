
package skaro.pokeflex.objects.nature;

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
    "high_hp_preference",
    "low_hp_preference",
    "move_battle_style"
})
public class MoveBattleStylePreference {

    @JsonProperty("high_hp_preference")
    private int highHpPreference;
    @JsonProperty("low_hp_preference")
    private int lowHpPreference;
    @JsonProperty("move_battle_style")
    private MoveBattleStyle moveBattleStyle;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("high_hp_preference")
    public int getHighHpPreference() {
        return highHpPreference;
    }

    @JsonProperty("high_hp_preference")
    public void setHighHpPreference(int highHpPreference) {
        this.highHpPreference = highHpPreference;
    }

    @JsonProperty("low_hp_preference")
    public int getLowHpPreference() {
        return lowHpPreference;
    }

    @JsonProperty("low_hp_preference")
    public void setLowHpPreference(int lowHpPreference) {
        this.lowHpPreference = lowHpPreference;
    }

    @JsonProperty("move_battle_style")
    public MoveBattleStyle getMoveBattleStyle() {
        return moveBattleStyle;
    }

    @JsonProperty("move_battle_style")
    public void setMoveBattleStyle(MoveBattleStyle moveBattleStyle) {
        this.moveBattleStyle = moveBattleStyle;
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
