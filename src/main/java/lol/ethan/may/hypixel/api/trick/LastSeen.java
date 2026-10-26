package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

public class LastSeen{

	@SerializedName("activity_type")
	private String activityType;

	@SerializedName("timestamp")
	private long timestamp;

	public String getActivityType(){
		return this.activityType;
	}

	public long getTimestamp(){
		return this.timestamp;
	}
}