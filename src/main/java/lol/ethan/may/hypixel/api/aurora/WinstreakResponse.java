package lol.ethan.may.hypixel.api.aurora;

import com.google.gson.annotations.SerializedName;

public class WinstreakResponse {
    @SerializedName(value = "data")
    private WinstreakData winstreakData;
    @SerializedName(value = "success")
    private boolean success;

    public WinstreakData getData() {
        return this.winstreakData;
    }

    public boolean isSuccess() {
        return this.success;
    }
}

