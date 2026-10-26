package lol.ethan.may.hypixel.skywars;

import com.google.gson.JsonObject;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.utils.JsonUtils;

public class SkywarsUtils {
    public static void showStats(final PlayerResponse.Player player) {
        if (null == player.stats) return;
        final double nwl = HypixelAPI.getNetworkLevel(player.networkExp);
        final String nwlFormatted = HypixelAPI.formatNetworkLevel(nwl);
        final JsonObject skywarsStats = player.stats.getAsJsonObject("SkyWars");
        final int kills = JsonUtils.getInt(skywarsStats, "kills");
        final int deaths = JsonUtils.getInt(skywarsStats, "deaths");
        final double rawKdr = (double) kills / (0 == deaths ? 1 : deaths);

    }
}
