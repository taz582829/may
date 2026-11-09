package lol.ethan.may.rbw.api;

import com.google.gson.annotations.SerializedName;

public class OpponentsItem{

	@SerializedName("minecraftName")
	private String minecraftName;

	public String getMinecraftName(){
		return minecraftName;
	}
}