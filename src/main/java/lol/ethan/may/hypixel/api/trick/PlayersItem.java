package lol.ethan.may.hypixel.api.trick;

import com.google.gson.annotations.SerializedName;

public class PlayersItem {
    @SerializedName(value = "score")
    private Object score;
    @SerializedName(value = "finals")
    private int finals;
    @SerializedName(value = "last_seen")
    private LastSeen lastSeen;
    @SerializedName(value = "player_name")
    private String playerName;
    @SerializedName(value = "beds")
    private int beds;
    @SerializedName(value = "uuid")
    private String uuid;
    @SerializedName(value = "record_timestamp")
    private long recordTimestamp;
    @SerializedName(value = "cosmetics")
    private Cosmetics cosmetics;

    public Object getScore() {
        return this.score;
    }

    public int getFinals() {
        return this.finals;
    }

    public LastSeen getLastSeen() {
        return this.lastSeen;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public int getBeds() {
        return this.beds;
    }

    public String getUuid() {
        return this.uuid;
    }

    public long getRecordTimestamp() {
        return this.recordTimestamp;
    }

    public Cosmetics getCosmetics() {
        return this.cosmetics;
    }
}

