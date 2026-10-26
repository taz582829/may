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
		return min;
	}

	public int getAvg(){
		return avg;
	}

	public int getMax(){
		return max;
	}

	public String getDay(){
		return day;
	}

	public long getTimestamp(){
		return timestamp;
	}
}