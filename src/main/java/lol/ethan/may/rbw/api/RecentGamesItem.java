package lol.ethan.may.rbw.api;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RecentGamesItem{

	@SerializedName("gameId")
	private String gameId;

	@SerializedName("eloDiff")
	private int eloDiff;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("teammates")
	private List<TeammatesItem> teammates;

	@SerializedName("mapPlaying")
	private String mapPlaying;

	@SerializedName("mvp")
	private boolean mvp;

	@SerializedName("won")
	private boolean won;

	@SerializedName("opponents")
	private List<OpponentsItem> opponents;

	public String getGameId(){
		return gameId;
	}

	public int getEloDiff(){
		return eloDiff;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public List<TeammatesItem> getTeammates(){
		return teammates;
	}

	public String getMapPlaying(){
		return mapPlaying;
	}

	public boolean isMvp(){
		return mvp;
	}

	public boolean isWon(){
		return won;
	}

	public List<OpponentsItem> getOpponents(){
		return opponents;
	}
}