package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

public class PlayersItem{

	@SerializedName("score")
	private Object score;

	@SerializedName("finals")
	private int finals;

	@SerializedName("last_seen")
	private LastSeen lastSeen;

	@SerializedName("player_name")
	private String playerName;

	@SerializedName("beds")
	private int beds;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("record_timestamp")
	private long recordTimestamp;

	@SerializedName("cosmetics")
	private Cosmetics cosmetics;

	public Object getScore(){
		return this.score;
	}

	public int getFinals(){
		return this.finals;
	}

	public LastSeen getLastSeen(){
		return this.lastSeen;
	}

	public String getPlayerName(){
		return this.playerName;
	}

	public int getBeds(){
		return this.beds;
	}

	public String getUuid(){
		return this.uuid;
	}

	public long getRecordTimestamp(){
		return this.recordTimestamp;
	}

	public Cosmetics getCosmetics(){
		return this.cosmetics;
	}
}