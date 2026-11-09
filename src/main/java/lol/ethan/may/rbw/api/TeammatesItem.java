package lol.ethan.may.rbw.api;

import com.google.gson.annotations.SerializedName;

public class TeammatesItem{

	@SerializedName("minecraftName")
	private String minecraftName;

	public String getMinecraftName(){
		return minecraftName;
	}
}