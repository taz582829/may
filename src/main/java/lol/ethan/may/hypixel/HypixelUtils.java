package lol.ethan.may.hypixel;

import lol.ethan.may.Entry;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.TimeUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class HypixelUtils {
    public static boolean isOnHypixel() {
        return ModAPI.isOnHypixel();
    }
    public static void showGeneralStats(PlayerResponse.Player player) {
        if (player.stats == null) return;
        String displayName = HypixelAPI.getDisplayName(player);
        double nwl = HypixelAPI.getNetworkLevel(player.networkExp);
        String nwlFormatted = HypixelAPI.formatNetworkLevel(nwl);
        //long karma = player.karma;
        int ap = player.achievementPoints;
        int ranks = JsonUtils.getInt(player.giftingMeta, "ranksGifted");
        LocalDateTime firstLogin = player.getFirstLogin();
        LocalDateTime lastLogin = player.getLastLogin();
        String msg = Entry.PREFIX + displayName + " §fLvl: " + nwlFormatted + " §fAP: §e" + ap + " §fRanks: §1" + ranks;
        msg += "\n" + Entry.SHORT_PREFIX + "§fFL: §3" + TimeUtils.formatTime(firstLogin);
        msg += " §fLL: §3" + (lastLogin == null ? "§4HIDDEN" : TimeUtils.formatTime(lastLogin));
        Entry.chat(msg);
    }
}
