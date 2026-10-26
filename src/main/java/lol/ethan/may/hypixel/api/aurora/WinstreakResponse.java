package lol.ethan.may.hypixel.api.aurora;

import com.google.gson.annotations.SerializedName;

public class WinstreakResponse{

	@SerializedName("data")
	private WinstreakData winstreakData;

	@SerializedName("success")
	private boolean success;

	public WinstreakData getData(){
		return this.winstreakData;
	}

	public boolean isSuccess(){
		return this.success;
	}
}