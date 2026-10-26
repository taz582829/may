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
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuelsUtils {
    private static DuelsMode currentMode;
    private static boolean pregame;

    static {
        pregame = false;
    }

    Pattern pattern = Pattern.compile("^Players: (\\d+)/(\\d+)$");

    public static DuelsMode getCurrentMode() {
        return currentMode;
    }

    public static boolean isInDuels() {
        return HypixelUtils.isOnHypixel() && LocationData.getGameType() == GameType.DUELS && LocationData.getLobbyName() == null;
    }

    public static boolean isInBwDuels() {
        return HypixelUtils.isOnHypixel() && LocationData.getGameType() == GameType.BEDWARS && LocationData.getLobbyName() == null && LocationData.getMode() != null && LocationData.getMode().toLowerCase().contains("duel");
    }

    public static boolean isInPregame() {
        if (!DuelsUtils.isInDuels()) {
            return false;
        }
        return pregame;
    }

    public static String getModeKeyFromMode(String mode) {
        if (!mode.toLowerCase().contains("bedwars")) {
            return mode;
        }
        if (mode.toLowerCase().contains("rush")) {
            return "bedwars_two_one_duels_rush";
        }
        return "bedwars_two_one_duels";
    }

    public static String formatWinstreak(int ws) {
        String result = "";
        result = ws >= 1000 ? result + "§5" : (ws >= 500 ? result + "§d" : (ws >= 200 ? result + "§4" : (ws >= 150 ? result + "§c" : (ws >= 100 ? result + "§6" : (ws >= 80 ? result + "§e" : (ws >= 50 ? result + "§2" : (ws >= 30 ? result + "§a" : (ws >= 10 ? result + "§f" : result + "§7"))))))));
        return result + String.format("%,d", ws);
    }

    public static String formatBestWinstreak(int bws) {
        String result = "";
        result = bws >= 1500 ? result + "§5" : (bws >= 750 ? result + "§d" : (bws >= 300 ? result + "§4" : (bws >= 225 ? result + "§c" : (bws >= 150 ? result + "§6" : (bws >= 120 ? result + "§e" : (bws >= 75 ? result + "§2" : (bws >= 45 ? result + "§a" : (bws >= 15 ? result + "§f" : result + "§7"))))))));
        return result + String.format("%,d", bws);
    }

    public static String formatWins(int wins) {
        String result = "";
        result = wins >= 95000 ? result + "§5" : (wins >= 48000 ? result + "§d" : (wins >= 24000 ? result + "§4" : (wins >= 14000 ? result + "§c" : (wins >= 7000 ? result + "§6" : (wins >= 4750 ? result + "§e" : (wins >= 2250 ? result + "§2" : (wins >= 1000 ? result + "§a" : (wins >= 450 ? result + "§f" : result + "§7"))))))));
        return result + String.format("%,d", wins);
    }

    public static String formatWLR(double wlr) {
        String result = "";
        result = wlr >= 42.0 ? result + "§5" : (wlr >= 20.0 ? result + "§d" : (wlr >= 12.5 ? result + "§4" : (wlr >= 8.2 ? result + "§c" : (wlr >= 4.4 ? result + "§6" : (wlr >= 3.2 ? result + "§e" : (wlr >= 1.9 ? result + "§2" : (wlr >= 1.2 ? result + "§a" : (wlr >= 0.4 ? result + "§f" : result + "§7"))))))));
        return result + String.format("%,.2f", wlr);
    }

    public static String formatKit(String rawKit) {
        if (rawKit == null) {
            return "None";
        }
        if (rawKit.split("_").length == 1) {
            return MayStringUtils.capitalizeText(rawKit);
        }
        Stream<String> kitStream = Arrays.stream(rawKit.split("_"));
        List kitthing = kitStream.skip(3L).map(s -> String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1)).collect(Collectors.toList());
        return "§b" + String.join(" ", kitthing);
    }

    public static DuelsMode getModeFromString(String mode) {
        if (mode.toLowerCase().contains("bedwars")) {
            if (!mode.toLowerCase().contains("rush")) {
                return DuelsMode.BEDWARS;
            }
            return DuelsMode.BEDWARS_RUSH;
        }
        String[] parts = mode.split("_");
        String key = parts[1];
        return DuelsMode.getModeFromString(key);
    }

    public static void showDuelsStats(PlayerResponse.Player player, DuelsMode mode) {
        if (!player.exists()) {
            Entry.chat("§dMay §8» §r&cThis player does not exist!");
            return;
        }
        String displayName = HypixelAPI.getDisplayName(player);
        String nwlText = HypixelAPI.formatNetworkLevel(HypixelAPI.getNetworkLevel(player.networkExp));
        JsonObject stats = player.stats;
        if (stats == null) {
            Entry.chat("§dMay §8» §r&cThis player has no stats!");
            return;
        }
        JsonObject duelsStats = stats.getAsJsonObject("Duels");
        if (duelsStats == null) {
            Entry.chat("§dMay §8» §r&cThis player has no Duels stats!");
            return;
        }

        String key = mode == null ? "" : mode.getModeKey();
        boolean apiHidden = !duelsStats.has("current_winstreak");
        String wsKey = mode == null
                ? "current_winstreak"
                : "current_winstreak_mode_" + key + (key.contains("bedwars") ? "" : "_duel");

        String bestWsKey = mode == null
                ? "best_overall_winstreak"
                : "best_winstreak_mode_" + key + (key.contains("bedwars") ? "" : "_duel");

        int wins = JsonUtils.getInt(duelsStats, mode == null ? "wins" : key + (key.contains("bedwars") ? "_wins" : "_duel_wins"), 0);
        String formattedWins = DuelsUtils.formatWins(wins);
        int losses = JsonUtils.getInt(duelsStats, mode == null ? "losses" : key + (key.contains("bedwars") ? "_losses" : "_duel_losses"), 0);
        int ws = JsonUtils.getInt(duelsStats, wsKey, 0);
        String formattedWs = apiHidden ? "§4API" : DuelsUtils.formatWinstreak(ws);
        int bws = JsonUtils.getInt(duelsStats, bestWsKey, 0);
        String formattedBWS = apiHidden ? "§4API" : DuelsUtils.formatBestWinstreak(bws);
        double wlr = (double) wins / (double) (losses == 0 ? 1 : losses);
        String formattedWlr = DuelsUtils.formatWLR(wlr);
        String lineBreak = ChatUtils.getChatBreak("§7§m-");
        ChatComponentText message = new ChatComponentText(lineBreak);
        message.appendText("\n");
        message.appendText("§5» §r").appendText(displayName).appendText(" §fLvl: ").appendText(nwlText).appendText("\n");
        message.appendText("§5» §r").appendText("§fW: " + formattedWins).appendText(" §fL: §8" + losses + " §fWLR: " + formattedWlr).appendText("\n");
        message.appendText("§5» §rWS: " + formattedWs + " §fBWS: " + formattedBWS);
        AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).thenAccept(res -> {
            boolean addKit;
            if (res.isSuccess()) {
                String ping = res.getData() != null && res.getData().size() > 0 ? PingUtils.formatPing(res.getData().get(0).getAvg()) : "§4N/A";
                message.appendText(" §fPing: " + ping);
            }
            boolean bl = addKit = key.equalsIgnoreCase("sw") || key.equalsIgnoreCase("blitz");
            if (addKit) {
                String formattedKit = DuelsUtils.formatKit(key.equalsIgnoreCase("sw") ? JsonUtils.getString(duelsStats, "sw_duels_kit_new3", "kit_mini_solo_armorer") : JsonUtils.getString(duelsStats, "blitz_duels_kit", "knight"));
                message.appendText("§f Kit: ").appendText(formattedKit);
            }
            message.appendText("\n").appendText("§5» §r").appendText("Mode: ").appendSibling(ChatUtils.createChatButton("§e[" + (mode == null ? "Overall" : mode.displayName) + "]", "§eClick to view overall stats!", ClickEvent.Action.RUN_COMMAND, "/sc duels " + player.ign + " overall"));
            message.appendText(lineBreak);
            Entry.chat(message);
        });
    }

    @SubscribeEvent
    public void onLocation(LocationEvent event) {
        if (!event.isLobby() && event.getGameType() == GameType.DUELS && event.getMode() != null) {
            currentMode = DuelsUtils.getModeFromString(event.getMode());
            pregame = true;
        }
    }

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (LocationData.getMode() == null) {
            return;
        }
        if (event.getMessage().getUnformattedText().trim().equals("Eliminate your opponents!") && !LocationData.getMode().toLowerCase().contains("bedwars")) {
            pregame = false;
        }
    }

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
        BEDWARS(new String[]{"bwd", "bws", "bw"}, "Bedwars", "bedwars_two_one_duels"),
        BEDWARS_RUSH(new String[]{"rush", "bedfight", "bf"}, "Bedwars Rush", "bedwars_two_one_duels_rush");

        public final String modeKey;
        public final String displayName;
        public String[] alternatives;

        DuelsMode(String displayName, String name) {
            this.displayName = displayName;
            this.modeKey = DuelsUtils.getModeKeyFromMode(name);
        }

        DuelsMode(String displayName) {
            this.displayName = displayName;
            this.modeKey = DuelsUtils.getModeKeyFromMode(this.name().toLowerCase());
        }

        DuelsMode() {
            this.displayName = MayStringUtils.capitalizeText(String.join(" ", this.name().toLowerCase().split(" ")));
            this.modeKey = DuelsUtils.getModeKeyFromMode(this.name().toLowerCase());
        }

        DuelsMode(String[] alternatives) {
            this.alternatives = alternatives;
            this.modeKey = DuelsUtils.getModeKeyFromMode(this.name().toLowerCase());
            this.displayName = MayStringUtils.capitalizeText(String.join(" ", this.name().toLowerCase().split(" ")));
        }

        DuelsMode(String[] alternatives, String displayName) {
            this.alternatives = alternatives;
            this.displayName = displayName;
            this.modeKey = DuelsUtils.getModeKeyFromMode(this.name().toLowerCase());
        }

        DuelsMode(String[] alternatives, String displayName, String name) {
            this.alternatives = alternatives;
            this.displayName = displayName;
            this.modeKey = DuelsUtils.getModeKeyFromMode(name);
        }

        public static DuelsMode getModeFromString(String k) {
            for (DuelsMode mode : DuelsMode.values()) {
                boolean matches;
                String key = mode.modeKey;
                String display = mode.displayName;
                boolean bl = matches = key.equalsIgnoreCase(k.toLowerCase()) || display.equalsIgnoreCase(k.toLowerCase());
                if (!matches && mode.getAlternatives() != null) {
                    for (String alt : mode.getAlternatives()) {
                        if (!alt.equalsIgnoreCase(k.toLowerCase())) continue;
                        matches = true;
                        break;
                    }
                }
                if (!matches) continue;
                return mode;
            }
            return null;
        }

        public String getModeKey() {
            return this.modeKey;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String[] getAlternatives() {
            if (this.alternatives == null || this.alternatives.length == 0) {
                return new String[]{this.displayName};
            }
            return this.alternatives;
        }
    }
}

