package lol.ethan.may.hypixel.api.responses;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class PlayerResponse extends Response {
    public Player player;
    public static class Player {
        @SerializedName("_id")
        public String hypixelId;
        public String uuid;
        @SerializedName("displayname")
        public String ign;
        public long firstLogin;
        public long lastLogin;
        public List<String> achievementsOneTime;
        public JsonObject stats;
        public JsonObject achievements;
        public JsonObject housingMeta;
        public String channel;
        public String mostRecentGameType;
        public JsonObject giftingMeta;
        public String rankPlusColor;
        public String prefix;
        public String rank;
        public String monthlyPackageRank;
        public String newPackageRank;
        public String packageRank;
        public String monthlyRankColor;
        public String mostRecentMonthlyPackageRank;
        public JsonObject quests;
        public long networkExp;
        public long karma;
        public int achievementPoints;

        public String getRankField() {
            if (prefix != null) return "prefix";
            if (rank != null) return "rank";
            if (monthlyPackageRank != null && !this.monthlyPackageRank.equalsIgnoreCase("NONE")) return "monthlyPackageRank";
            if (newPackageRank != null) return "newPackageRank";
            if (packageRank != null) return "packageRank";
            return null;
        }
        public LocalDateTime getFirstLogin() {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.firstLogin), ZoneId.of("America/New_York"));
        }
        public LocalDateTime getLastLogin() {
            if (lastLogin == 0) return null;
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.lastLogin), ZoneId.of("America/New_York"));
        }
        public boolean exists() {
            return hypixelId != null;
        }
        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("hypixelId", this.hypixelId)
                    .append("uuid", this.uuid)
                    .append("firstLogin", this.firstLogin)
                    .append("lastLogin", this.lastLogin)
                    .append("achievementsOneTime", this.achievementsOneTime)
                    .append("stats", this.stats)
                    .append("achievements", this.achievements)
                    .append("housingMeta", this.housingMeta)
                    .append("channel", this.channel)
                    .append("mostRecentGameType", this.mostRecentGameType)
                    .append("giftingMeta", this.giftingMeta)
                    .append("rankPlusColor", this.rankPlusColor)
                    .append("monthlyPackageRank", this.monthlyPackageRank)
                    .append("newPackageRank", this.newPackageRank)
                    .toString();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("player", this.player)
                .toString();
    }
}
