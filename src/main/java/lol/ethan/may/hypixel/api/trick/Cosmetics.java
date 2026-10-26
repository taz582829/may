package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

public class Cosmetics {
    @SerializedName(value = "figurine")
    private String figurine;
    @SerializedName(value = "glyph")
    private String glyph;
    @SerializedName(value = "killeffect")
    private String kill_effect;
    @SerializedName(value = "killmessages")
    private String kill_message;
    @SerializedName(value = "woodSkin")
    private String wood_skin;
    @SerializedName(value = "victorydance")
    private String victory_dance;
    @SerializedName(value = "islandtopper")
    private String island_topper;
    @SerializedName(value = "npcskin")
    private String npc_skin;
    @SerializedName(value = "beddestroy")
    private String bed_destroy;

    public String getFigurine() {
        return this.figurine;
    }

    public String getGlyph() {
        return this.glyph;
    }

    public String getKillEffect() {
        return this.kill_effect;
    }

    public String getKillMessage() {
        return this.kill_message;
    }

    public String getWoodSkin() {
        return this.wood_skin;
    }

    public String getVictoryDance() {
        return this.victory_dance;
    }

    public String getIslandTopper() {
        return this.island_topper;
    }

    public String getNPCSkin() {
        return this.npc_skin;
    }

    public String getBedDestroy() {
        return this.bed_destroy;
    }
}

