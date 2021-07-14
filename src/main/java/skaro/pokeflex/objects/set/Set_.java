
package skaro.pokeflex.objects.set;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "abilities",
    "description",
    "evs",
    "format",
    "items",
    "ivs",
    "moves",
    "name",
    "natures",
    "showdown"
})
public class Set_ {

    @JsonProperty("abilities")
    private List<String> abilities = null;
    @JsonProperty("description")
    private String description;
    @JsonProperty("evs")
    private List<Ev> evs = null;
    @JsonProperty("format")
    private String format;
    @JsonProperty("items")
    private List<String> items = null;
    @JsonProperty("ivs")
    private List<Iv> ivs = null;
    @JsonProperty("moves")
    private List<List<String>> moves = null;
    @JsonProperty("name")
    private String name;
    @JsonProperty("natures")
    private List<String> natures = null;
    @JsonProperty("showdown")
    private String showdown;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("abilities")
    public List<String> getAbilities() {
        return abilities;
    }

    @JsonProperty("abilities")
    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("evs")
    public List<Ev> getEvs() {
        return evs;
    }

    @JsonProperty("evs")
    public void setEvs(List<Ev> evs) {
        this.evs = evs;
    }

    @JsonProperty("format")
    public String getFormat() {
        return format;
    }

    @JsonProperty("format")
    public void setFormat(String format) {
        this.format = format;
    }

    @JsonProperty("items")
    public List<String> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(List<String> items) {
        this.items = items;
    }

    @JsonProperty("ivs")
    public List<Iv> getIvs() {
        return ivs;
    }

    @JsonProperty("ivs")
    public void setIvs(List<Iv> ivs) {
        this.ivs = ivs;
    }

    @JsonProperty("moves")
    public List<List<String>> getMoves() {
        return moves;
    }

    @JsonProperty("moves")
    public void setMoves(List<List<String>> moves) {
        this.moves = moves;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("natures")
    public List<String> getNatures() {
        return natures;
    }

    @JsonProperty("natures")
    public void setNatures(List<String> natures) {
        this.natures = natures;
    }

    @JsonProperty("showdown")
    public String getShowdown() {
        return showdown;
    }

    @JsonProperty("showdown")
    public void setShowdown(String showdown) {
        this.showdown = showdown;
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
