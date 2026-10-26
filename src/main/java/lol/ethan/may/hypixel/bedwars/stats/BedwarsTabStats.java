
package lol.ethan.may.hypixel.bedwars.stats;

import com.google.gson.JsonObject;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.aurora.AuroraAPI;
import lol.ethan.may.hypixel.api.aurora.PingData;
import lol.ethan.may.hypixel.api.aurora.WinstreakData;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.hypixel.stats.IStats;
import lol.ethan.may.hypixel.stats.TabStats;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.UUIDUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BedwarsTabStats extends TabStats {
    @Override
    protected String getDisplayName(NetworkPlayerInfo playerInfo, IStats stats) {
        if (customDisplayNames.containsKey(playerInfo.getGameProfile().getId())) return customDisplayNames.get(playerInfo.getGameProfile().getId());
        if (stats == null) return ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c?";
        List<String> statList = stats.getDisplayStats(playerInfo);
        return String.join(" ", statList);
    }

    @Override
    public IStats createStats() {
        return new BedwarsStats();
    }

    public static class BedwarsStats implements IStats {
        public UUID uuid;
        public String starsFormatted;
        public int finals;
        public double fkdr;
        public String fkdrFormatted;
        public volatile int winstreak;
        public boolean winstreakEstimated;
        public boolean zeroFinalDeaths;
        public String otherTags;
        public volatile int ping;

        @Override
        public IStats buildStats(PlayerResponse.Player player, String mode) {
            int bwsLvl = JsonUtils.getInt(player.achievements, "bedwars_level", 0);
            JsonObject genStats = player.stats;
            if (genStats == null) return null;
            JsonObject bedwarsStats = genStats.getAsJsonObject("Bedwars");
            if (bedwarsStats == null) return null;

            String starsFormatted = HypixelAPI.bedwarsLevelFormatted(
                    bwsLvl,
                    JsonUtils.getString(bedwarsStats, "active_star", "black_open"),
                    JsonUtils.getString(bedwarsStats, "active_prestige_scheme", "prestige_scheme_none"),
                    JsonUtils.getString(bedwarsStats, "active_prestige_bracket", "")
            );
            int finals = JsonUtils.getInt(bedwarsStats, "final_kills_bedwars", 0);
            int fds = JsonUtils.getInt(bedwarsStats, "final_deaths_bedwars", 0);
            double fkdrRaw = (double) finals / (fds == 0 ? 1 : fds);
            boolean zeroFds = fds == 0;
            String fkdr = HypixelAPI.fkdrFormatted(fkdrRaw);
            String otherTags = "";
            if (zeroFds) otherTags += "§5Z";

            this.uuid = UUID.fromString(UUIDUtils.dashUUID(player.uuid));
            this.starsFormatted = starsFormatted;
            this.finals = finals;
            this.fkdr = fkdrRaw;
            this.fkdrFormatted = fkdr;
            this.winstreak = HypixelAPI.isAPIHidden(player.stats) ? 0 : bedwarsStats.get("winstreak").getAsInt();
            this.winstreakEstimated = HypixelAPI.isAPIHidden(player.stats);
            this.zeroFinalDeaths = zeroFds;
            this.otherTags = otherTags;
            this.ping = 1;
            AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).whenComplete((res, err) -> {
                if (!res.isSuccess()) return;
                PingData data = res.getData().get(0);
                this.ping = data.getAvg();
            });
            if (winstreakEstimated) {
                AuroraAPI.getWinstreak(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).whenComplete((res, err) -> {
                    if (!res.isSuccess()) return;
                    WinstreakData data = res.getData();
                    this.winstreak = data.getWinstreak();
                });
            }


            return this;
        }

        @Override
        public List<String> getDisplayStats(NetworkPlayerInfo playerInfo) {

            List<String> stats = new ArrayList<>();
            if (playerInfo.getGameProfile().getId().version() == 1) {
                stats.add(ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()));
                stats.add("§c[NICK]");
            }
            stats.add(starsFormatted);
            stats.add(ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()));
            stats.add("§7|");
            stats.add(fkdrFormatted);
            stats.add("§7|");
            stats.add(EnumChatFormatting.YELLOW.toString() + (winstreakEstimated ? EnumChatFormatting.ITALIC : "") + winstreak);
            return stats;
        }

        public int getPing() {
            return ping;
        }
    }
}
