
package skaro.pokeflex.objects.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "category",
    "name",
    "fling_effect",
    "effect_entries",
    "held_by_pokemon",
    "sprites",
    "game_indices",
    "baby_trigger_for",
    "cost",
    "names",
    "attributes",
    "flavor_text_entries",
    "id",
    "machines",
    "fling_power",
    "ldesc",
    "sdesc",
    "ng_type",
    "ng_power",
    "debut"
})
public class Item implements IFlexObject {

    @JsonProperty("category")
    private Category category;
    @JsonProperty("name")
    private String name;
    @JsonProperty("fling_effect")
    private Object flingEffect;
    @JsonProperty("effect_entries")
    private List<EffectEntry> effectEntries = null;
    @JsonProperty("held_by_pokemon")
    private List<HeldByPokemon> heldByPokemon = null;
    @JsonProperty("sprites")
    private Sprites sprites;
    @JsonProperty("game_indices")
    private List<GameIndex> gameIndices = null;
    @JsonProperty("baby_trigger_for")
    private Object babyTriggerFor;
    @JsonProperty("cost")
    private int cost;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("attributes")
    private List<Attribute> attributes = null;
    @JsonProperty("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("machines")
    private List<Object> machines = null;
    @JsonProperty("fling_power")
    private int flingPower;
    @JsonProperty("ldesc")
    private String ldesc;
    @JsonProperty("sdesc")
    private String sdesc;
    @JsonProperty("ng_type")
    private String ngType;
    @JsonProperty("ng_power")
    private int ngPower;
    @JsonProperty("debut")
    private int debut;
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("fling_effect")
    public Object getFlingEffect() {
        return flingEffect;
    }

    @JsonProperty("fling_effect")
    public void setFlingEffect(Object flingEffect) {
        this.flingEffect = flingEffect;
    }

    @JsonProperty("effect_entries")
    public List<EffectEntry> getEffectEntries() {
        return effectEntries;
    }

    @JsonProperty("effect_entries")
    public void setEffectEntries(List<EffectEntry> effectEntries) {
        this.effectEntries = effectEntries;
    }

    @JsonProperty("held_by_pokemon")
    public List<HeldByPokemon> getHeldByPokemon() {
        return heldByPokemon;
    }

    @JsonProperty("held_by_pokemon")
    public void setHeldByPokemon(List<HeldByPokemon> heldByPokemon) {
        this.heldByPokemon = heldByPokemon;
    }

    @JsonProperty("sprites")
    public Sprites getSprites() {
        return sprites;
    }

    @JsonProperty("sprites")
    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    @JsonProperty("game_indices")
    public List<GameIndex> getGameIndices() {
        return gameIndices;
    }

    @JsonProperty("game_indices")
    public void setGameIndices(List<GameIndex> gameIndices) {
        this.gameIndices = gameIndices;
    }

    @JsonProperty("baby_trigger_for")
    public Object getBabyTriggerFor() {
        return babyTriggerFor;
    }

    @JsonProperty("baby_trigger_for")
    public void setBabyTriggerFor(Object babyTriggerFor) {
        this.babyTriggerFor = babyTriggerFor;
    }

    @JsonProperty("cost")
    public int getCost() {
        return cost;
    }

    @JsonProperty("cost")
    public void setCost(int cost) {
        this.cost = cost;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("attributes")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("flavor_text_entries")
    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    @JsonProperty("flavor_text_entries")
    public void setFlavorTextEntries(List<FlavorTextEntry> flavorTextEntries) {
        this.flavorTextEntries = flavorTextEntries;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("machines")
    public List<Object> getMachines() {
        return machines;
    }

    @JsonProperty("machines")
    public void setMachines(List<Object> machines) {
        this.machines = machines;
    }

    @JsonProperty("fling_power")
    public int getFlingPower() {
        return flingPower;
    }

    @JsonProperty("fling_power")
    public void setFlingPower(int flingPower) {
        this.flingPower = flingPower;
    }

    @JsonProperty("ldesc")
    public String getLdesc() {
        return ldesc;
    }

    @JsonProperty("ldesc")
    public void setLdesc(String ldesc) {
        this.ldesc = ldesc;
    }

    @JsonProperty("sdesc")
    public String getSdesc() {
        return sdesc;
    }

    @JsonProperty("sdesc")
    public void setSdesc(String sdesc) {
        this.sdesc = sdesc;
    }

    @JsonProperty("ng_type")
    public String getNgType() {
        return ngType;
    }

    @JsonProperty("ng_type")
    public void setNgType(String ngType) {
        this.ngType = ngType;
    }

    @JsonProperty("ng_power")
    public int getNgPower() {
        return ngPower;
    }

    @JsonProperty("ng_power")
    public void setNgPower(int ngPower) {
        this.ngPower = ngPower;
    }

    @JsonProperty("debut")
    public int getDebut() {
        return debut;
    }

    @JsonProperty("debut")
    public void setDebut(int debut) {
        this.debut = debut;
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
		
		return this.getName();	//Default to English
	}
	
    public Optional<String> getFlavorTextEntry(String lang, String version)
    {
    	String secondBest = null;
    	String backup = null;
    	
    	for(FlavorTextEntry entry : flavorTextEntries)
    	{
			if(entry.getLanguage().getName().equals(lang) && entry.getVersionGroup().getName().equals(version))
				return Optional.of(entry.getText());
			else if(entry.getLanguage().getName().equals(lang))
				secondBest = entry.getText();
			else if(entry.getLanguage().getName().equals("en"))
				backup = entry.getText();
    	}
    	
    	if(secondBest != null)
    		return Optional.of(secondBest);
    	
    	return Optional.ofNullable(backup);
    }

}
