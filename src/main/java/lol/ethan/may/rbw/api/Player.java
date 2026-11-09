package lol.ethan.may.rbw.api;

import com.google.gson.annotations.SerializedName;

public class Player{

	@SerializedName("serverRank")
	private String serverRank;

	@SerializedName("nickname")
	private Object nickname;

	@SerializedName("winStreak")
	private int winStreak;

	@SerializedName("minecraftName")
	private String minecraftName;

	@SerializedName("userId")
	private String userId;

	@SerializedName("uuid")
	private String uuid;

	public String getServerRank(){
		return serverRank;
	}

	public Object getNickname(){
		return nickname;
	}

	public int getWinStreak(){
		return winStreak;
	}

	public String getMinecraftName(){
		return minecraftName;
	}

	public String getUserId(){
		return userId;
	}

	public String getUuid(){
		return uuid;
	}
}