package lol.ethan.may.hypixel.api.other;

import com.google.gson.annotations.SerializedName;

public class NamesItem {
    @SerializedName(value = "changed_at")
    private String changedAt;
    @SerializedName(value = "username")
    private String username;

    public String getChangedAt() {
        return this.changedAt;
    }

    public String getUsername() {
        return this.username;
    }
}

