
package skaro.pokeflex.objects.ability;

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
    "effect_changes",
    "name",
    "generation",
    "pokemon",
    "is_main_series",
    "effect_entries",
    "names",
    "flavor_text_entries",
    "id",
    "rating",
    "ldesc",
    "sdesc"
})
public class Ability implements IFlexObject {

    @JsonProperty("effect_changes")
    private List<Object> effectChanges = null;
    @JsonProperty("name")
    private String name;
    @JsonProperty("generation")
    private Generation generation;
    @JsonProperty("pokemon")
    private List<Pokemon> pokemon = null;
    @JsonProperty("is_main_series")
    private boolean isMainSeries;
    @JsonProperty("effect_entries")
    private List<EffectEntry> effectEntries = null;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("rating")
    private String rating;
    @JsonProperty("ldesc")
    private String ldesc;
    @JsonProperty("sdesc")
    private String sdesc;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("effect_changes")
    public List<Object> getEffectChanges() {
        return effectChanges;
    }

    @JsonProperty("effect_changes")
    public void setEffectChanges(List<Object> effectChanges) {
        this.effectChanges = effectChanges;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("generation")
    public Generation getGeneration() {
        return generation;
    }

    @JsonProperty("generation")
    public void setGeneration(Generation generation) {
        this.generation = generation;
    }

    @JsonProperty("pokemon")
    public List<Pokemon> getPokemon() {
        return pokemon;
    }

    @JsonProperty("pokemon")
    public void setPokemon(List<Pokemon> pokemon) {
        this.pokemon = pokemon;
    }

    @JsonProperty("is_main_series")
    public boolean isIsMainSeries() {
        return isMainSeries;
    }

    @JsonProperty("is_main_series")
    public void setIsMainSeries(boolean isMainSeries) {
        this.isMainSeries = isMainSeries;
    }

    @JsonProperty("effect_entries")
    public List<EffectEntry> getEffectEntries() {
        return effectEntries;
    }

    @JsonProperty("effect_entries")
    public void setEffectEntries(List<EffectEntry> effectEntries) {
        this.effectEntries = effectEntries;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
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

    @JsonProperty("rating")
    public String getRating() {
        return rating;
    }

    @JsonProperty("rating")
    public void setRating(String rating) {
        this.rating = rating;
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
		
		return null;
	}
	
    public Optional<String> getFlavorTextEntry(String lang, String version)
    {
    	String secondBest = null;
    	String backup = null;
    	
    	for(FlavorTextEntry entry : flavorTextEntries)
    	{
			if(entry.getLanguage().getName().equals(lang) && entry.getVersionGroup().getName().equals(version))
				return Optional.of(entry.getFlavorText());
			else if(entry.getLanguage().getName().equals(lang))
				secondBest = entry.getFlavorText();
			else if(entry.getLanguage().getName().equals("en"))
				backup = entry.getFlavorText();
    	}
    	
    	if(secondBest != null)
    		return Optional.of(secondBest);
    	
    	return Optional.ofNullable(backup);
    }
}
