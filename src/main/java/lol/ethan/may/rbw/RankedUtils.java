//package lol.ethan.may.rbw;
//
//import lol.ethan.may.Entry;
//import lol.ethan.may.rbw.api.RankedResponse;
//import lol.ethan.may.utils.ChatUtils;
//import lol.ethan.may.utils.HttpUtils;
//import lol.ethan.may.utils.JsonUtils;
//import net.minecraft.util.ChatComponentText;
//
//import java.util.UUID;
//
//public class RankedUtils {
//    public static void showRankedStats(final String ign) {
//        final String lineBreak = "§8" + ChatUtils.getChatBreak();
//        final ChatComponentText message = new ChatComponentText(lineBreak);
//        HttpUtils.get("https://compmc.net/api/stats/profile?name=" + ign).thenAccept(r -> {
//            final RankedResponse stats = JsonUtils.gson.fromJson(r, RankedResponse.class);
//            message.appendText("\n");
//            message.appendText(Entry.SHORT_PREFIX).appendText(stars + " ").appendText(displayName).appendText(" §fLvl: ").appendText(nwlFormatted).appendText("\n");
//            message.appendText(Entry.SHORT_PREFIX).appendText("§fF: " + formattedFinals).appendText(" §fFD: §8" + fds + " §fFKDR: " + formatted).appendText("\n");
//            message.appendText(Entry.SHORT_PREFIX + "WS: " + formattedWs);
//        });
//    }
//}
