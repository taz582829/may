package lol.ethan.may.hypixel.api.aurora;

import com.google.gson.annotations.SerializedName;

public class PingData {

	@SerializedName("min")
	private int min;

	@SerializedName("avg")
	private int avg;

	@SerializedName("max")
	private int max;

	@SerializedName("day")
	private String day;

	@SerializedName("timestamp")
	private long timestamp;

	public int getMin(){
		return this.min;
	}

	public int getAvg(){
		return this.avg;
	}

	public int getMax(){
		return this.max;
	}

	public String getDay(){
		return this.day;
	}

	public long getTimestamp(){
		return this.timestamp;
	}
}