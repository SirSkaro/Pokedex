
package skaro.pokeflex.objects.evolution_chain;

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
    "min_level",
    "min_beauty",
    "time_of_day",
    "gender",
    "relative_physical_stats",
    "needs_overworld_rain",
    "turn_upside_down",
    "item",
    "trigger",
    "known_move_type",
    "min_affection",
    "party_type",
    "trade_species",
    "party_species",
    "min_happiness",
    "held_item",
    "known_move",
    "location"
})
public class EvolutionDetail_ {

    @JsonProperty("min_level")
    private int minLevel;
    @JsonProperty("min_beauty")
    private int minBeauty;
    @JsonProperty("time_of_day")
    private String timeOfDay;
    @JsonProperty("gender")
    private int gender;
    @JsonProperty("relative_physical_stats")
    private int relativePhysicalStats;
    @JsonProperty("needs_overworld_rain")
    private boolean needsOverworldRain;
    @JsonProperty("turn_upside_down")
    private boolean turnUpsideDown;
    @JsonProperty("item")
    private Item_ item;
    @JsonProperty("trigger")
    private Trigger_ trigger;
    @JsonProperty("known_move_type")
    private KnownMoveType_ knownMoveType;
    @JsonProperty("min_affection")
    private int minAffection;
    @JsonProperty("party_type")
    private PartyType_ partyType;
    @JsonProperty("trade_species")
    private TradeSpecies_ tradeSpecies;
    @JsonProperty("party_species")
    private PartySpecies_ partySpecies;
    @JsonProperty("min_happiness")
    private int minHappiness;
    @JsonProperty("held_item")
    private HeldItem_ heldItem;
    @JsonProperty("known_move")
    private KnownMove_ knownMove;
    @JsonProperty("location")
    private Location_ location;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("min_level")
    public int getMinLevel() {
        return minLevel;
    }

    @JsonProperty("min_level")
    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    @JsonProperty("min_beauty")
    public int getMinBeauty() {
        return minBeauty;
    }

    @JsonProperty("min_beauty")
    public void setMinBeauty(int minBeauty) {
        this.minBeauty = minBeauty;
    }

    @JsonProperty("time_of_day")
    public String getTimeOfDay() {
        return timeOfDay;
    }

    @JsonProperty("time_of_day")
    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    @JsonProperty("gender")
    public int getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(int gender) {
        this.gender = gender;
    }

    @JsonProperty("relative_physical_stats")
    public int getRelativePhysicalStats() {
        return relativePhysicalStats;
    }

    @JsonProperty("relative_physical_stats")
    public void setRelativePhysicalStats(int relativePhysicalStats) {
        this.relativePhysicalStats = relativePhysicalStats;
    }

    @JsonProperty("needs_overworld_rain")
    public boolean isNeedsOverworldRain() {
        return needsOverworldRain;
    }

    @JsonProperty("needs_overworld_rain")
    public void setNeedsOverworldRain(boolean needsOverworldRain) {
        this.needsOverworldRain = needsOverworldRain;
    }

    @JsonProperty("turn_upside_down")
    public boolean isTurnUpsideDown() {
        return turnUpsideDown;
    }

    @JsonProperty("turn_upside_down")
    public void setTurnUpsideDown(boolean turnUpsideDown) {
        this.turnUpsideDown = turnUpsideDown;
    }

    @JsonProperty("item")
    public Item_ getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(Item_ item) {
        this.item = item;
    }

    @JsonProperty("trigger")
    public Trigger_ getTrigger() {
        return trigger;
    }

    @JsonProperty("trigger")
    public void setTrigger(Trigger_ trigger) {
        this.trigger = trigger;
    }

    @JsonProperty("known_move_type")
    public KnownMoveType_ getKnownMoveType() {
        return knownMoveType;
    }

    @JsonProperty("known_move_type")
    public void setKnownMoveType(KnownMoveType_ knownMoveType) {
        this.knownMoveType = knownMoveType;
    }

    @JsonProperty("min_affection")
    public int getMinAffection() {
        return minAffection;
    }

    @JsonProperty("min_affection")
    public void setMinAffection(int minAffection) {
        this.minAffection = minAffection;
    }

    @JsonProperty("party_type")
    public PartyType_ getPartyType() {
        return partyType;
    }

    @JsonProperty("party_type")
    public void setPartyType(PartyType_ partyType) {
        this.partyType = partyType;
    }

    @JsonProperty("trade_species")
    public TradeSpecies_ getTradeSpecies() {
        return tradeSpecies;
    }

    @JsonProperty("trade_species")
    public void setTradeSpecies(TradeSpecies_ tradeSpecies) {
        this.tradeSpecies = tradeSpecies;
    }

    @JsonProperty("party_species")
    public PartySpecies_ getPartySpecies() {
        return partySpecies;
    }

    @JsonProperty("party_species")
    public void setPartySpecies(PartySpecies_ partySpecies) {
        this.partySpecies = partySpecies;
    }

    @JsonProperty("min_happiness")
    public int getMinHappiness() {
        return minHappiness;
    }

    @JsonProperty("min_happiness")
    public void setMinHappiness(int minHappiness) {
        this.minHappiness = minHappiness;
    }

    @JsonProperty("held_item")
    public HeldItem_ getHeldItem() {
        return heldItem;
    }

    @JsonProperty("held_item")
    public void setHeldItem(HeldItem_ heldItem) {
        this.heldItem = heldItem;
    }

    @JsonProperty("known_move")
    public KnownMove_ getKnownMove() {
        return knownMove;
    }

    @JsonProperty("known_move")
    public void setKnownMove(KnownMove_ knownMove) {
        this.knownMove = knownMove;
    }

    @JsonProperty("location")
    public Location_ getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location_ location) {
        this.location = location;
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
