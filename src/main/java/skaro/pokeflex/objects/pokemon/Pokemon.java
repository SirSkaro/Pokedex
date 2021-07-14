
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

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "forms",
    "abilities",
    "stats",
    "name",
    "weight",
    "moves",
    "sprites",
    "held_items",
    "location_area_encounters",
    "height",
    "is_default",
    "species",
    "id",
    "order",
    "game_indices",
    "base_experience",
    "types",
    "shiny_model",
    "model"
})
public class Pokemon implements IFlexObject {

    @JsonProperty("forms")
    private List<Form> forms = null;
    @JsonProperty("abilities")
    private List<Ability> abilities = null;
    @JsonProperty("stats")
    private List<Stat> stats = null;
    @JsonProperty("name")
    private String name;
    @JsonProperty("weight")
    private int weight;
    @JsonProperty("moves")
    private List<Move> moves = null;
    @JsonProperty("sprites")
    private Sprites sprites;
    @JsonProperty("held_items")
    private List<Object> heldItems = null;
    @JsonProperty("location_area_encounters")
    private String locationAreaEncounters;
    @JsonProperty("height")
    private int height;
    @JsonProperty("is_default")
    private boolean isDefault;
    @JsonProperty("species")
    private Species species;
    @JsonProperty("id")
    private int id;
    @JsonProperty("order")
    private int order;
    @JsonProperty("game_indices")
    private List<GameIndex> gameIndices = null;
    @JsonProperty("base_experience")
    private int baseExperience;
    @JsonProperty("types")
    private List<Type> types = null;
    @JsonProperty("shiny_model")
    private ShinyModel shinyModel;
    @JsonProperty("model")
    private Model model;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("forms")
    public List<Form> getForms() {
        return forms;
    }

    @JsonProperty("forms")
    public void setForms(List<Form> forms) {
        this.forms = forms;
    }

    @JsonProperty("abilities")
    public List<Ability> getAbilities() {
        return abilities;
    }

    @JsonProperty("abilities")
    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    @JsonProperty("stats")
    public List<Stat> getStats() {
        return stats;
    }

    @JsonProperty("stats")
    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("weight")
    public int getWeight() {
        return weight;
    }

    @JsonProperty("weight")
    public void setWeight(int weight) {
        this.weight = weight;
    }

    @JsonProperty("moves")
    public List<Move> getMoves() {
        return moves;
    }

    @JsonProperty("moves")
    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    @JsonProperty("sprites")
    public Sprites getSprites() {
        return sprites;
    }

    @JsonProperty("sprites")
    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    @JsonProperty("held_items")
    public List<Object> getHeldItems() {
        return heldItems;
    }

    @JsonProperty("held_items")
    public void setHeldItems(List<Object> heldItems) {
        this.heldItems = heldItems;
    }

    @JsonProperty("location_area_encounters")
    public String getLocationAreaEncounters() {
        return locationAreaEncounters;
    }

    @JsonProperty("location_area_encounters")
    public void setLocationAreaEncounters(String locationAreaEncounters) {
        this.locationAreaEncounters = locationAreaEncounters;
    }

    @JsonProperty("height")
    public int getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(int height) {
        this.height = height;
    }

    @JsonProperty("is_default")
    public boolean isIsDefault() {
        return isDefault;
    }

    @JsonProperty("is_default")
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @JsonProperty("species")
    public Species getSpecies() {
        return species;
    }

    @JsonProperty("species")
    public void setSpecies(Species species) {
        this.species = species;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("order")
    public int getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(int order) {
        this.order = order;
    }

    @JsonProperty("game_indices")
    public List<GameIndex> getGameIndices() {
        return gameIndices;
    }

    @JsonProperty("game_indices")
    public void setGameIndices(List<GameIndex> gameIndices) {
        this.gameIndices = gameIndices;
    }

    @JsonProperty("base_experience")
    public int getBaseExperience() {
        return baseExperience;
    }

    @JsonProperty("base_experience")
    public void setBaseExperience(int baseExperience) {
        this.baseExperience = baseExperience;
    }

    @JsonProperty("types")
    public List<Type> getTypes() {
        return types;
    }

    @JsonProperty("types")
    public void setTypes(List<Type> types) {
        this.types = types;
    }

    @JsonProperty("shiny_model")
    public ShinyModel getShinyModel() {
        return shinyModel;
    }

    @JsonProperty("shiny_model")
    public void setShinyModel(ShinyModel shinyModel) {
        this.shinyModel = shinyModel;
    }

    @JsonProperty("model")
    public Model getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(Model model) {
        this.model = model;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    public int getStat(String statName)
    {
    	for(Stat stat : this.stats)
    		if(stat.getStat().getName().equals(statName))
    			return stat.getBaseStat();
    	
    	return -1;
    }
    
    public int getEffotStat(String statName)
    {
    	for(Stat stat : this.stats)
    		if(stat.getStat().getName().equals(statName))
    			return stat.getEffort();
    	
    	return -1;
    }

}
