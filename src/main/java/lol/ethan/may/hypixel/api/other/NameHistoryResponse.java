package lol.ethan.may.hypixel.api.other;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NameHistoryResponse{

	@SerializedName("names")
	private List<NamesItem> names;

	@SerializedName("success")
	private boolean success;

	@SerializedName("cached")
	private boolean cached;

	public List<NamesItem> getNames(){
		return names;
	}

	public boolean isSuccess(){
		return success;
	}

	public boolean isCached(){
		return cached;
	}
}