
package skaro.pokeflex.objects.region;

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
    "locations",
    "version_groups",
    "names",
    "main_generation",
    "pokedexes",
    "id"
})
public class Region implements IFlexObject {

    @JsonProperty("name")
    private String name;
    @JsonProperty("locations")
    private List<Location> locations = null;
    @JsonProperty("version_groups")
    private List<VersionGroup> versionGroups = null;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("main_generation")
    private MainGeneration mainGeneration;
    @JsonProperty("pokedexes")
    private List<Pokedex> pokedexes = null;
    @JsonProperty("id")
    private int id;
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

    @JsonProperty("locations")
    public List<Location> getLocations() {
        return locations;
    }

    @JsonProperty("locations")
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @JsonProperty("version_groups")
    public List<VersionGroup> getVersionGroups() {
        return versionGroups;
    }

    @JsonProperty("version_groups")
    public void setVersionGroups(List<VersionGroup> versionGroups) {
        this.versionGroups = versionGroups;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("main_generation")
    public MainGeneration getMainGeneration() {
        return mainGeneration;
    }

    @JsonProperty("main_generation")
    public void setMainGeneration(MainGeneration mainGeneration) {
        this.mainGeneration = mainGeneration;
    }

    @JsonProperty("pokedexes")
    public List<Pokedex> getPokedexes() {
        return pokedexes;
    }

    @JsonProperty("pokedexes")
    public void setPokedexes(List<Pokedex> pokedexes) {
        this.pokedexes = pokedexes;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
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
