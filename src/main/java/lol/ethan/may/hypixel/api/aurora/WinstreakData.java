package lol.ethan.may.hypixel.api.aurora;

import com.google.gson.annotations.SerializedName;

public class WinstreakData {

	@SerializedName("eight_one_winstreak")
	private int eightOneWinstreak;

	@SerializedName("two_four_winstreak")
	private int twoFourWinstreak;

	@SerializedName("four_four_winstreak")
	private int fourFourWinstreak;

	@SerializedName("accurate_winstreak")
	private Object accurateWinstreak;

	@SerializedName("winstreak")
	private int winstreak;

	@SerializedName("estimated")
	private boolean estimated;

	@SerializedName("eight_two_winstreak")
	private int eightTwoWinstreak;

	@SerializedName("four_three_winstreak")
	private int fourThreeWinstreak;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("confirmed")
	private long confirmed;

	public int getEightOneWinstreak(){
		return eightOneWinstreak;
	}

	public int getTwoFourWinstreak(){
		return twoFourWinstreak;
	}

	public int getFourFourWinstreak(){
		return fourFourWinstreak;
	}

	public Object getAccurateWinstreak(){
		return accurateWinstreak;
	}

	public int getWinstreak(){
		return winstreak;
	}

	public boolean isEstimated(){
		return estimated;
	}

	public int getEightTwoWinstreak(){
		return eightTwoWinstreak;
	}

	public int getFourThreeWinstreak(){
		return fourThreeWinstreak;
	}

	public String getUuid(){
		return uuid;
	}

	public long getConfirmed(){
		return confirmed;
	}
}