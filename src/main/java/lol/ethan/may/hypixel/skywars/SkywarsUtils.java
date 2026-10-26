package lol.ethan.may.hypixel.skywars;

import com.google.gson.JsonObject;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.utils.JsonUtils;

public class SkywarsUtils {
    public static void showStats(PlayerResponse.Player player) {
        if (player.stats == null) return;
        double nwl = HypixelAPI.getNetworkLevel(player.networkExp);
        String nwlFormatted = HypixelAPI.formatNetworkLevel(nwl);
        JsonObject skywarsStats = player.stats.getAsJsonObject("SkyWars");
        int kills = JsonUtils.getInt(skywarsStats, "kills");
        int deaths = JsonUtils.getInt(skywarsStats, "deaths");
        double rawKdr = (double) kills / (deaths == 0 ? 1 : deaths);

    }
}
