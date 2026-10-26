package lol.ethan.may.hypixel.api.aurora;

import com.google.gson.annotations.SerializedName;

public class PingData {
    @SerializedName(value = "min")
    private int min;
    @SerializedName(value = "avg")
    private int avg;
    @SerializedName(value = "max")
    private int max;
    @SerializedName(value = "day")
    private String day;
    @SerializedName(value = "timestamp")
    private long timestamp;

    public int getMin() {
        return this.min;
    }

    public int getAvg() {
        return this.avg;
    }

    public int getMax() {
        return this.max;
    }

    public String getDay() {
        return this.day;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}

