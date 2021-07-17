
package skaro.pokeflex.objects.card;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "types",
    "set",
    "number",
    "images",
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card_ {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("types")
    private List<String> types = null;
    @JsonProperty("set")
    private Set set;
    @JsonProperty("number")
    private String number;
    @JsonProperty("images")
    private Images images;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("types")
    public List<String> getTypes() {
        return types;
    }

    @JsonProperty("types")
    public void setTypes(List<String> types) {
        this.types = types;
    }

    @JsonProperty("set")
    public Set getSet() {
        return set;
    }

    @JsonProperty("set")
    public void setSet(Set set) {
        this.set = set;
    }

    @JsonProperty("number")
    public String getNumber() {
        return number;
    }

    @JsonProperty("number")
    public void setNumber(String number) {
        this.number = number;
    }

    @JsonProperty("images")
    public Images getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(Images images) {
        this.images = images;
    }

}
