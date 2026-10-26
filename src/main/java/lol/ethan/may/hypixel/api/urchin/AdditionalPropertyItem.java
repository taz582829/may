package lol.ethan.may.hypixel.api.urchin;

import com.google.gson.annotations.SerializedName;

public class AdditionalPropertyItem{

	@SerializedName("reason")
	private String reason;

	@SerializedName("expires_at")
	private long expiresAt;

	@SerializedName("added_by")
	private long addedBy;

	@SerializedName("added_by_username")
	private String addedByUsername;

	@SerializedName("hide_username")
	private boolean hideUsername;

	@SerializedName("tag_type")
	private String tagType;

	@SerializedName("added_on")
	private long addedOn;

	public String getReason(){
		return reason;
	}

	public long getExpiresAt(){
		return expiresAt;
	}

	public long getAddedBy(){
		return addedBy;
	}

	public String getAddedByUsername(){
		return addedByUsername;
	}

	public boolean isHideUsername(){
		return hideUsername;
	}

	public String getTagType(){
		return tagType;
	}

	public long getAddedOn(){
		return addedOn;
	}
}