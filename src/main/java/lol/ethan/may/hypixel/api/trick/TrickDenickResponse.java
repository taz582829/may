package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrickDenickResponse {
    @SerializedName(value = "players")
    private List<PlayersItem> players;
    @SerializedName(value = "matches")
    private int matches;

    public List<PlayersItem> getPlayers() {
        return this.players;
    }

    public int getMatches() {
        return this.matches;
    }
}

