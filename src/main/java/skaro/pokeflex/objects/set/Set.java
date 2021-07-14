
package skaro.pokeflex.objects.set;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sets",
    "url"
})
public class Set implements IFlexObject {

    @JsonProperty("sets")
    private List<Set_> sets = null;
    @JsonProperty("url")
    private String url;

    @JsonProperty("sets")
    public List<Set_> getSets() {
        return sets;
    }

    @JsonProperty("sets")
    public void setSets(List<Set_> sets) {
        this.sets = sets;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

}
