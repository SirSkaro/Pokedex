
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
    "genus",
    "language"
})
public class Genera {

    @JsonProperty("genus")
    private String genus;
    @JsonProperty("language")
    private Language__ language;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("genus")
    public String getGenus() {
        return genus;
    }

    @JsonProperty("genus")
    public void setGenus(String genus) {
        this.genus = genus;
    }

    @JsonProperty("language")
    public Language__ getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Language__ language) {
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
