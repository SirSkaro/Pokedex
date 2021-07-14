
package skaro.pokeflex.objects.location_area;

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
    "names",
    "name",
    "encounter_method_rates",
    "location",
    "pokemon_encounters",
    "id",
    "game_index"
})
public class LocationArea implements IFlexObject {

    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("name")
    private String name;
    @JsonProperty("encounter_method_rates")
    private List<EncounterMethodRate> encounterMethodRates = null;
    @JsonProperty("location")
    private Location location;
    @JsonProperty("pokemon_encounters")
    private List<PokemonEncounter> pokemonEncounters = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("game_index")
    private int gameIndex;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("encounter_method_rates")
    public List<EncounterMethodRate> getEncounterMethodRates() {
        return encounterMethodRates;
    }

    @JsonProperty("encounter_method_rates")
    public void setEncounterMethodRates(List<EncounterMethodRate> encounterMethodRates) {
        this.encounterMethodRates = encounterMethodRates;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("pokemon_encounters")
    public List<PokemonEncounter> getPokemonEncounters() {
        return pokemonEncounters;
    }

    @JsonProperty("pokemon_encounters")
    public void setPokemonEncounters(List<PokemonEncounter> pokemonEncounters) {
        this.pokemonEncounters = pokemonEncounters;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("game_index")
    public int getGameIndex() {
        return gameIndex;
    }

    @JsonProperty("game_index")
    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
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
