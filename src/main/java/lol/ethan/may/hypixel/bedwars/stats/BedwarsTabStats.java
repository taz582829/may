
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
    protected String getDisplayName(final NetworkPlayerInfo playerInfo, final IStats stats) {
        if (customDisplayNames.containsKey(playerInfo.getGameProfile().getId())) return customDisplayNames.get(playerInfo.getGameProfile().getId());
        if (null == stats) return ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c?";
        final List<String> statList = stats.getDisplayStats(playerInfo);
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
        public IStats buildStats(final PlayerResponse.Player player, final String mode) {
            final int bwsLvl = JsonUtils.getInt(player.achievements, "bedwars_level", 0);
            final JsonObject genStats = player.stats;
            if (null == genStats) return null;
            final JsonObject bedwarsStats = genStats.getAsJsonObject("Bedwars");
            if (null == bedwarsStats) return null;

            final String starsFormatted = HypixelAPI.bedwarsLevelFormatted(
                    bwsLvl,
                    JsonUtils.getString(bedwarsStats, "active_star", "black_open"),
                    JsonUtils.getString(bedwarsStats, "active_prestige_scheme", "prestige_scheme_none"),
                    JsonUtils.getString(bedwarsStats, "active_prestige_bracket", "")
            );
            final int finals = JsonUtils.getInt(bedwarsStats, "final_kills_bedwars", 0);
            final int fds = JsonUtils.getInt(bedwarsStats, "final_deaths_bedwars", 0);
            final double fkdrRaw = (double) finals / (0 == fds ? 1 : fds);
            final boolean zeroFds = 0 == fds;
            final String fkdr = HypixelAPI.fkdrFormatted(fkdrRaw);
            String otherTags = "";
            if (zeroFds) otherTags += "§5Z";

            uuid = UUID.fromString(UUIDUtils.dashUUID(player.uuid));
            this.starsFormatted = starsFormatted;
            this.finals = finals;
            this.fkdr = fkdrRaw;
            fkdrFormatted = fkdr;
            winstreak = HypixelAPI.isAPIHidden(player.stats) ? 0 : bedwarsStats.get("winstreak").getAsInt();
            winstreakEstimated = HypixelAPI.isAPIHidden(player.stats);
            zeroFinalDeaths = zeroFds;
            this.otherTags = otherTags;
            ping = 1;
            AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).whenComplete((res, err) -> {
                if (!res.isSuccess()) return;
                final PingData data = res.getData().get(0);
                ping = data.getAvg();
            });
            if (this.winstreakEstimated) {
                AuroraAPI.getWinstreak(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).whenComplete((res, err) -> {
                    if (!res.isSuccess()) return;
                    final WinstreakData data = res.getData();
                    winstreak = data.getWinstreak();
                });
            }


            return this;
        }

        @Override
        public List<String> getDisplayStats(final NetworkPlayerInfo playerInfo) {

            final List<String> stats = new ArrayList<>();
            if (1 == playerInfo.getGameProfile().getId().version()) {
                stats.add(ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()));
                stats.add("§c[NICK]");
            }
            stats.add(this.starsFormatted);
            stats.add(ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()));
            stats.add("§7|");
            stats.add(this.fkdrFormatted);
            stats.add("§7|");
            stats.add(EnumChatFormatting.YELLOW.toString() + (this.winstreakEstimated ? EnumChatFormatting.ITALIC : "") + this.winstreak);
            return stats;
        }

        public int getPing() {
            return this.ping;
        }
    }
}
