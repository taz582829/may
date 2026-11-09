package lol.ethan.may.rbw;

import lol.ethan.may.Entry;
import lol.ethan.may.rbw.api.*;
import lol.ethan.may.utils.ChatUtils;
import lol.ethan.may.utils.HttpUtils;
import lol.ethan.may.utils.JsonUtils;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class RankedUtils {
    public static void showRankedStats(final String ign) {
        final String lineBreak = "§8" + ChatUtils.getChatBreak();
        final ChatComponentText message = new ChatComponentText(lineBreak);
        HttpUtils.get("https://compmc.net/api/stats/profile?name=" + ign).thenAccept(r -> {
            final RankedResponse stats = JsonUtils.gson.fromJson(r, RankedResponse.class);

            final Player player = stats.getPlayer();
            final Season season = stats.getSeason();
            final String displayName = player.getServerRank().trim() + " " + player.getMinecraftName();
            final String elo = "§8[" + RBWOverlay.getEloColor(season.getElo()) + season.getElo() + "§8] ";
            final String peakElo = RBWOverlay.getEloColor(season.getPeakElo()).toString();
            final int ws = player.getWinStreak();
            final int wins = stats.getSeason().getWins();
            final double losses = stats.getSeason().getLosses();
            final double wlr = wins / (losses == 0 ? 1 : losses);

            message.appendText("\n");
            message.appendText(Entry.SHORT_PREFIX).appendText(elo).appendText(displayName).appendText(" §fP: " + peakElo).appendText("\n");
            message.appendText(Entry.SHORT_PREFIX).appendText("§fW: " + formatWins(wins)).appendText(" §fL: §8" + losses + " §fWLR: " + RBWOverlay.getFormattedWLR(wlr)).appendText("\n");
            message.appendText(Entry.SHORT_PREFIX + "WS: " + formatWs(ws));

            final ChatComponentText recentGames = new ChatComponentText("§fRecent Games §e[Hover]");
            final ChatComponentText rgHover = new ChatComponentText("");
            for (final RecentGamesItem game : stats.getRecentGames()) {
                final boolean won = game.isWon();
                final String eloDiff = (game.getEloDiff() > 0 ? "§a+" : "§c-") + game.getEloDiff();
                rgHover.appendText(won ? "§a§lW" : "§c§lL" + " " + eloDiff + " " + (game.isMvp() ? "§6MVP" : ""));
                rgHover.appendText("\n");
            }

            recentGames.setChatStyle(new ChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, rgHover)));

            message.appendText("\n").appendSibling(recentGames);
            message.appendText("\n" + lineBreak);
            Entry.chat(message);
        });
    }
    public static String formatWins(final int wins) {
        final EnumChatFormatting color;
        if (wins < 10) color = EnumChatFormatting.DARK_GRAY;
        else if (wins < 25) color = EnumChatFormatting.GRAY;
        else if (wins < 35) color = EnumChatFormatting.WHITE;
        else if (wins < 50) color = EnumChatFormatting.GREEN;
        else if (wins < 70) color = EnumChatFormatting.DARK_GREEN;
        else if (wins < 100) color = EnumChatFormatting.YELLOW;
        else if (wins < 120) color = EnumChatFormatting.GOLD;
        else color = EnumChatFormatting.RED;

        return color.toString() + wins + EnumChatFormatting.RESET;
    }
    public static String formatWs(final int ws) {
        final EnumChatFormatting color;
        if (ws == 0) color = EnumChatFormatting.DARK_GRAY;
        else if (ws <= 3) color = EnumChatFormatting.GRAY;
        else if (ws <= 5) color = EnumChatFormatting.WHITE;
        else if (ws <= 7) color = EnumChatFormatting.GREEN;
        else if (ws <= 10) color = EnumChatFormatting.GOLD;
        else if (ws <= 15) color = EnumChatFormatting.RED;
        else color = EnumChatFormatting.DARK_RED;

        return color.toString() + ws + EnumChatFormatting.RESET;
    }
    public static String formatKills(final int kills) {
        final EnumChatFormatting color;
        if (kills <= 250) color = EnumChatFormatting.DARK_GRAY;
        else if (kills <= 500) color = EnumChatFormatting.GRAY;
        else if (kills <= 1000) color = EnumChatFormatting.WHITE;
        else if (kills <= 2000) color = EnumChatFormatting.GREEN;
        else if (kills <= 3000) color = EnumChatFormatting.YELLOW;
        else if (kills <= 5000) color = EnumChatFormatting.GOLD;
        else if (kills <= 10000) color = EnumChatFormatting.RED;
        else color = EnumChatFormatting.DARK_RED;

        return color.toString() + kills + EnumChatFormatting.RESET;
    }
}
