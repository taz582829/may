package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

public class LastSeen {
    @SerializedName(value = "activity_type")
    private String activityType;
    @SerializedName(value = "timestamp")
    private long timestamp;

    public String getActivityType() {
        return this.activityType;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}

