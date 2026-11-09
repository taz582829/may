package lol.ethan.may.rbw.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RankedResponse{

	@SerializedName("recentGames")
	private List<RecentGamesItem> recentGames;

	@SerializedName("success")
	private boolean success;

	@SerializedName("awards")
	private Object awards;

	@SerializedName("daily")
	private Daily daily;

	@SerializedName("lifetime")
	private Lifetime lifetime;

	@SerializedName("season")
	private Season season;

	@SerializedName("weekly")
	private Weekly weekly;

	@SerializedName("player")
	private Player player;

	public List<RecentGamesItem> getRecentGames(){
		return recentGames;
	}

	public boolean isSuccess(){
		return success;
	}

	public Object getAwards(){
		return awards;
	}

	public Daily getDaily(){
		return daily;
	}

	public Lifetime getLifetime(){
		return lifetime;
	}

	public Season getSeason(){
		return season;
	}

	public Weekly getWeekly(){
		return weekly;
	}

	public Player getPlayer(){
		return player;
	}
}