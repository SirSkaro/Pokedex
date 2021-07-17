package skaro.pokeflex.objects.set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "move",
    "type"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Move {

	@JsonProperty("move")
    private String move;
	@JsonProperty("type")
    private String type;
	
	@JsonProperty("move")
	public String getMove() {
		return move;
	}
	@JsonProperty("move")
	public void setMove(String move) {
		this.move = move;
	}
	@JsonProperty("type")
	public String getType() {
		return type;
	}
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}
	
}
