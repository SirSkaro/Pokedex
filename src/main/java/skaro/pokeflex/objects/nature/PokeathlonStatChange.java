
package skaro.pokeflex.objects.nature;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "pokeathlon_stat", "max_change" })
public class PokeathlonStatChange {

	@JsonProperty("pokeathlon_stat")
	private PokeathlonStat pokeathlonStat;
	@JsonProperty("max_change")
	private int maxChange;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("pokeathlon_stat")
	public PokeathlonStat getPokeathlonStat() {
		return pokeathlonStat;
	}

	@JsonProperty("pokeathlon_stat")
	public void setPokeathlonStat(PokeathlonStat pokeathlonStat) {
		this.pokeathlonStat = pokeathlonStat;
	}

	@JsonProperty("max_change")
	public int getMaxChange() {
		return maxChange;
	}

	@JsonProperty("max_change")
	public void setMaxChange(int maxChange) {
		this.maxChange = maxChange;
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
