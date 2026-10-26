package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrickDenickResponse{

	@SerializedName("players")
	private List<PlayersItem> players;

	@SerializedName("matches")
	private int matches;

	public List<PlayersItem> getPlayers(){
		return players;
	}

	public int getMatches(){
		return matches;
	}
}