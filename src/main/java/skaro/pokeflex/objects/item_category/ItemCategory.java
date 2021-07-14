
package skaro.pokeflex.objects.item_category;

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
@JsonPropertyOrder({
    "pocket",
    "items",
    "names",
    "id",
    "name"
})
public class ItemCategory implements IFlexObject {

    @JsonProperty("pocket")
    private Pocket pocket;
    @JsonProperty("items")
    private List<Item> items = null;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pocket")
    public Pocket getPocket() {
        return pocket;
    }

    @JsonProperty("pocket")
    public void setPocket(Pocket pocket) {
        this.pocket = pocket;
    }

    @JsonProperty("items")
    public List<Item> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(List<Item> items) {
        this.items = items;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
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

	public String getNameInLanguage(String lang)
	{
		for(Name nm : this.names)
		{
			if(nm.getLanguage().getName().equals(lang))
				return nm.getName();
		}
		
		return this.getName();	//Default to English
	}
    
}
