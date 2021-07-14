
package skaro.pokeflex.objects.encounter_condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "names", "values", "id", "name" })
public class EncounterCondition implements IFlexObject {

	@JsonProperty("names")
	private List<Name> names = null;
	@JsonProperty("values")
	private List<Value> values = null;
	@JsonProperty("id")
	private int id;
	@JsonProperty("name")
	private String name;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("names")
	public List<Name> getNames() {
		return names;
	}

	@JsonProperty("names")
	public void setNames(List<Name> names) {
		this.names = names;
	}

	@JsonProperty("values")
	public List<Value> getValues() {
		return values;
	}

	@JsonProperty("values")
	public void setValues(List<Value> values) {
		this.values = values;
	}

	@JsonProperty("id")
	public int getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(int id) {
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

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
