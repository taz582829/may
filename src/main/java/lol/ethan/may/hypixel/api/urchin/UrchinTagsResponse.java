package lol.ethan.may.hypixel.api.urchin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UrchinTagsResponse {
    @SerializedName(value = "displayname")
    private String displayname;
    @SerializedName(value = "uuid")
    private String uuid;
    @SerializedName(value = "tags")
    private List<TagsItem> tags;

    public String getDisplayname() {
        return this.displayname;
    }

    public String getUuid() {
        return this.uuid;
    }

    public List<TagsItem> getTags() {
        return this.tags;
    }
}

