package lol.ethan.may.hypixel.tnttag;

import com.google.gson.JsonObject;
import lol.ethan.may.Entry;
import lol.ethan.may.hypixel.api.aurora.AuroraAPI;
import lol.ethan.may.hypixel.api.aurora.PingData;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.hypixel.stats.IStats;
import lol.ethan.may.hypixel.stats.TabStats;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.UUIDUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.*;

import static net.minecraft.util.EnumChatFormatting.*;

public class TNTTagTabStats extends TabStats {
    public static final Map<String, String> winMap = new HashMap<>();

    @Override
    public IStats createStats() {
        return new TagStats();
    }

    @Override
    protected String getDisplayName(NetworkPlayerInfo playerInfo, IStats stats) {
        if (customDisplayNames.containsKey(playerInfo.getGameProfile().getId())) return customDisplayNames.get(playerInfo.getGameProfile().getId());
        if (stats == null) return ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c?";
        List<String> statList = stats.getDisplayStats(playerInfo);
        return String.join(" ", statList);
    }

    public static class TagStats implements IStats {
        public volatile String wins;
        public volatile int ping;

        @Override
        public IStats buildStats(PlayerResponse.Player player, String mode) {
            Entry.debug("started building stats for " + player.ign);
            JsonObject genStats = player.stats;
            if (genStats == null) return null;
            JsonObject tntStats = genStats.getAsJsonObject("TNTGames");
            if (tntStats == null) return null;

            int tagWins = JsonUtils.getInt(tntStats, "wins_tntag", 0);
            this.wins = formatWins(tagWins);
            winMap.put(player.ign.toLowerCase().trim(), formatWins(tagWins));
            this.ping = 1;
            AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).whenComplete((res, err) -> {
                if (!res.isSuccess()) return;
                PingData data = res.getData().get(0);
                this.ping = data.getAvg();
            });
            Entry.debug("finished building stats for " + player.ign);
            return this;
        }

        @Override
        public int getPing() {
            return ping;
        }

        public String getWins() {
            return wins;
        }

        @Override
        public List<String> getDisplayStats(NetworkPlayerInfo playerInfo) {

            List<String> stats = new ArrayList<>();
            if (playerInfo.getGameProfile().getId().version() == 1) {
                stats.add(ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()));
                stats.add("§c[NICK]");
            }

            stats.add(ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()));
            stats.add(wins);

            return stats;
        }



    }
    public static String formatWins(int wins) {
        String w = "[" + wins + "]";
        StringBuilder result = new StringBuilder();
        if (wins < 15) result.append(DARK_GRAY);
        else if (wins < 50) result.append(GRAY);
        else if (wins < 100) result.append(WHITE);
        else if (wins < 250) result.append(DARK_GREEN);
        else if (wins < 500) result.append(GREEN);
        else if (wins < 1000) result.append(BLUE);
        else if (wins < 1500) result.append(DARK_PURPLE);
        else if (wins < 2500) result.append(GOLD);
        else if (wins < 5000) result.append(RED);
        else if (wins < 10000) result.append(BLACK);
        else {
            final String[] RAINBOW_COLORS = {
                    "c",
                    "6",
                    "e",
                    "a",
                    "b",
                    "d",
                    "5"
            };
            for (int i = 0; i < w.length(); i++) {
                String part = String.valueOf(w.charAt(i));
                String color = RAINBOW_COLORS[i % 7];
                result.append("§").append(color).append(part);
            }
        }
        result.append(w);

        return result.toString();
    }
}
