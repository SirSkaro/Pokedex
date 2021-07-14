
package skaro.pokeflex.objects.set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "atk",
    "def",
    "hp",
    "spa",
    "spd",
    "spe"
})
public class Ev {

    @JsonProperty("atk")
    private int atk;
    @JsonProperty("def")
    private int def;
    @JsonProperty("hp")
    private int hp;
    @JsonProperty("spa")
    private int spa;
    @JsonProperty("spd")
    private int spd;
    @JsonProperty("spe")
    private int spe;

    @JsonProperty("atk")
    public int getAtk() {
        return atk;
    }

    @JsonProperty("atk")
    public void setAtk(int atk) {
        this.atk = atk;
    }

    @JsonProperty("def")
    public int getDef() {
        return def;
    }

    @JsonProperty("def")
    public void setDef(int def) {
        this.def = def;
    }

    @JsonProperty("hp")
    public int getHp() {
        return hp;
    }

    @JsonProperty("hp")
    public void setHp(int hp) {
        this.hp = hp;
    }

    @JsonProperty("spa")
    public int getSpa() {
        return spa;
    }

    @JsonProperty("spa")
    public void setSpa(int spa) {
        this.spa = spa;
    }

    @JsonProperty("spd")
    public int getSpd() {
        return spd;
    }

    @JsonProperty("spd")
    public void setSpd(int spd) {
        this.spd = spd;
    }

    @JsonProperty("spe")
    public int getSpe() {
        return spe;
    }

    @JsonProperty("spe")
    public void setSpe(int spe) {
        this.spe = spe;
    }

}
