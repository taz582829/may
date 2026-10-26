package lol.ethan.may.hypixel;

import lol.ethan.may.Entry;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.TimeUtils;

import java.time.LocalDateTime;

public class HypixelUtils {

    public static boolean isOnHypixel() {
        return ModAPI.isOnHypixel();
    }

    public static void showGeneralStats(PlayerResponse.Player player) {
        if (player.stats == null) {
            return;
        }
        String displayName = HypixelAPI.getDisplayName(player);
        double nwl = HypixelAPI.getNetworkLevel(player.networkExp);
        String nwlFormatted = HypixelAPI.formatNetworkLevel(nwl);
        int ap = player.achievementPoints;
        int ranks = JsonUtils.getInt(player.giftingMeta, "ranksGifted");
        LocalDateTime firstLogin = player.getFirstLogin();
        LocalDateTime lastLogin = player.getLastLogin();
        String msg = "§dMay §8» §r" + displayName + " §fLvl: " + nwlFormatted + " §fAP: §e" + ap + " §fRanks: §1" + ranks;
        msg = msg + "\n§5» §r§fFL: §3" + TimeUtils.formatTime(firstLogin);
        msg = msg + " §fLL: §3" + (lastLogin == null ? "§4HIDDEN" : TimeUtils.formatTime(lastLogin));
        Entry.chat(msg);
    }
}

