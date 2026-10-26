package lol.ethan.may.hypixel.duels;

import com.google.gson.JsonObject;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.hypixel.stats.IStats;
import lol.ethan.may.hypixel.stats.TabStats;
import lol.ethan.may.utils.JsonUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.Arrays;
import java.util.List;

public class DuelsTabStats extends TabStats {
    @Override
    public IStats createStats() {
        return new DuelsStats();
    }

    @Override
    protected String getDisplayName(final NetworkPlayerInfo playerInfo, final IStats stats) {
        if (customDisplayNames.containsKey(playerInfo.getGameProfile().getId())) return customDisplayNames.get(playerInfo.getGameProfile().getId());
        if (null == stats) return ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c?";
        final List<String> statList = stats.getDisplayStats(playerInfo);
        return String.join(" ", statList);
    }

    public static class DuelsStats implements IStats {
        public String stars;
        public String winsFormatted;
        public String wlrFormatted;

        @Override
        public IStats buildStats(final PlayerResponse.Player player, final String mode) {
            final JsonObject duelsStats = JsonUtils.getObject(player.stats, "Duels", null);
            if (null == duelsStats) return null;
            int wins = JsonUtils.getInt(duelsStats, "wins", 0);
            int losses = JsonUtils.getInt(duelsStats, "losses", 0);
            double wlr = (double) wins / (0 == losses ? 1 : losses);
            if (null != mode && mode.toLowerCase().contains("bedwars")) {
                wins = JsonUtils.getInt(duelsStats, "wins_bedwars", 0);
                losses = JsonUtils.getInt(duelsStats, "losses_bedwars", 0);
                wlr = (double) wins / (0 == losses ? 1 : losses);
                final int rawStars = JsonUtils.getInt(player.achievements, "bedwars_level", 0);
                final JsonObject bedwarsStats = JsonUtils.getObject(player.stats, "Bedwars", null);

                this.stars = HypixelAPI.bedwarsLevelFormatted(
                        rawStars,
                        JsonUtils.getString(bedwarsStats, "active_star", "black_open"),
                        JsonUtils.getString(bedwarsStats, "active_prestige_scheme", "prestige_scheme_none"),
                        JsonUtils.getString(bedwarsStats, "active_prestige_bracket", "")
                );
            }
            this.winsFormatted = DuelsUtils.formatWins(wins);
            this.wlrFormatted = DuelsUtils.formatWLR(wlr);
            return this;
        }

        @Override
        public List<String> getDisplayStats(final NetworkPlayerInfo playerInfo) {
            final String displayName = ScorePlayerTeam.formatPlayerName(
                    playerInfo.getPlayerTeam(),
                    playerInfo.getGameProfile().getName()
            );
            return Arrays.asList(
                    displayName,
                    "§8» " + this.winsFormatted
            );
        }

        @Override
        public int getPing() {
            return 0;
        }
    }
}
