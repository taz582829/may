package lol.ethan.may.hypixel.api.urchin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UrchinTagsResponse{

	@SerializedName("displayname")
	private String displayname;

	@SerializedName("uuid")
	private String uuid;

	@SerializedName("tags")
	private List<TagsItem> tags;

	public String getDisplayname(){
		return displayname;
	}

	public String getUuid(){
		return uuid;
	}

	public List<TagsItem> getTags(){
		return tags;
	}
}