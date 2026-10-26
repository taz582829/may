package lol.ethan.may.hypixel.api.other;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NameHistoryResponse{

	@SerializedName("names")
	private List<NamesItem> names;

	@SerializedName("success")
	private boolean success;

	@SerializedName("cached")
	private boolean cached;

	public List<NamesItem> getNames(){
		return this.names;
	}

	public boolean isSuccess(){
		return this.success;
	}

	public boolean isCached(){
		return this.cached;
	}
}