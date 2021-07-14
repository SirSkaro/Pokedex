
package skaro.pokeflex.objects.stat;

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
    "is_battle_only",
    "names",
    "affecting_natures",
    "characteristics",
    "affecting_moves",
    "move_damage_class",
    "game_index",
    "id",
    "name"
})
public class Stat implements IFlexObject {

    @JsonProperty("is_battle_only")
    private boolean isBattleOnly;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("affecting_natures")
    private AffectingNatures affectingNatures;
    @JsonProperty("characteristics")
    private List<Characteristic> characteristics = null;
    @JsonProperty("affecting_moves")
    private AffectingMoves affectingMoves;
    @JsonProperty("move_damage_class")
    private Object moveDamageClass;
    @JsonProperty("game_index")
    private int gameIndex;
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("is_battle_only")
    public boolean isIsBattleOnly() {
        return isBattleOnly;
    }

    @JsonProperty("is_battle_only")
    public void setIsBattleOnly(boolean isBattleOnly) {
        this.isBattleOnly = isBattleOnly;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("affecting_natures")
    public AffectingNatures getAffectingNatures() {
        return affectingNatures;
    }

    @JsonProperty("affecting_natures")
    public void setAffectingNatures(AffectingNatures affectingNatures) {
        this.affectingNatures = affectingNatures;
    }

    @JsonProperty("characteristics")
    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    @JsonProperty("characteristics")
    public void setCharacteristics(List<Characteristic> characteristics) {
        this.characteristics = characteristics;
    }

    @JsonProperty("affecting_moves")
    public AffectingMoves getAffectingMoves() {
        return affectingMoves;
    }

    @JsonProperty("affecting_moves")
    public void setAffectingMoves(AffectingMoves affectingMoves) {
        this.affectingMoves = affectingMoves;
    }

    @JsonProperty("move_damage_class")
    public Object getMoveDamageClass() {
        return moveDamageClass;
    }

    @JsonProperty("move_damage_class")
    public void setMoveDamageClass(Object moveDamageClass) {
        this.moveDamageClass = moveDamageClass;
    }

    @JsonProperty("game_index")
    public int getGameIndex() {
        return gameIndex;
    }

    @JsonProperty("game_index")
    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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
