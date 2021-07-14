package skaro.pokeflex.objects.card;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"cards"
})
public class Cards implements IFlexObject {
	@JsonProperty("cards")
	private List<Card_> cards = null;

	@JsonProperty("cards")
	public List<Card_> getCards() {
		return cards;
	}

	@JsonProperty("cards")
	public void setCards(List<Card_> cards) {
		this.cards = cards;
	}
}
