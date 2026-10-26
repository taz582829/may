package lol.ethan.may.hypixel.api.other;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NameHistoryResponse {
    @SerializedName(value = "names")
    private List<NamesItem> names;
    @SerializedName(value = "success")
    private boolean success;
    @SerializedName(value = "cached")
    private boolean cached;

    public List<NamesItem> getNames() {
        return this.names;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isCached() {
        return this.cached;
    }
}

