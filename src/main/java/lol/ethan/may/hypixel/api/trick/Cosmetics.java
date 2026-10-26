package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

public class Cosmetics{

	@SerializedName("figurine")
	private String figurine;

	@SerializedName("glyph")
	private String glyph;

	@SerializedName("killeffect")
	private String kill_effect;

	@SerializedName("killmessages")
	private String kill_message;

	@SerializedName("woodSkin")
	private String wood_skin;

	@SerializedName("victorydance")
	private String victory_dance;

	@SerializedName("islandtopper")
	private String island_topper;

	@SerializedName("npcskin")
	private String npc_skin;

	@SerializedName("beddestroy")
	private String bed_destroy;

	public String getFigurine(){
		return figurine;
	}

	public String getGlyph(){
		return glyph;
	}

	public String getKillEffect(){
		return kill_effect;
	}

	public String getKillMessage(){
		return kill_message;
	}

	public String getWoodSkin(){
		return wood_skin;
	}

	public String getVictoryDance(){
		return victory_dance;
	}

	public String getIslandTopper(){
		return island_topper;
	}

	public String getNPCSkin(){
		return npc_skin;
	}

	public String getBedDestroy(){
		return bed_destroy;
	}
}