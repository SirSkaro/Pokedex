package skaro.pokeflex.objects.card;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"id",
	"name",
	"nationalPokedexNumber",
	"imageUrl",
	"imageUrlHiRes",
	"types",
	"supertype",
	"subtype",
	"evolvesFrom",
	"hp",
	"retreatCost",
	"convertedRetreatCost",
	"number",
	"artist",
	"rarity",
	"series",
	"set",
	"setCode",
	"text",
	"attacks",
	"weaknesses",
	"resistances",
	"ability"
})
public class Card_ {

	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("nationalPokedexNumber")
	private Integer nationalPokedexNumber;
	@JsonProperty("imageUrl")
	private String imageUrl;
	@JsonProperty("imageUrlHiRes")
	private String imageUrlHiRes;
	@JsonProperty("types")
	private List<String> types = null;
	@JsonProperty("supertype")
	private String supertype;
	@JsonProperty("subtype")
	private String subtype;
	@JsonProperty("evolvesFrom")
	private String evolvesFrom;
	@JsonProperty("hp")
	private String hp;
	@JsonProperty("retreatCost")
	private List<String> retreatCost = null;
	@JsonProperty("convertedRetreatCost")
	private Integer convertedRetreatCost;
	@JsonProperty("number")
	private String number;
	@JsonProperty("artist")
	private String artist;
	@JsonProperty("rarity")
	private String rarity;
	@JsonProperty("series")
	private String series;
	@JsonProperty("set")
	private String set;
	@JsonProperty("setCode")
	private String setCode;
	@JsonProperty("text")
	private List<String> text = null;
	@JsonProperty("attacks")
	private List<Attack> attacks = null;
	@JsonProperty("weaknesses")
	private List<Weakness> weaknesses = null;
	@JsonProperty("resistances")
	private List<Resistance> resistances = null;
	@JsonProperty("ability")
	private Ability ability;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
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

	@JsonProperty("nationalPokedexNumber")
	public Integer getNationalPokedexNumber() {
		return nationalPokedexNumber;
	}

	@JsonProperty("nationalPokedexNumber")
	public void setNationalPokedexNumber(Integer nationalPokedexNumber) {
		this.nationalPokedexNumber = nationalPokedexNumber;
	}

	@JsonProperty("imageUrl")
	public String getImageUrl() {
		return imageUrl;
	}

	@JsonProperty("imageUrl")
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@JsonProperty("imageUrlHiRes")
	public String getImageUrlHiRes() {
		return imageUrlHiRes;
	}

	@JsonProperty("imageUrlHiRes")
	public void setImageUrlHiRes(String imageUrlHiRes) {
		this.imageUrlHiRes = imageUrlHiRes;
	}

	@JsonProperty("types")
	public List<String> getTypes() {
		return types;
	}

	@JsonProperty("types")
	public void setTypes(List<String> types) {
		this.types = types;
	}

	@JsonProperty("supertype")
	public String getSupertype() {
		return supertype;
	}

	@JsonProperty("supertype")
	public void setSupertype(String supertype) {
		this.supertype = supertype;
	}

	@JsonProperty("subtype")
	public String getSubtype() {
		return subtype;
	}

	@JsonProperty("subtype")
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	@JsonProperty("evolvesFrom")
	public String getEvolvesFrom() {
		return evolvesFrom;
	}

	@JsonProperty("evolvesFrom")
	public void setEvolvesFrom(String evolvesFrom) {
		this.evolvesFrom = evolvesFrom;
	}

	@JsonProperty("hp")
	public String getHp() {
		return hp;
	}

	@JsonProperty("hp")
	public void setHp(String hp) {
		this.hp = hp;
	}

	@JsonProperty("retreatCost")
	public List<String> getRetreatCost() {
		return retreatCost;
	}

	@JsonProperty("retreatCost")
	public void setRetreatCost(List<String> retreatCost) {
		this.retreatCost = retreatCost;
	}

	@JsonProperty("convertedRetreatCost")
	public Integer getConvertedRetreatCost() {
		return convertedRetreatCost;
	}

	@JsonProperty("convertedRetreatCost")
	public void setConvertedRetreatCost(Integer convertedRetreatCost) {
		this.convertedRetreatCost = convertedRetreatCost;
	}

	@JsonProperty("number")
	public String getNumber() {
		return number;
	}

	@JsonProperty("number")
	public void setNumber(String number) {
		this.number = number;
	}

	@JsonProperty("artist")
	public String getArtist() {
		return artist;
	}

	@JsonProperty("artist")
	public void setArtist(String artist) {
		this.artist = artist;
	}

	@JsonProperty("rarity")
	public String getRarity() {
		return rarity;
	}

	@JsonProperty("rarity")
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	@JsonProperty("series")
	public String getSeries() {
		return series;
	}

	@JsonProperty("series")
	public void setSeries(String series) {
		this.series = series;
	}

	@JsonProperty("set")
	public String getSet() {
		return set;
	}

	@JsonProperty("set")
	public void setSet(String set) {
		this.set = set;
	}

	@JsonProperty("setCode")
	public String getSetCode() {
		return setCode;
	}

	@JsonProperty("setCode")
	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}

	@JsonProperty("text")
	public List<String> getText() {
		return text;
	}

	@JsonProperty("text")
	public void setText(List<String> text) {
		this.text = text;
	}

	@JsonProperty("attacks")
	public List<Attack> getAttacks() {
		return attacks;
	}

	@JsonProperty("attacks")
	public void setAttacks(List<Attack> attacks) {
		this.attacks = attacks;
	}

	@JsonProperty("weaknesses")
	public List<Weakness> getWeaknesses() {
		return weaknesses;
	}

	@JsonProperty("weaknesses")
	public void setWeaknesses(List<Weakness> weaknesses) {
		this.weaknesses = weaknesses;
	}

	@JsonProperty("resistances")
	public List<Resistance> getResistances() {
		return resistances;
	}

	@JsonProperty("resistances")
	public void setResistances(List<Resistance> resistances) {
		this.resistances = resistances;
	}

	@JsonProperty("ability")
	public Ability getAbility() {
		return ability;
	}

	@JsonProperty("ability")
	public void setAbility(Ability ability) {
		this.ability = ability;
	}

}
