package lol.ethan.may.rbw.api;

import com.google.gson.annotations.SerializedName;

public class Season{

	@SerializedName("wins")
	private int wins;

	@SerializedName("peakElo")
	private int peakElo;

	@SerializedName("gamesPlayed")
	private int gamesPlayed;

	@SerializedName("elo")
	private int elo;

	@SerializedName("streak")
	private int streak;

	@SerializedName("position")
	private int position;

	@SerializedName("losses")
	private int losses;

	@SerializedName("mvps")
	private int mvps;

	public int getWins(){
		return wins;
	}

	public int getPeakElo(){
		return peakElo;
	}

	public int getGamesPlayed(){
		return gamesPlayed;
	}

	public int getElo(){
		return elo;
	}

	public int getStreak(){
		return streak;
	}

	public int getPosition(){
		return position;
	}

	public int getLosses(){
		return losses;
	}

	public int getMvps(){
		return mvps;
	}
}