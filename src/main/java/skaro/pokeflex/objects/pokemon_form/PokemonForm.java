
package skaro.pokeflex.objects.pokemon_form;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "is_battle_only",
    "sprites",
    "version_group",
    "form_order",
    "is_mega",
    "form_names",
    "id",
    "is_default",
    "names",
    "form_name",
    "pokemon",
    "order",
    "name"
})
public class PokemonForm implements IFlexObject {

    @JsonProperty("is_battle_only")
    private boolean isBattleOnly;
    @JsonProperty("sprites")
    private Sprites sprites;
    @JsonProperty("version_group")
    private VersionGroup versionGroup;
    @JsonProperty("form_order")
    private int formOrder;
    @JsonProperty("is_mega")
    private boolean isMega;
    @JsonProperty("form_names")
    private List<FormName> formNames = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("is_default")
    private boolean isDefault;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("form_name")
    private String formName;
    @JsonProperty("pokemon")
    private Pokemon pokemon;
    @JsonProperty("order")
    private int order;
    @JsonProperty("name")
    private String name;

    @JsonProperty("is_battle_only")
    public boolean isIsBattleOnly() {
        return isBattleOnly;
    }

    @JsonProperty("is_battle_only")
    public void setIsBattleOnly(boolean isBattleOnly) {
        this.isBattleOnly = isBattleOnly;
    }

    @JsonProperty("sprites")
    public Sprites getSprites() {
        return sprites;
    }

    @JsonProperty("sprites")
    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    @JsonProperty("version_group")
    public VersionGroup getVersionGroup() {
        return versionGroup;
    }

    @JsonProperty("version_group")
    public void setVersionGroup(VersionGroup versionGroup) {
        this.versionGroup = versionGroup;
    }

    @JsonProperty("form_order")
    public int getFormOrder() {
        return formOrder;
    }

    @JsonProperty("form_order")
    public void setFormOrder(int formOrder) {
        this.formOrder = formOrder;
    }

    @JsonProperty("is_mega")
    public boolean isIsMega() {
        return isMega;
    }

    @JsonProperty("is_mega")
    public void setIsMega(boolean isMega) {
        this.isMega = isMega;
    }

    @JsonProperty("form_names")
    public List<FormName> getFormNames() {
        return formNames;
    }

    @JsonProperty("form_names")
    public void setFormNames(List<FormName> formNames) {
        this.formNames = formNames;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("is_default")
    public boolean isIsDefault() {
        return isDefault;
    }

    @JsonProperty("is_default")
    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @JsonProperty("names")
    public List<Name> getNames() {
        return names;
    }

    @JsonProperty("names")
    public void setNames(List<Name> names) {
        this.names = names;
    }

    @JsonProperty("form_name")
    public String getFormName() {
        return formName;
    }

    @JsonProperty("form_name")
    public void setFormName(String formName) {
        this.formName = formName;
    }

    @JsonProperty("pokemon")
    public Pokemon getPokemon() {
        return pokemon;
    }

    @JsonProperty("pokemon")
    public void setPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
    }

    @JsonProperty("order")
    public int getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(int order) {
        this.order = order;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getFormInLanguage(String lang)
    {
    	for(FormName name : this.getFormNames())
    	{
    		if(name.getLanguage().getName().equals(lang))
    			return name.getName();
    	}
    	
    	return this.formName; //default English form
    }
    
}
