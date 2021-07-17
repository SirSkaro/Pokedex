
package skaro.pokeflex.objects.card;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cards implements IFlexObject {

    @JsonProperty("data")
    private List<Card_> cards = List.of();

    @JsonProperty("data")
    public List<Card_> getCards() {
        return cards;
    }

    @JsonProperty("data")
    public void setCards(List<Card_> cards) {
        this.cards = cards;
    }

}
