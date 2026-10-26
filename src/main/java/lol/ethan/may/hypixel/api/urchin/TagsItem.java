package lol.ethan.may.hypixel.api.urchin;

import com.google.gson.annotations.SerializedName;

public class TagsItem{

	@SerializedName("reason")
	private String reason;

	@SerializedName("added_by")
	private long addedBy;

	@SerializedName("added_by_username")
	private String addedByUsername;

	@SerializedName("tag_type")
	private String tagType;

	@SerializedName("hide_username")
	private boolean hideUsername;

	@SerializedName("added_on")
	private long addedOn;

	public String getReason(){
		return this.reason;
	}

	public long getAddedBy(){
		return this.addedBy;
	}

	public String getAddedByUsername(){
		return this.addedByUsername;
	}

	public String getTagType(){
		return this.tagType;
	}

	public boolean isHideUsername(){
		return this.hideUsername;
	}

	public long getAddedOn(){
		return this.addedOn;
	}
}