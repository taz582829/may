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

    public static void showGeneralStats(final PlayerResponse.Player player) {
        if (player.stats == null) return;
        final String displayName = HypixelAPI.getDisplayName(player);
        final double nwl = HypixelAPI.getNetworkLevel(player.networkExp);
        final String nwlFormatted = HypixelAPI.formatNetworkLevel(nwl);
        //long karma = player.karma;
        final int ap = player.achievementPoints;
        final int ranks = JsonUtils.getInt(player.giftingMeta, "ranksGifted");
        final LocalDateTime firstLogin = player.getFirstLogin();
        final LocalDateTime lastLogin = player.getLastLogin();
        String msg = Entry.PREFIX + displayName + " §fLvl: " + nwlFormatted + " §fAP: §e" + ap + " §fRanks: §1" + ranks;
        msg += "\n" + Entry.SHORT_PREFIX + "§fFL: §5" + TimeUtils.formatTime(firstLogin);
        msg += " §fLL: §5" + (lastLogin == null ? "§4HIDDEN" : TimeUtils.formatTime(lastLogin));
        Entry.chat(msg);
    }
}
