
package skaro.pokeflex.objects.nature;

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
import skaro.pokeflex.objects.ability.Name;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "pokeathlon_stat_changes",
    "names",
    "hates_flavor",
    "likes_flavor",
    "decreased_stat",
    "move_battle_style_preferences",
    "id",
    "increased_stat"
})
public class Nature implements IFlexObject {

    @JsonProperty("name")
    private String name;
    @JsonProperty("pokeathlon_stat_changes")
    private List<PokeathlonStatChange> pokeathlonStatChanges = null;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("hates_flavor")
    private HatesFlavor hatesFlavor;
    @JsonProperty("likes_flavor")
    private LikesFlavor likesFlavor;
    @JsonProperty("decreased_stat")
    private DecreasedStat decreasedStat;
    @JsonProperty("move_battle_style_preferences")
    private List<MoveBattleStylePreference> moveBattleStylePreferences = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("increased_stat")
    private IncreasedStat increasedStat;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("pokeathlon_stat_changes")
    public List<PokeathlonStatChange> getPokeathlonStatChanges() {
        return pokeathlonStatChanges;
    }

    @JsonProperty("pokeathlon_stat_changes")
    public void setPokeathlonStatChanges(List<PokeathlonStatChange> pokeathlonStatChanges) {
        this.pokeathlonStatChanges = pokeathlonStatChanges;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("hates_flavor")
    public HatesFlavor getHatesFlavor() {
        return hatesFlavor;
    }

    @JsonProperty("hates_flavor")
    public void setHatesFlavor(HatesFlavor hatesFlavor) {
        this.hatesFlavor = hatesFlavor;
    }

    @JsonProperty("likes_flavor")
    public LikesFlavor getLikesFlavor() {
        return likesFlavor;
    }

    @JsonProperty("likes_flavor")
    public void setLikesFlavor(LikesFlavor likesFlavor) {
        this.likesFlavor = likesFlavor;
    }

    @JsonProperty("decreased_stat")
    public DecreasedStat getDecreasedStat() {
        return decreasedStat;
    }

    @JsonProperty("decreased_stat")
    public void setDecreasedStat(DecreasedStat decreasedStat) {
        this.decreasedStat = decreasedStat;
    }

    @JsonProperty("move_battle_style_preferences")
    public List<MoveBattleStylePreference> getMoveBattleStylePreferences() {
        return moveBattleStylePreferences;
    }

    @JsonProperty("move_battle_style_preferences")
    public void setMoveBattleStylePreferences(List<MoveBattleStylePreference> moveBattleStylePreferences) {
        this.moveBattleStylePreferences = moveBattleStylePreferences;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("increased_stat")
    public IncreasedStat getIncreasedStat() {
        return increasedStat;
    }

    @JsonProperty("increased_stat")
    public void setIncreasedStat(IncreasedStat increasedStat) {
        this.increasedStat = increasedStat;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

	public String getNameInLanguage(String lang)
	{
		for(Name nm : this.names)
		{
			if(nm.getLanguage().getName().equals(lang))
				return nm.getName();
		}
		
		return name;
	}
    
}
