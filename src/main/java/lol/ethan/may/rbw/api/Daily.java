package lol.ethan.may.rbw.api;

import com.google.gson.annotations.SerializedName;

public class Daily{

	@SerializedName("wins")
	private int wins;

	@SerializedName("eloDiff")
	private int eloDiff;

	@SerializedName("games")
	private int games;

	@SerializedName("losses")
	private int losses;

	@SerializedName("mvps")
	private int mvps;

	public int getWins(){
		return wins;
	}

	public int getEloDiff(){
		return eloDiff;
	}

	public int getGames(){
		return games;
	}

	public int getLosses(){
		return losses;
	}

	public int getMvps(){
		return mvps;
	}
}