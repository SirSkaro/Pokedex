
package skaro.pokeflex.objects.move;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import skaro.pokeflex.api.IFlexObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "effect_chance",
    "generation",
    "stat_changes",
    "effect_changes",
    "names",
    "id",
    "machines",
    "pp",
    "contest_combos",
    "effect_entries",
    "contest_type",
    "priority",
    "contest_effect",
    "type",
    "accuracy",
    "power",
    "past_values",
    "target",
    "super_contest_effect",
    "name",
    "flavor_text_entries",
    "damage_class",
    "meta",
    "max_pp",
    "ldesc",
    "sdesc",
    "z_power",
    "z_effect",
    "crystal",
    "flags",
    "images",
    "pokemon"
})
public class Move implements IFlexObject {

    @JsonProperty("effect_chance")
    private int effectChance;
    @JsonProperty("generation")
    private Generation generation;
    @JsonProperty("stat_changes")
    private List<Object> statChanges = null;
    @JsonProperty("effect_changes")
    private List<EffectChange> effectChanges = null;
    @JsonProperty("names")
    private List<Name> names = null;
    @JsonProperty("id")
    private int id;
    @JsonProperty("machines")
    private List<Machine> machines = null;
    @JsonProperty("pp")
    private int pp;
    @JsonProperty("contest_combos")
    private ContestCombos contestCombos;
    @JsonProperty("effect_entries")
    private List<EffectEntry_> effectEntries = null;
    @JsonProperty("contest_type")
    private ContestType contestType;
    @JsonProperty("priority")
    private int priority;
    @JsonProperty("contest_effect")
    private ContestEffect contestEffect;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("accuracy")
    private int accuracy;
    @JsonProperty("power")
    private int power;
    @JsonProperty("past_values")
    private List<PastValue> pastValues = null;
    @JsonProperty("target")
    private Target target;
    @JsonProperty("super_contest_effect")
    private SuperContestEffect superContestEffect;
    @JsonProperty("name")
    private String name;
    @JsonProperty("flavor_text_entries")
    private List<FlavorTextEntry> flavorTextEntries = null;
    @JsonProperty("damage_class")
    private DamageClass damageClass;
    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("max_pp")
    private int maxPp;
    @JsonProperty("ldesc")
    private String ldesc;
    @JsonProperty("sdesc")
    private String sdesc;
    @JsonProperty("z_power")
    private int zPower;
    @JsonProperty("z_effect")
    private Object zEffect;
    @JsonProperty("z_boost")
    private Object zBoost;
    @JsonProperty("crystal")
    private String crystal;
    @JsonProperty("flags")
    private List<String> flags = null;
    @JsonProperty("images")
    private List<Image> images = null;
    @JsonProperty("pokemon")
    private List<String> pokemon = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pokemon")
    public List<String> getPokemon() {
        return pokemon;
    }
    
    @JsonProperty("pokemon")
    public void setPokemon(List<String> pokemon) {
        this.pokemon = pokemon;
    }
    
    @JsonProperty("images")
    public List<Image> getImages() {
        return images;
    }
    
    @JsonProperty("images")
    public void setImages(List<Image> images) {
        this.images = images;
    }
    
    @JsonProperty("effect_chance")
    public int getEffectChance() {
        return effectChance;
    }

    @JsonProperty("effect_chance")
    public void setEffectChance(int effectChance) {
        this.effectChance = effectChance;
    }

    @JsonProperty("generation")
    public Generation getGeneration() {
        return generation;
    }

    @JsonProperty("generation")
    public void setGeneration(Generation generation) {
        this.generation = generation;
    }

    @JsonProperty("stat_changes")
    public List<Object> getStatChanges() {
        return statChanges;
    }

    @JsonProperty("stat_changes")
    public void setStatChanges(List<Object> statChanges) {
        this.statChanges = statChanges;
    }

    @JsonProperty("effect_changes")
    public List<EffectChange> getEffectChanges() {
        return effectChanges;
    }

    @JsonProperty("effect_changes")
    public void setEffectChanges(List<EffectChange> effectChanges) {
        this.effectChanges = effectChanges;
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

    @JsonProperty("machines")
    public List<Machine> getMachines() {
        return machines;
    }

    @JsonProperty("machines")
    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    @JsonProperty("pp")
    public int getPp() {
        return pp;
    }

    @JsonProperty("pp")
    public void setPp(int pp) {
        this.pp = pp;
    }

    @JsonProperty("contest_combos")
    public ContestCombos getContestCombos() {
        return contestCombos;
    }

    @JsonProperty("contest_combos")
    public void setContestCombos(ContestCombos contestCombos) {
        this.contestCombos = contestCombos;
    }

    @JsonProperty("effect_entries")
    public List<EffectEntry_> getEffectEntries() {
        return effectEntries;
    }

    @JsonProperty("effect_entries")
    public void setEffectEntries(List<EffectEntry_> effectEntries) {
        this.effectEntries = effectEntries;
    }

    @JsonProperty("contest_type")
    public ContestType getContestType() {
        return contestType;
    }

    @JsonProperty("contest_type")
    public void setContestType(ContestType contestType) {
        this.contestType = contestType;
    }

    @JsonProperty("priority")
    public int getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @JsonProperty("contest_effect")
    public ContestEffect getContestEffect() {
        return contestEffect;
    }

    @JsonProperty("contest_effect")
    public void setContestEffect(ContestEffect contestEffect) {
        this.contestEffect = contestEffect;
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty("accuracy")
    public int getAccuracy() {
        return accuracy;
    }

    @JsonProperty("accuracy")
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    @JsonProperty("power")
    public int getPower() {
        return power;
    }

    @JsonProperty("power")
    public void setPower(int power) {
        this.power = power;
    }

    @JsonProperty("past_values")
    public List<PastValue> getPastValues() {
        return pastValues;
    }

    @JsonProperty("past_values")
    public void setPastValues(List<PastValue> pastValues) {
        this.pastValues = pastValues;
    }

    @JsonProperty("target")
    public Target getTarget() {
        return target;
    }

    @JsonProperty("target")
    public void setTarget(Target target) {
        this.target = target;
    }

    @JsonProperty("super_contest_effect")
    public SuperContestEffect getSuperContestEffect() {
        return superContestEffect;
    }

    @JsonProperty("super_contest_effect")
    public void setSuperContestEffect(SuperContestEffect superContestEffect) {
        this.superContestEffect = superContestEffect;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("flavor_text_entries")
    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavorTextEntries;
    }

    @JsonProperty("flavor_text_entries")
    public void setFlavorTextEntries(List<FlavorTextEntry> flavorTextEntries) {
        this.flavorTextEntries = flavorTextEntries;
    }

    @JsonProperty("damage_class")
    public DamageClass getDamageClass() {
        return damageClass;
    }

    @JsonProperty("damage_class")
    public void setDamageClass(DamageClass damageClass) {
        this.damageClass = damageClass;
    }

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @JsonProperty("max_pp")
    public int getMaxPp() {
        return maxPp;
    }

    @JsonProperty("max_pp")
    public void setMaxPp(int maxPp) {
        this.maxPp = maxPp;
    }

    @JsonProperty("ldesc")
    public String getLdesc() {
        return ldesc;
    }

    @JsonProperty("ldesc")
    public void setLdesc(String ldesc) {
        this.ldesc = ldesc;
    }

    @JsonProperty("sdesc")
    public String getSdesc() {
        return sdesc;
    }

    @JsonProperty("sdesc")
    public void setSdesc(String sdesc) {
        this.sdesc = sdesc;
    }

    @JsonProperty("z_power")
    public int getZPower() {
        return zPower;
    }

    @JsonProperty("z_power")
    public void setZPower(int zPower) {
        this.zPower = zPower;
    }

    @JsonProperty("z_effect")
    public Object getZEffect() {
        return zEffect;
    }

    @JsonProperty("z_effect")
    public void setZEffect(Object zEffect) {
        this.zEffect = zEffect;
    }
    
    @JsonProperty("z_boost")
    public Object getZBoost() {
        return zBoost;
    }

    @JsonProperty("z_boost")
    public void setZBoost(Object zBoost) {
        this.zBoost = zBoost;
    }

    @JsonProperty("crystal")
    public String getCrystal() {
        return crystal;
    }

    @JsonProperty("crystal")
    public void setCrystal(String crystal) {
        this.crystal = crystal;
    }

    @JsonProperty("flags")
    public List<String> getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    public Optional<String> getFlavorTextEntry(String lang, String version)
    {
    	String secondBest = null;
    	String backup = null;
    	
    	for(FlavorTextEntry entry : flavorTextEntries)
    	{
			if(entry.getLanguage().getName().equals(lang) && entry.getVersionGroup().getName().equals(version))
				return Optional.of(entry.getFlavorText());
			else if(entry.getLanguage().getName().equals(lang))
				secondBest = entry.getFlavorText();
			else if(entry.getLanguage().getName().equals("en"))
				backup = entry.getFlavorText();
    	}
    	
    	if(secondBest != null)
    		return Optional.of(secondBest);
    	
    	return Optional.ofNullable(backup);
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
    
    public Optional<Image> getImage(String lang, int gen)
    {
    	for(Image image : this.images)
    		if(image.getGeneration() == gen && image.getLanguage().equals(lang))
    			return Optional.of(image);
    	
    	return Optional.empty();
    }

}
