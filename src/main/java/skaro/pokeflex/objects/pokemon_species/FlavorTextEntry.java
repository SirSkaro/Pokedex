
package skaro.pokeflex.objects.pokemon_species;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version",
    "flavor_text",
    "language"
})
public class FlavorTextEntry {

    @JsonProperty("version")
    private Version version;
    @JsonProperty("flavor_text")
    private String flavorText;
    @JsonProperty("language")
    private Language_ language;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("version")
    public Version getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version version) {
        this.version = version;
    }

    @JsonProperty("flavor_text")
    public String getFlavorText() {
        return flavorText;
    }

    @JsonProperty("flavor_text")
    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    @JsonProperty("language")
    public Language_ getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Language_ language) {
        this.language = language;
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
