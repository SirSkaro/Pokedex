
package skaro.pokeflex.objects.item;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "text", "version_group", "language" })
public class FlavorTextEntry {

	@JsonProperty("text")
	private String text;
	@JsonProperty("version_group")
	private VersionGroup versionGroup;
	@JsonProperty("language")
	private Language__ language;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("text")
	public String getText() {
		return text;
	}

	@JsonProperty("text")
	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty("version_group")
	public VersionGroup getVersionGroup() {
		return versionGroup;
	}

	@JsonProperty("version_group")
	public void setVersionGroup(VersionGroup versionGroup) {
		this.versionGroup = versionGroup;
	}

	@JsonProperty("language")
	public Language__ getLanguage() {
		return language;
	}

	@JsonProperty("language")
	public void setLanguage(Language__ language) {
		this.language = language;
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
