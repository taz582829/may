package lol.ethan.may.hypixel.duels;

import com.google.gson.JsonObject;
import lol.ethan.may.Entry;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.LocationEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.aurora.AuroraAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.hypixel.modapi.LocationData;
import lol.ethan.may.utils.*;
import net.hypixel.data.type.GameType;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuelsUtils {
    Pattern pattern = Pattern.compile("^Players: (\\d+)/(\\d+)$");

    public enum DuelsMode {
        BOW(new String[]{"tnt"}),
        CLASSIC,
        MEGA_WALLS("Mega Walls", "mw"),
        QUAKE,
        SPLEEF,
        OP("OP"),
        BLITZ,
        COMBO,
        BOXING,
        SKYWARS(new String[]{"sw", "swd"}, "SkyWars", "sw"),
        NODEBUFF("NoDebuff"),
        SUMO,
        BRIDGE,
        UHC("UHC"),
        BEDWARS(new String[]{ "bwd", "bws", "bw" }, "Bedwars", "bedwars_two_one_duels"),
        BEDWARS_RUSH(new String[]{ "rush", "bedfight", "bf" }, "Bedwars Rush", "bedwars_two_one_duels_rush");

        public final String modeKey;
        public final String displayName;
        public String[] alternatives;

        DuelsMode(final String displayName, final String name) {
            this.displayName = displayName;
            modeKey = getModeKeyFromMode(name);
        }
        DuelsMode(final String displayName) {
            this.displayName = displayName;
            modeKey = getModeKeyFromMode(name().toLowerCase());
        }
        DuelsMode() {
            displayName = MayStringUtils.capitalizeText(
                    String.join(" ", name().toLowerCase().split(" "))
            );
            modeKey = getModeKeyFromMode(name().toLowerCase());
        }
        DuelsMode(final String[] alternatives) {
            this.alternatives = alternatives;
            modeKey = getModeKeyFromMode(name().toLowerCase());
            displayName = MayStringUtils.capitalizeText(
                    String.join(" ", name().toLowerCase().split(" "))
            );
        }
        DuelsMode(final String[] alternatives, final String displayName) {
            this.alternatives = alternatives;
            this.displayName = displayName;
            modeKey = getModeKeyFromMode(name().toLowerCase());
        }
        DuelsMode(final String[] alternatives, final String displayName, final String name) {
            this.alternatives = alternatives;
            this.displayName = displayName;
            modeKey = getModeKeyFromMode(name);
        }

        public String getModeKey() {
            return this.modeKey;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String[] getAlternatives() {
            if (null == alternatives || 0 == alternatives.length)
                return new String[]{this.displayName};
            return this.alternatives;
        }

        public static DuelsMode getModeFromString(final String k) {
            for (final DuelsMode mode : DuelsMode.values()) {
                final String key = mode.modeKey;
                final String display = mode.displayName;
                boolean matches = key.equalsIgnoreCase(k.toLowerCase())
                        || display.equalsIgnoreCase(k.toLowerCase());
                if (!matches && null != mode.getAlternatives()) {
                    for (final String alt : mode.getAlternatives()) {
                        if (alt.equalsIgnoreCase(k.toLowerCase())) {
                            matches = true;
                            break;
                        }
                    }
                }


                if (matches) return mode;
            }
            return null;
        }
    }
    private static DuelsMode currentMode;
    public static DuelsMode getCurrentMode() {
        return currentMode;
    }
    private static boolean pregame;
    public static boolean isInDuels() {
        return HypixelUtils.isOnHypixel() &&
                GameType.DUELS == LocationData.getGameType() &&
                null == LocationData.getLobbyName();
    }
    public static boolean isInBwDuels() {
        return HypixelUtils.isOnHypixel() &&
                GameType.BEDWARS == LocationData.getGameType() &&
                null == LocationData.getLobbyName() &&
                null != LocationData.getMode() &&
                LocationData.getMode().toLowerCase().contains("duel");
    }
    public static boolean isInPregame() {
        if (!isInDuels()) return false;
        return pregame;
    }
    public static String getModeKeyFromMode(final String mode) {
        if (!mode.toLowerCase().contains("bedwars")) {
            return mode;
        } else {
            if (mode.toLowerCase().contains("rush")) return "bedwars_two_one_duels_rush";
            return "bedwars_two_one_duels";
        }
    }
    public static String formatWinstreak(final int ws) {
        String result = "";
        if (1000 <= ws) result += "§5";
        else if (500 <= ws) result += "§d";
        else if (200 <= ws) result += "§4";
        else if (150 <= ws) result += "§c";
        else if (100 <= ws) result += "§6";
        else if (80 <= ws) result += "§e";
        else if (50 <= ws) result += "§2";
        else if (30 <= ws) result += "§a";
        else if (10 <= ws) result += "§f";
        else result += "§7";
        return result + String.format("%,d", ws);
    }
    public static String formatBestWinstreak(final int bws) {
        String result = "";
        if (1500 <= bws) result += "§5";
        else if (750 <= bws) result += "§d";
        else if (300 <= bws) result += "§4";
        else if (225 <= bws) result += "§c";
        else if (150 <= bws) result += "§6";
        else if (120 <= bws) result += "§e";
        else if (75 <= bws) result += "§2";
        else if (45 <= bws) result += "§a";
        else if (15 <= bws) result += "§f";
        else result += "§7";
        return result + String.format("%,d", bws);
    }
    public static String formatWins(final int wins) {
        String result = "";
        if (95000 <= wins) result += "§5";
        else if (48000 <= wins) result += "§d";
        else if (24000 <= wins) result += "§4";
        else if (14000 <= wins) result += "§c";
        else if (7000 <= wins) result += "§6";
        else if (4750 <= wins) result += "§e";
        else if (2250 <= wins) result += "§2";
        else if (1000 <= wins) result += "§a";
        else if (450 <= wins) result += "§f";
        else result += "§7";
        return result + String.format("%,d", wins);
    }
    public static String formatWLR(final double wlr) {
        String result = "";
        if (42 <= wlr) result += "§5";
        else if (20 <= wlr) result += "§d";
        else if (12.5 <= wlr) result += "§4";
        else if (8.2 <= wlr) result += "§c";
        else if (4.4 <= wlr) result += "§6";
        else if (3.2 <= wlr) result += "§e";
        else if (1.9 <= wlr) result += "§2";
        else if (1.2 <= wlr) result += "§a";
        else if (0.4 <= wlr) result += "§f";
        else result += "§7";
        return result + String.format("%,.2f", wlr);
    }
    public static String formatKit(final String rawKit) {
        if (null == rawKit) return "None";

        if (1 == rawKit.split("_").length) return MayStringUtils.capitalizeText(rawKit);
        final Stream<String> kitStream = Arrays.stream(rawKit.split("_"));
        final List<String> kitthing = kitStream
                .skip(3)
                .map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1))
                .collect(Collectors.toList());
        return "§b" + String.join(" ", kitthing);
    }
    public static DuelsMode getModeFromString(final String mode) {
        if (mode.toLowerCase().contains("bedwars")) {
            if (!mode.toLowerCase().contains("rush")) return DuelsMode.BEDWARS;
            return DuelsMode.BEDWARS_RUSH;
        }

        final String[] parts = mode.split("_");
        final String key = parts[1];
        return DuelsMode.getModeFromString(key);
    }
    public static void showDuelsStats(final PlayerResponse.Player player, final DuelsMode mode) {
        if (!player.exists()) {
            Entry.chat(Entry.PREFIX + "&cThis player does not exist!");
            return;
        }
        final String displayName = HypixelAPI.getDisplayName(player);
        final String nwlText = HypixelAPI.formatNetworkLevel(HypixelAPI.getNetworkLevel(player.networkExp));

        final JsonObject stats = player.stats;
        if (null == stats) {
            Entry.chat(Entry.PREFIX + "&cThis player has no stats!");
            return;
        }
        final JsonObject duelsStats = stats.getAsJsonObject("Duels");
        if (null == duelsStats) {
            Entry.chat(Entry.PREFIX + "&cThis player has no Duels stats!");
            return;
        }

        final String key = null == mode ? "" : mode.getModeKey();
        final boolean apiHidden = !duelsStats.has("current_winstreak");
        final String wsKey = null == mode
                ? "current_winstreak"
                : "current_winstreak_mode_" + key + (key.contains("bedwars") ? "" : "_duel");

        final String bestWsKey = null == mode
                ? "best_overall_winstreak"
                : "best_winstreak_mode_" + key + (key.contains("bedwars") ? "" : "_duel");

        final int wins = JsonUtils.getInt(duelsStats, null == mode ? "wins" : key + (key.contains("bedwars") ? "_wins" : "_duel_wins"), 0);
        final String formattedWins = formatWins(wins);
        final int losses = JsonUtils.getInt(duelsStats, null == mode ? "losses" : key + (key.contains("bedwars") ? "_losses" : "_duel_losses"), 0);
        final int ws = JsonUtils.getInt(duelsStats, wsKey, 0);

        final String formattedWs = apiHidden ? "§4API" : formatWinstreak(ws);

        final int bws = JsonUtils.getInt(duelsStats, bestWsKey, 0);
        final String formattedBWS = apiHidden ? "§4API" : formatBestWinstreak(bws);

        final double wlr = ((double) wins) / (0 == losses ? 1 : losses);

        final String formattedWlr = formatWLR(wlr);
        final String lineBreak = ChatUtils.getChatBreak("§7§m-");
        final ChatComponentText message = new ChatComponentText(lineBreak);
        message.appendText("\n");
        message.appendText(Entry.SHORT_PREFIX).appendText(displayName).appendText(" §fLvl: ").appendText(nwlText).appendText("\n");
        message.appendText(Entry.SHORT_PREFIX).appendText("§fW: " + formattedWins).appendText(" §fL: §8" + losses + " §fWLR: " + formattedWlr).appendText("\n");
        message.appendText(Entry.SHORT_PREFIX + "WS: " + formattedWs + " §fBWS: " + formattedBWS);

        AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).thenAccept(res -> {
            if (res.isSuccess()) {
                final String ping;
                if (null != res.getData() && 0 < res.getData().size()) {
                    ping = PingUtils.formatPing(res.getData().get(0).getAvg());
                } else {
                    ping = "§4N/A";
                }
                message.appendText(" §fPing: " + ping);
            }

            final boolean addKit = key.equalsIgnoreCase("sw") || key.equalsIgnoreCase("blitz");
            if (addKit) {
                final String formattedKit = formatKit(key.equalsIgnoreCase("sw")
                        ? JsonUtils.getString(duelsStats, "sw_duels_kit_new3", "kit_mini_solo_armorer")
                        : JsonUtils.getString(duelsStats, "blitz_duels_kit", "knight"));

                message.appendText("§f Kit: ").appendText(formattedKit);
            }
            message.appendText("\n").appendText(Entry.SHORT_PREFIX).appendText("Mode: ").appendSibling(
                    ChatUtils.createChatButton(
                            "§e[" + (null == mode ? "Overall" : mode.displayName) + "]",
                            "§eClick to view overall stats!",
                            ClickEvent.Action.RUN_COMMAND,
                            "/sc duels " + player.ign + " overall"
                    )
            );
            message.appendText(lineBreak);
            Entry.chat(message);
        });


    }
    @SubscribeEvent
    public void onLocation(final LocationEvent event) {
        if (!event.isLobby() && GameType.DUELS == event.getGameType() && null != event.getMode()) {
            currentMode = getModeFromString(event.getMode());
            pregame = true;
        }
    }
    @SubscribeEvent
    public void onChat(final ChatEvent.Received event) {
        if (null == LocationData.getMode()) return;
        if (event.getMessage().getUnformattedText().trim().equals("Eliminate your opponents!") && !LocationData.getMode().toLowerCase().contains("bedwars")) {
            pregame = false;
        }
    }
}
