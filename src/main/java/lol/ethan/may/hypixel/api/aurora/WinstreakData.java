package lol.ethan.may.hypixel.api.aurora;

import com.google.gson.annotations.SerializedName;

public class WinstreakData {
    @SerializedName(value = "eight_one_winstreak")
    private int eightOneWinstreak;
    @SerializedName(value = "two_four_winstreak")
    private int twoFourWinstreak;
    @SerializedName(value = "four_four_winstreak")
    private int fourFourWinstreak;
    @SerializedName(value = "accurate_winstreak")
    private Object accurateWinstreak;
    @SerializedName(value = "winstreak")
    private int winstreak;
    @SerializedName(value = "estimated")
    private boolean estimated;
    @SerializedName(value = "eight_two_winstreak")
    private int eightTwoWinstreak;
    @SerializedName(value = "four_three_winstreak")
    private int fourThreeWinstreak;
    @SerializedName(value = "uuid")
    private String uuid;
    @SerializedName(value = "confirmed")
    private long confirmed;

    public int getEightOneWinstreak() {
        return this.eightOneWinstreak;
    }

    public int getTwoFourWinstreak() {
        return this.twoFourWinstreak;
    }

    public int getFourFourWinstreak() {
        return this.fourFourWinstreak;
    }

    public Object getAccurateWinstreak() {
        return this.accurateWinstreak;
    }

    public int getWinstreak() {
        return this.winstreak;
    }

    public boolean isEstimated() {
        return this.estimated;
    }

    public int getEightTwoWinstreak() {
        return this.eightTwoWinstreak;
    }

    public int getFourThreeWinstreak() {
        return this.fourThreeWinstreak;
    }

    public String getUuid() {
        return this.uuid;
    }

    public long getConfirmed() {
        return this.confirmed;
    }
}

