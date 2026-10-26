package lol.ethan.may.hypixel.api.aurora;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PingResponse{

	@SerializedName("data")
	private List<PingData> data;

	@SerializedName("success")
	private boolean success;

	public List<PingData> getData(){
		return data;
	}

	public boolean isSuccess(){
		return success;
	}
}