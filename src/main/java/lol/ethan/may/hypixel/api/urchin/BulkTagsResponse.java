package lol.ethan.may.hypixel.api.urchin;

import com.google.gson.annotations.SerializedName;

public class BulkTagsResponse {
    @SerializedName(value = "players")
    private Players players;

    public Players getPlayers() {
        return this.players;
    }
}

