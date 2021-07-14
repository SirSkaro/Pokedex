
package skaro.pokeflex.objects.pokemon;

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
    "back_female",
    "back_shiny_female",
    "back_default",
    "front_female",
    "front_shiny_female",
    "back_shiny",
    "front_default",
    "front_shiny"
})
public class Sprites {

    @JsonProperty("back_female")
    private Object backFemale;
    @JsonProperty("back_shiny_female")
    private Object backShinyFemale;
    @JsonProperty("back_default")
    private String backDefault;
    @JsonProperty("front_female")
    private String frontFemale;
    @JsonProperty("front_shiny_female")
    private String frontShinyFemale;
    @JsonProperty("back_shiny")
    private String backShiny;
    @JsonProperty("front_default")
    private String frontDefault;
    @JsonProperty("front_shiny")
    private String frontShiny;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("back_female")
    public Object getBackFemale() {
        return backFemale;
    }

    @JsonProperty("back_female")
    public void setBackFemale(Object backFemale) {
        this.backFemale = backFemale;
    }

    @JsonProperty("back_shiny_female")
    public Object getBackShinyFemale() {
        return backShinyFemale;
    }

    @JsonProperty("back_shiny_female")
    public void setBackShinyFemale(Object backShinyFemale) {
        this.backShinyFemale = backShinyFemale;
    }

    @JsonProperty("back_default")
    public String getBackDefault() {
        return backDefault;
    }

    @JsonProperty("back_default")
    public void setBackDefault(String backDefault) {
        this.backDefault = backDefault;
    }

    @JsonProperty("front_female")
    public String getFrontFemale() {
        return frontFemale;
    }

    @JsonProperty("front_female")
    public void setFrontFemale(String frontFemale) {
        this.frontFemale = frontFemale;
    }

    @JsonProperty("front_shiny_female")
    public String getFrontShinyFemale() {
        return frontShinyFemale;
    }

    @JsonProperty("front_shiny_female")
    public void setFrontShinyFemale(String frontShinyFemale) {
        this.frontShinyFemale = frontShinyFemale;
    }

    @JsonProperty("back_shiny")
    public String getBackShiny() {
        return backShiny;
    }

    @JsonProperty("back_shiny")
    public void setBackShiny(String backShiny) {
        this.backShiny = backShiny;
    }

    @JsonProperty("front_default")
    public String getFrontDefault() {
        return frontDefault;
    }

    @JsonProperty("front_default")
    public void setFrontDefault(String frontDefault) {
        this.frontDefault = frontDefault;
    }

    @JsonProperty("front_shiny")
    public String getFrontShiny() {
        return frontShiny;
    }

    @JsonProperty("front_shiny")
    public void setFrontShiny(String frontShiny) {
        this.frontShiny = frontShiny;
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
