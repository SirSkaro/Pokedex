
package skaro.pokeflex.objects.pokedex;

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
    "name",
    "region",
    "version_groups",
    "is_main_series",
    "descriptions",
    "pokemon_entries",
    "id",
    "names"
})
public class Pokedex implements IFlexObject {

    @JsonProperty("name")
    private String name;
    @JsonProperty("region")
    private Region region;
    @JsonProperty("version_groups")
    private List<VersionGroup> versionGroups = null;
    @JsonProperty("is_main_series")
    private boolean isMainSeries;
    @JsonProperty("descriptions")
    private List<Description> descriptions = null;
    @JsonProperty("pokemon_entries")
    private List<PokemonEntry> pokemonEntries = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("names")
    private List<Name> names = null;
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

    @JsonProperty("region")
    public Region getRegion() {
        return region;
    }

    @JsonProperty("region")
    public void setRegion(Region region) {
        this.region = region;
    }

    @JsonProperty("version_groups")
    public List<VersionGroup> getVersionGroups() {
        return versionGroups;
    }

    @JsonProperty("version_groups")
    public void setVersionGroups(List<VersionGroup> versionGroups) {
        this.versionGroups = versionGroups;
    }

    @JsonProperty("is_main_series")
    public boolean isIsMainSeries() {
        return isMainSeries;
    }

    @JsonProperty("is_main_series")
    public void setIsMainSeries(boolean isMainSeries) {
        this.isMainSeries = isMainSeries;
    }

    @JsonProperty("descriptions")
    public List<Description> getDescriptions() {
        return descriptions;
    }

    @JsonProperty("descriptions")
    public void setDescriptions(List<Description> descriptions) {
        this.descriptions = descriptions;
    }

    @JsonProperty("pokemon_entries")
    public List<PokemonEntry> getPokemonEntries() {
        return pokemonEntries;
    }

    @JsonProperty("pokemon_entries")
    public void setPokemonEntries(List<PokemonEntry> pokemonEntries) {
        this.pokemonEntries = pokemonEntries;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
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
