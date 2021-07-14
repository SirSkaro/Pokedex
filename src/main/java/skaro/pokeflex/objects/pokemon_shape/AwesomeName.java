
package skaro.pokeflex.objects.pokemon_shape;

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
    "language",
    "awesome_name"
})
public class AwesomeName {

    @JsonProperty("language")
    private Language_ language;
    @JsonProperty("awesome_name")
    private String awesomeName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("language")
    public Language_ getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Language_ language) {
        this.language = language;
    }

    @JsonProperty("awesome_name")
    public String getAwesomeName() {
        return awesomeName;
    }

    @JsonProperty("awesome_name")
    public void setAwesomeName(String awesomeName) {
        this.awesomeName = awesomeName;
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
