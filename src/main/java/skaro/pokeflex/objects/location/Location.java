
package skaro.pokeflex.objects.location;

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
    "game_indices",
    "names",
    "id",
    "areas"
})
public class Location implements IFlexObject {

    @JsonProperty("name")
    private String name;
    @JsonProperty("region")
    private Region region;
    @JsonProperty("game_indices")
    private List<GameIndex> gameIndices = null;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("areas")
    private List<Area> areas = null;
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

    @JsonProperty("game_indices")
    public List<GameIndex> getGameIndices() {
        return gameIndices;
    }

    @JsonProperty("game_indices")
    public void setGameIndices(List<GameIndex> gameIndices) {
        this.gameIndices = gameIndices;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("areas")
    public List<Area> getAreas() {
        return areas;
    }

    @JsonProperty("areas")
    public void setAreas(List<Area> areas) {
        this.areas = areas;
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
