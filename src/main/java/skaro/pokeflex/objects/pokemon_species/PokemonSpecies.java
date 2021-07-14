
package skaro.pokeflex.objects.pokemon_species;

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
@JsonPropertyOrder({ "capture_rate", "habitat", "color", "forms_switchable", "shape", "names", "id", "egg_groups",
		"base_happiness", "generation", "flavor_text_entries", "growth_rate", "hatch_counter", "genera",
		"evolves_from_species", "form_descriptions", "varieties", "name", "evolution_chain", "has_gender_differences",
		"is_baby", "gender_rate", "pal_park_encounters", "order", "pokedex_numbers" })
public class PokemonSpecies implements IFlexObject {

	@JsonProperty("capture_rate")
	private int captureRate;
	@JsonProperty("habitat")
	private Habitat habitat;
	@JsonProperty("color")
	private Color color;
	@JsonProperty("forms_switchable")
	private boolean formsSwitchable;
	@JsonProperty("shape")
	private Shape shape;
	@JsonProperty("names")
	private List<Name> names = null;
	@JsonProperty("id")
	private int id;
	@JsonProperty("egg_groups")
	private List<EggGroup> eggGroups = null;
	@JsonProperty("base_happiness")
	private int baseHappiness;
	@JsonProperty("generation")
	private Generation generation;
	@JsonProperty("flavor_text_entries")
	private List<FlavorTextEntry> flavorTextEntries = null;
	@JsonProperty("growth_rate")
	private GrowthRate growthRate;
	@JsonProperty("hatch_counter")
	private int hatchCounter;
	@JsonProperty("genera")
	private List<Genera> genera = null;
	@JsonProperty("evolves_from_species")
	private Object evolvesFromSpecies;
	@JsonProperty("form_descriptions")
	private List<FormDescription> formDescriptions = null;
	@JsonProperty("varieties")
	private List<Variety> varieties = null;
	@JsonProperty("name")
	private String name;
	@JsonProperty("evolution_chain")
	private EvolutionChain evolutionChain;
	@JsonProperty("has_gender_differences")
	private boolean hasGenderDifferences;
	@JsonProperty("is_baby")
	private boolean isBaby;
	@JsonProperty("gender_rate")
	private int genderRate;
	@JsonProperty("pal_park_encounters")
	private List<PalParkEncounter> palParkEncounters = null;
	@JsonProperty("order")
	private int order;
	@JsonProperty("pokedex_numbers")
	private List<PokedexNumber> pokedexNumbers = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("capture_rate")
	public int getCaptureRate() {
		return captureRate;
	}

	@JsonProperty("capture_rate")
	public void setCaptureRate(int captureRate) {
		this.captureRate = captureRate;
	}

	@JsonProperty("habitat")
	public Habitat getHabitat() {
		return habitat;
	}

	@JsonProperty("habitat")
	public void setHabitat(Habitat habitat) {
		this.habitat = habitat;
	}

	@JsonProperty("color")
	public Color getColor() {
		return color;
	}

	@JsonProperty("color")
	public void setColor(Color color) {
		this.color = color;
	}

	@JsonProperty("forms_switchable")
	public boolean isFormsSwitchable() {
		return formsSwitchable;
	}

	@JsonProperty("forms_switchable")
	public void setFormsSwitchable(boolean formsSwitchable) {
		this.formsSwitchable = formsSwitchable;
	}

	@JsonProperty("shape")
	public Shape getShape() {
		return shape;
	}

	@JsonProperty("shape")
	public void setShape(Shape shape) {
		this.shape = shape;
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

	@JsonProperty("egg_groups")
	public List<EggGroup> getEggGroups() {
		return eggGroups;
	}

	@JsonProperty("egg_groups")
	public void setEggGroups(List<EggGroup> eggGroups) {
		this.eggGroups = eggGroups;
	}

	@JsonProperty("base_happiness")
	public int getBaseHappiness() {
		return baseHappiness;
	}

	@JsonProperty("base_happiness")
	public void setBaseHappiness(int baseHappiness) {
		this.baseHappiness = baseHappiness;
	}

	@JsonProperty("generation")
	public Generation getGeneration() {
		return generation;
	}

	@JsonProperty("generation")
	public void setGeneration(Generation generation) {
		this.generation = generation;
	}

	@JsonProperty("flavor_text_entries")
	public List<FlavorTextEntry> getFlavorTextEntries() {
		return flavorTextEntries;
	}

	@JsonProperty("flavor_text_entries")
	public void setFlavorTextEntries(List<FlavorTextEntry> flavorTextEntries) {
		this.flavorTextEntries = flavorTextEntries;
	}

	@JsonProperty("growth_rate")
	public GrowthRate getGrowthRate() {
		return growthRate;
	}

	@JsonProperty("growth_rate")
	public void setGrowthRate(GrowthRate growthRate) {
		this.growthRate = growthRate;
	}

	@JsonProperty("hatch_counter")
	public int getHatchCounter() {
		return hatchCounter;
	}

	@JsonProperty("hatch_counter")
	public void setHatchCounter(int hatchCounter) {
		this.hatchCounter = hatchCounter;
	}

	@JsonProperty("genera")
	public List<Genera> getGenera() {
		return genera;
	}

	@JsonProperty("genera")
	public void setGenera(List<Genera> genera) {
		this.genera = genera;
	}

	@JsonProperty("evolves_from_species")
	public Object getEvolvesFromSpecies() {
		return evolvesFromSpecies;
	}

	@JsonProperty("evolves_from_species")
	public void setEvolvesFromSpecies(Object evolvesFromSpecies) {
		this.evolvesFromSpecies = evolvesFromSpecies;
	}

	@JsonProperty("form_descriptions")
	public List<FormDescription> getFormDescriptions() {
		return formDescriptions;
	}

	@JsonProperty("form_descriptions")
	public void setFormDescriptions(List<FormDescription> formDescriptions) {
		this.formDescriptions = formDescriptions;
	}

	@JsonProperty("varieties")
	public List<Variety> getVarieties() {
		return varieties;
	}

	@JsonProperty("varieties")
	public void setVarieties(List<Variety> varieties) {
		this.varieties = varieties;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("evolution_chain")
	public EvolutionChain getEvolutionChain() {
		return evolutionChain;
	}

	@JsonProperty("evolution_chain")
	public void setEvolutionChain(EvolutionChain evolutionChain) {
		this.evolutionChain = evolutionChain;
	}

	@JsonProperty("has_gender_differences")
	public boolean isHasGenderDifferences() {
		return hasGenderDifferences;
	}

	@JsonProperty("has_gender_differences")
	public void setHasGenderDifferences(boolean hasGenderDifferences) {
		this.hasGenderDifferences = hasGenderDifferences;
	}

	@JsonProperty("is_baby")
	public boolean isIsBaby() {
		return isBaby;
	}

	@JsonProperty("is_baby")
	public void setIsBaby(boolean isBaby) {
		this.isBaby = isBaby;
	}

	@JsonProperty("gender_rate")
	public int getGenderRate() {
		return genderRate;
	}

	@JsonProperty("gender_rate")
	public void setGenderRate(int genderRate) {
		this.genderRate = genderRate;
	}

	@JsonProperty("pal_park_encounters")
	public List<PalParkEncounter> getPalParkEncounters() {
		return palParkEncounters;
	}

	@JsonProperty("pal_park_encounters")
	public void setPalParkEncounters(List<PalParkEncounter> palParkEncounters) {
		this.palParkEncounters = palParkEncounters;
	}

	@JsonProperty("order")
	public int getOrder() {
		return order;
	}

	@JsonProperty("order")
	public void setOrder(int order) {
		this.order = order;
	}

	@JsonProperty("pokedex_numbers")
	public List<PokedexNumber> getPokedexNumbers() {
		return pokedexNumbers;
	}

	@JsonProperty("pokedex_numbers")
	public void setPokedexNumbers(List<PokedexNumber> pokedexNumbers) {
		this.pokedexNumbers = pokedexNumbers;
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
	
	public String getGeneraInLanguage(String lang)
	{
		for(Genera genera : this.getGenera())
		{
			if(genera.getLanguage().getName().equals(lang))
				return genera.getGenus();
		}
		
		for(Genera genera : this.getGenera())		//default to English
		{
			if(genera.getLanguage().getName().equals("en"))
				return genera.getGenus();
		}
		
		return null;
	}
	
    public Optional<String> getFlavorTextEntry(String lang, String version)
    {
    	for(FlavorTextEntry entry : flavorTextEntries)
			if(entry.getLanguage().getName().equals(lang) && entry.getVersion().getName().equals(version))
				return Optional.of(entry.getFlavorText());
    	
    	return Optional.empty();
    }

}
