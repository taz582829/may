package lol.ethan.may.hypixel.api.aurora;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PingResponse {
    @SerializedName(value = "data")
    private List<PingData> data;
    @SerializedName(value = "success")
    private boolean success;

    public List<PingData> getData() {
        return this.data;
    }

    public boolean isSuccess() {
        return this.success;
    }
}

