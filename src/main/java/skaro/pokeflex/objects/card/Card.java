package skaro.pokeflex.objects.card;

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
public class Card implements IFlexObject {
	@JsonProperty("data")
	private Card_ datum;
	
	@JsonProperty("data")
	public Card_ getCard() {
		return datum;
	}

	@JsonProperty("data")
	public void setCard(Card_ datum) {
		this.datum = datum;
	}
}
