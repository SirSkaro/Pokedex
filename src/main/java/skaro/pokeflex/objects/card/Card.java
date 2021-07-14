
package skaro.pokeflex.objects.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"card"
})
public class Card implements IFlexObject {
	@JsonProperty("card")
	private Card_ card;

	@JsonProperty("card")
	public Card_ getCard() {
		return card;
	}

	@JsonProperty("card")
	public void setCard(Card_ card) {
		this.card = card;
	}
}
