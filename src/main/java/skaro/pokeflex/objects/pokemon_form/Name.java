
package skaro.pokeflex.objects.pokemon_form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "language"
})
public class Name {

    @JsonProperty("name")
    private String name;
    @JsonProperty("language")
    private Language_ language;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("language")
    public Language_ getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Language_ language) {
        this.language = language;
    }

}
