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
		return score;
	}

	public int getFinals(){
		return finals;
	}

	public LastSeen getLastSeen(){
		return lastSeen;
	}

	public String getPlayerName(){
		return playerName;
	}

	public int getBeds(){
		return beds;
	}

	public String getUuid(){
		return uuid;
	}

	public long getRecordTimestamp(){
		return recordTimestamp;
	}

	public Cosmetics getCosmetics(){
		return cosmetics;
	}
}