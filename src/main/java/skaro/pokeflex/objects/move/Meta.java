
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
    "category",
    "healing",
    "max_turns",
    "drain",
    "ailment",
    "stat_chance",
    "flinch_chance",
    "min_hits",
    "ailment_chance",
    "crit_rate",
    "min_turns",
    "max_hits"
})
public class Meta {

    @JsonProperty("category")
    private Category category;
    @JsonProperty("healing")
    private int healing;
    @JsonProperty("max_turns")
    private Object maxTurns;
    @JsonProperty("drain")
    private int drain;
    @JsonProperty("ailment")
    private Ailment ailment;
    @JsonProperty("stat_chance")
    private int statChance;
    @JsonProperty("flinch_chance")
    private int flinchChance;
    @JsonProperty("min_hits")
    private Object minHits;
    @JsonProperty("ailment_chance")
    private int ailmentChance;
    @JsonProperty("crit_rate")
    private int critRate;
    @JsonProperty("min_turns")
    private Object minTurns;
    @JsonProperty("max_hits")
    private Object maxHits;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("category")
    public Category getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(Category category) {
        this.category = category;
    }

    @JsonProperty("healing")
    public int getHealing() {
        return healing;
    }

    @JsonProperty("healing")
    public void setHealing(int healing) {
        this.healing = healing;
    }

    @JsonProperty("max_turns")
    public Object getMaxTurns() {
        return maxTurns;
    }

    @JsonProperty("max_turns")
    public void setMaxTurns(Object maxTurns) {
        this.maxTurns = maxTurns;
    }

    @JsonProperty("drain")
    public int getDrain() {
        return drain;
    }

    @JsonProperty("drain")
    public void setDrain(int drain) {
        this.drain = drain;
    }

    @JsonProperty("ailment")
    public Ailment getAilment() {
        return ailment;
    }

    @JsonProperty("ailment")
    public void setAilment(Ailment ailment) {
        this.ailment = ailment;
    }

    @JsonProperty("stat_chance")
    public int getStatChance() {
        return statChance;
    }

    @JsonProperty("stat_chance")
    public void setStatChance(int statChance) {
        this.statChance = statChance;
    }

    @JsonProperty("flinch_chance")
    public int getFlinchChance() {
        return flinchChance;
    }

    @JsonProperty("flinch_chance")
    public void setFlinchChance(int flinchChance) {
        this.flinchChance = flinchChance;
    }

    @JsonProperty("min_hits")
    public Object getMinHits() {
        return minHits;
    }

    @JsonProperty("min_hits")
    public void setMinHits(Object minHits) {
        this.minHits = minHits;
    }

    @JsonProperty("ailment_chance")
    public int getAilmentChance() {
        return ailmentChance;
    }

    @JsonProperty("ailment_chance")
    public void setAilmentChance(int ailmentChance) {
        this.ailmentChance = ailmentChance;
    }

    @JsonProperty("crit_rate")
    public int getCritRate() {
        return critRate;
    }

    @JsonProperty("crit_rate")
    public void setCritRate(int critRate) {
        this.critRate = critRate;
    }

    @JsonProperty("min_turns")
    public Object getMinTurns() {
        return minTurns;
    }

    @JsonProperty("min_turns")
    public void setMinTurns(Object minTurns) {
        this.minTurns = minTurns;
    }

    @JsonProperty("max_hits")
    public Object getMaxHits() {
        return maxHits;
    }

    @JsonProperty("max_hits")
    public void setMaxHits(Object maxHits) {
        this.maxHits = maxHits;
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
