package lol.ethan.may.hypixel.skywars;

import com.google.gson.JsonObject;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkywarsTabStats extends TabStats {
    private static boolean isMini;

    @Override
    public IStats createStats() {
        return new SkywarsStats();
    }

    public void setMini(boolean mini) {
        isMini = mini;
    }

    @Override
    protected String getDisplayName(NetworkPlayerInfo playerInfo, IStats stats) {
        if (customDisplayNames.containsKey(playerInfo.getGameProfile().getId())) return customDisplayNames.get(playerInfo.getGameProfile().getId());
        if (stats == null) return ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c?";
        List<String> statList = stats.getDisplayStats(playerInfo);
        return String.join(" ", statList);
    }

    public static class SkywarsStats implements IStats {
        public String stars;
        public double kdr;
        public String kit;
        public volatile int ping;

        @Override
        public IStats buildStats(PlayerResponse.Player player, String mode) {
            JsonObject genStats = player.stats;
            if (genStats == null) return null;
            JsonObject swStats = genStats.getAsJsonObject("SkyWars");
            if (swStats == null) return null;

            this.stars = swStats.get("levelFormattedWithBrackets").getAsString().trim();
            int kills = JsonUtils.getInt(swStats, "kills", 0);
            int deaths = JsonUtils.getInt(swStats, "deaths", 0);

            this.kdr = (double) kills / (deaths == 0 ? 1 : deaths);
            String rawKit = JsonUtils.getString(swStats, "activeKit_" + (isMini ? "MINI" : "SOLO"), "§cNone");
            this.kit = rawKit.equals("§cNone") ? rawKit : formatKit(rawKit);
            this.ping = 1;
            AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).whenComplete((res, err) -> {
                if (!res.isSuccess()) return;
                PingData data = res.getData().get(0);
                this.ping = data.getAvg();
            });
            return this;
        }

        @Override
        public List<String> getDisplayStats(NetworkPlayerInfo playerInfo) {
            if (playerInfo.getGameProfile().getId().version() == 1) {
                return new ArrayList<>(Arrays.asList(
                        ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()),
                        "§c[NICK]"
                ));
            }
            return new ArrayList<>(Arrays.asList(
                    stars,
                    ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()),
                    "§e" + String.format("%.2f", kdr),
                    kit
            ));
        }

        @Override
        public int getPing() {
            return ping;
        }

        public String formatKit(String rawKit) {
            if (rawKit == null) return "None";

            if (rawKit.split("_").length == 1) return capitalizeText(rawKit);
            Stream<String> kitStream = Arrays.stream(rawKit.split("_"));
            List<String> kitthing = kitStream
                    .skip(3)
                    .map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1))
                    .collect(Collectors.toList());
            return "§b" + String.join(" ", kitthing);
        }
        public String capitalizeText(String text) {
            Stream<String> stream = Arrays.stream(text.split("\\s"))
                    .map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1));

            return String.join(" ", stream.collect(Collectors.toList()));
        }
    }
}
