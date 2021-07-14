
package skaro.pokeflex.objects.move_learn_method;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "descriptions",
    "id",
    "name",
    "names",
    "version_groups"
})
public class MoveLearnMethod implements IFlexObject {

    @JsonProperty("descriptions")
    private List<Description> descriptions = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("version_groups")
    private List<VersionGroup> versionGroups = null;

    @JsonProperty("descriptions")
    public List<Description> getDescriptions() {
        return descriptions;
    }

    @JsonProperty("descriptions")
    public void setDescriptions(List<Description> descriptions) {
        this.descriptions = descriptions;
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

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("version_groups")
    public List<VersionGroup> getVersionGroups() {
        return versionGroups;
    }

    @JsonProperty("version_groups")
    public void setVersionGroups(List<VersionGroup> versionGroups) {
        this.versionGroups = versionGroups;
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
