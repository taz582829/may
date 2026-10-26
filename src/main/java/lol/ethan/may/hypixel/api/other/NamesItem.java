package lol.ethan.may.hypixel.api.other;

import com.google.gson.annotations.SerializedName;

public class NamesItem{

	@SerializedName("changed_at")
	private String changedAt;

	@SerializedName("username")
	private String username;

	public String getChangedAt(){
		return this.changedAt;
	}

	public String getUsername(){
		return this.username;
	}
}