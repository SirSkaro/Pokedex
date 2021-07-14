
package skaro.pokeflex.objects.ability;

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
    "flavor_text",
    "language",
    "version_group"
})
public class FlavorTextEntry {

    @JsonProperty("flavor_text")
    private String flavorText;
    @JsonProperty("language")
    private Language__ language;
    @JsonProperty("version_group")
    private VersionGroup versionGroup;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("flavor_text")
    public String getFlavorText() {
        return flavorText;
    }

    @JsonProperty("flavor_text")
    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    @JsonProperty("language")
    public Language__ getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Language__ language) {
        this.language = language;
    }

    @JsonProperty("version_group")
    public VersionGroup getVersionGroup() {
        return versionGroup;
    }

    @JsonProperty("version_group")
    public void setVersionGroup(VersionGroup versionGroup) {
        this.versionGroup = versionGroup;
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
