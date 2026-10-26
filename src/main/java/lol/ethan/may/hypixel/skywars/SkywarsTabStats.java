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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkywarsTabStats extends TabStats {
    private static boolean isMini;

    @Override
    public IStats createStats() {
        return new SkywarsStats();
    }

    public void setMini(final boolean mini) {
        isMini = mini;
    }

    @Override
    protected String getDisplayName(final NetworkPlayerInfo playerInfo, final IStats stats) {
        if (customDisplayNames.containsKey(playerInfo.getGameProfile().getId())) return customDisplayNames.get(playerInfo.getGameProfile().getId());
        if (null == stats) return ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c?";
        final List<String> statList = stats.getDisplayStats(playerInfo);
        return String.join(" ", statList);
    }

    public static class SkywarsStats implements IStats {
        public String stars;
        public double kdr;
        public String kit;
        public volatile int ping;

        @Override
        public IStats buildStats(final PlayerResponse.Player player, final String mode) {
            final JsonObject genStats = player.stats;
            if (null == genStats) return null;
            final JsonObject swStats = genStats.getAsJsonObject("SkyWars");
            if (null == swStats) return null;

            stars = swStats.get("levelFormattedWithBrackets").getAsString().trim();
            final int kills = JsonUtils.getInt(swStats, "kills", 0);
            final int deaths = JsonUtils.getInt(swStats, "deaths", 0);

            kdr = (double) kills / (0 == deaths ? 1 : deaths);
            final String rawKit = JsonUtils.getString(swStats, "activeKit_" + (isMini ? "MINI" : "SOLO"), "§cNone");
            kit = rawKit.equals("§cNone") ? rawKit : this.formatKit(rawKit);
            ping = 1;
            AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).whenComplete((res, err) -> {
                if (!res.isSuccess()) return;
                final PingData data = res.getData().get(0);
                ping = data.getAvg();
            });
            return this;
        }

        @Override
        public List<String> getDisplayStats(final NetworkPlayerInfo playerInfo) {
            if (1 == playerInfo.getGameProfile().getId().version()) {
                return new ArrayList<>(Arrays.asList(
                        ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()),
                        "§c[NICK]"
                ));
            }
            return new ArrayList<>(Arrays.asList(
                    this.stars,
                    ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()),
                    "§e" + String.format("%.2f", this.kdr),
                    this.kit
            ));
        }

        @Override
        public int getPing() {
            return this.ping;
        }

        public String formatKit(final String rawKit) {
            if (null == rawKit) return "None";

            if (1 == rawKit.split("_").length) return this.capitalizeText(rawKit);
            final Stream<String> kitStream = Arrays.stream(rawKit.split("_"));
            final List<String> kitthing = kitStream
                    .skip(3)
                    .map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1))
                    .collect(Collectors.toList());
            return "§b" + String.join(" ", kitthing);
        }
        public String capitalizeText(final String text) {
            final Stream<String> stream = Arrays.stream(text.split("\\s"))
                    .map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1));

            return String.join(" ", stream.collect(Collectors.toList()));
        }
    }
}
