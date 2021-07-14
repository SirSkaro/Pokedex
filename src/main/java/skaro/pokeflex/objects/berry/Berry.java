
package skaro.pokeflex.objects.berry;

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
    "natural_gift_power",
    "flavors",
    "natural_gift_type",
    "name",
    "max_harvest",
    "soil_dryness",
    "smoothness",
    "item",
    "firmness",
    "growth_time",
    "id",
    "size"
})
public class Berry implements IFlexObject {

    @JsonProperty("natural_gift_power")
    private int naturalGiftPower;
    @JsonProperty("flavors")
    private List<Flavor> flavors = null;
    @JsonProperty("natural_gift_type")
    private NaturalGiftType naturalGiftType;
    @JsonProperty("name")
    private String name;
    @JsonProperty("max_harvest")
    private int maxHarvest;
    @JsonProperty("soil_dryness")
    private int soilDryness;
    @JsonProperty("smoothness")
    private int smoothness;
    @JsonProperty("item")
    private Item item;
    @JsonProperty("firmness")
    private Firmness firmness;
    @JsonProperty("growth_time")
    private int growthTime;
    @JsonProperty("id")
    private int id;
    @JsonProperty("size")
    private int size;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("natural_gift_power")
    public int getNaturalGiftPower() {
        return naturalGiftPower;
    }

    @JsonProperty("natural_gift_power")
    public void setNaturalGiftPower(int naturalGiftPower) {
        this.naturalGiftPower = naturalGiftPower;
    }

    @JsonProperty("flavors")
    public List<Flavor> getFlavors() {
        return flavors;
    }

    @JsonProperty("flavors")
    public void setFlavors(List<Flavor> flavors) {
        this.flavors = flavors;
    }

    @JsonProperty("natural_gift_type")
    public NaturalGiftType getNaturalGiftType() {
        return naturalGiftType;
    }

    @JsonProperty("natural_gift_type")
    public void setNaturalGiftType(NaturalGiftType naturalGiftType) {
        this.naturalGiftType = naturalGiftType;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("max_harvest")
    public int getMaxHarvest() {
        return maxHarvest;
    }

    @JsonProperty("max_harvest")
    public void setMaxHarvest(int maxHarvest) {
        this.maxHarvest = maxHarvest;
    }

    @JsonProperty("soil_dryness")
    public int getSoilDryness() {
        return soilDryness;
    }

    @JsonProperty("soil_dryness")
    public void setSoilDryness(int soilDryness) {
        this.soilDryness = soilDryness;
    }

    @JsonProperty("smoothness")
    public int getSmoothness() {
        return smoothness;
    }

    @JsonProperty("smoothness")
    public void setSmoothness(int smoothness) {
        this.smoothness = smoothness;
    }

    @JsonProperty("item")
    public Item getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(Item item) {
        this.item = item;
    }

    @JsonProperty("firmness")
    public Firmness getFirmness() {
        return firmness;
    }

    @JsonProperty("firmness")
    public void setFirmness(Firmness firmness) {
        this.firmness = firmness;
    }

    @JsonProperty("growth_time")
    public int getGrowthTime() {
        return growthTime;
    }

    @JsonProperty("growth_time")
    public void setGrowthTime(int growthTime) {
        this.growthTime = growthTime;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("size")
    public int getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(int size) {
        this.size = size;
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
