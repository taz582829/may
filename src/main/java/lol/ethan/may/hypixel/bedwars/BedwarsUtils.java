package lol.ethan.may.hypixel.bedwars;

import com.google.gson.JsonObject;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.GameStartEvent;
import lol.ethan.may.events.LocationEvent;
import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.api.aurora.AuroraAPI;
import lol.ethan.may.hypixel.api.urchin.UrchinAPI;
import lol.ethan.may.hypixel.modapi.LocationData;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.hypixel.bedwars.enums.BedwarsTeam;
import lol.ethan.may.utils.ChatUtils;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.PingUtils;
import lol.ethan.may.utils.UUIDUtils;
import net.hypixel.data.type.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.util.ChatComponentText;
import lol.ethan.may.events.*;

import java.util.*;

public class BedwarsUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static int ticksSinceGameStart = 0;
    public static long timeOfGameStart = 0;
    public static final Set<String> MODE_KEYS = new HashSet<>();
    public static boolean isInBedwars() {
        return HypixelUtils.isOnHypixel()
                && LocationData.getGameType() == GameType.BEDWARS
                && LocationData.getLobbyName() == null
                && gameStarted;
    }
    public static boolean isInPregame() {
        return HypixelUtils.isOnHypixel()
                && LocationData.getGameType() == GameType.BEDWARS
                && LocationData.getLobbyName() == null
                && !gameStarted;
    }
    private static boolean gameStarted = false;

    public static Map<BedwarsTeam, List<UUID>> getAllPlayerTeams() {
        Map<BedwarsTeam, List<UUID>> teamMap = new HashMap<>();

        for (BedwarsTeam team : BedwarsTeam.values()) {
            List<UUID> teamList = teamMap.computeIfAbsent(team, k -> new ArrayList<>());
            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                if (playerInfo.getGameProfile().getId().version() == 2) continue;
                if (playerInfo.getPlayerTeam() == null) continue;
                UUID uuid = playerInfo.getGameProfile().getId();

                String teamPrefix = playerInfo.getPlayerTeam().getColorPrefix();
                if (team.prefix.equals(teamPrefix)) {
                    teamList.add(uuid);
                }
            }
        }


        return teamMap;
    }
    public enum Mode{
        SOLO("eight_one", "Solos", new String[]{"1s"}),
        DOUBLES("eight_two", "Doubles", new String[]{"2s"}),
        THREES("four_three", "Threes", new String[]{"3s", "Threes"}),
        FOURS("four_four", "Fours", new String[]{"4s", "Fours"}),
        TWO_FOUR("two_four", "4v4"),
        BW_DUEL("bw_two_one_duels", "Bedwars Duels"),
        BW_RUSH_DUEL("bw_two_one_duels_rush", "Bedwars Duels Rush"),
        OVERALL("", "Overall"),
        UNKNOWN(null, null);

        public final String modeKey;
        public final String displayName;
        public final String[] alternatives;

        Mode(String modeKey, String displayName) {
            this.modeKey = modeKey;
            this.displayName = displayName;
            this.alternatives = new String[]{};
        }
        Mode(String modeKey, String displayName, String[] alternatives) {
            this.modeKey = modeKey;
            this.displayName = displayName;
            this.alternatives = alternatives;
        }
        public static Mode parseMode(String gameMode) {
            Mode gamemode = OVERALL;
            for (Mode m : Mode.values()) {
                if (m.modeKey == null) continue;
                String modeKey = m.modeKey;
                boolean matches = modeKey.equalsIgnoreCase(gameMode.toLowerCase())
                        || m.displayName.equalsIgnoreCase(gameMode.toLowerCase());
                if (!matches && m.alternatives != null) {
                    for (String alt : m.alternatives) {
                        if (alt.equalsIgnoreCase(gameMode.toLowerCase())) {
                            matches = true;
                            break;
                        }
                    }
                }

                if (matches) {
                    gamemode = m;
                    break;
                }
            }
            return gamemode;
        }
    }
    static {
        for (Mode m : Mode.values()) {
            if (m == Mode.UNKNOWN) continue;
            if (!m.modeKey.isEmpty()) MODE_KEYS.add(m.modeKey);
            if (!m.displayName.isEmpty()) MODE_KEYS.add(m.displayName);
            if (m.alternatives != null) {
                Collections.addAll(MODE_KEYS, m.alternatives);
            }
        }
    }
    public static Mode currentMode;
    public static void showStats(PlayerResponse.Player player) {
        if (player.stats == null) return;
        String displayName = HypixelAPI.getDisplayName(player);
        double nwl = HypixelAPI.getNetworkLevel(player.networkExp);
        String nwlFormatted = HypixelAPI.formatNetworkLevel(nwl);
        JsonObject bedwarsStats = player.stats.getAsJsonObject("Bedwars");
        int bwsLvl = player.achievements.get("bedwars_level").getAsInt();
        String stars = HypixelAPI.bedwarsLevelFormatted(
                bwsLvl,
                JsonUtils.getString(bedwarsStats, "active_star", "black_open"),
                JsonUtils.getString(bedwarsStats, "active_prestige_scheme", "prestige_scheme_none"),
                JsonUtils.getString(bedwarsStats, "active_prestige_bracket", "")
        );
        int finals = JsonUtils.getInt(bedwarsStats, "final_kills_bedwars", 0);
        int fds = JsonUtils.getInt(bedwarsStats, "final_deaths_bedwars", 0);
        double rawFkdr = finals / (double) (fds == 0 ? 1 : fds);
        String formatted = HypixelAPI.fkdrFormatted(rawFkdr);
        String formattedFinals = HypixelAPI.finalsFormatted(finals);

        String formattedWs = HypixelAPI.isAPIHidden(player.stats) ? "§4API" : ("§e" + JsonUtils.getInt(bedwarsStats, "winstreak", 0));

        String lineBreak = "§8" + ChatUtils.getChatBreak();
        ChatComponentText message = new ChatComponentText(lineBreak);
        message.appendText("\n");
        message.appendText(Entry.SHORT_PREFIX).appendText(stars + " ").appendText(displayName).appendText(" §fLvl: ").appendText(nwlFormatted).appendText("\n");
        message.appendText(Entry.SHORT_PREFIX).appendText("§fF: " + formattedFinals).appendText(" §fFD: §8" + fds + " §fFKDR: " + formatted).appendText("\n");
        message.appendText(Entry.SHORT_PREFIX + "WS: " + formattedWs);

        AuroraAPI.getPing(UUID.fromString(UUIDUtils.dashUUID(player.uuid))).thenAccept(res -> {
            if (res.isSuccess()) {
                String ping;
                if (res.getData() != null && res.getData().size() > 0) {
                    ping = PingUtils.formatPing(res.getData().get(0).getAvg());
                } else {
                    ping = "§4N/A";
                }
                message.appendText(" §fPing: " + ping);
            }


//            message.appendText("\n").appendText(Entry.SHORT_PREFIX).appendText("Mode: ").appendSibling(
//                    ChatUtils.createChatButton(
//                            "§e[" + (mode == null ? "Overall" : mode.displayName) + "]",
//                            "§eClick to view overall stats!",
//                            ClickEvent.Action.RUN_COMMAND,
//                            "/sc bw " + player.ign + " overall"
//                    )
//            );
            message.appendText(lineBreak);
//            if (Config.urchinKey.value != null && !Config.urchinKey.value.isEmpty()) {
//                UrchinAPI.getTag(player.ign).thenAccept(r -> {
//                    UrchinAPI.formatTags()
//                })
//            }
            Entry.chat(message);
        });

    }

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (event.getMessage().getUnformattedText().trim().equals("Protect your bed and destroy the enemy beds.")) {
            ticksSinceGameStart = 0;
            gameStarted = true;
            EventBus.postEvent(new GameStartEvent(GameStartEvent.Mode.BEDWARS));
            ResourceSpawnManager.resetSpawnCounts();
            timeOfGameStart = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S47PacketPlayerListHeaderFooter && isInBedwars()) event.setCancelled(true);
    }
    @SubscribeEvent
    public void onTick(TickEvent.Post event) {
        if (isInBedwars()) {
            ticksSinceGameStart++;
            ResourceSpawnManager.updateTimers();
            if (Config.resourceTimerTab.enabled) {
                ChatComponentText comp = new ChatComponentText("§bDiamonds (§f" + ResourceSpawnManager.diamondSpawnCount + "§b): §f" + ResourceSpawnManager.timeUntilDiamond + " ");
                comp.appendText("§aEmeralds (§f" + ResourceSpawnManager.emeraldSpawnCount + "§a): §f" + ResourceSpawnManager.timeUntilEmerald);
                mc.ingameGUI.getTabList().setHeader(comp);
            }
        }
    }

    @SubscribeEvent
    public void onLocation(LocationEvent event) {
        if (event.getMode() == null) return;
        gameStarted = false;

        String mode = event.getMode().toLowerCase();

        if (mode.contains("duel")) {
            if (mode.contains("rush")) currentMode = Mode.BW_RUSH_DUEL;
            else currentMode = Mode.BW_DUEL;
        }
        if (mode.contains("three")) currentMode = Mode.THREES;
        if (mode.contains("two_four") || mode.contains("four_two")) currentMode = Mode.TWO_FOUR;
        if (mode.contains("eight_one")) currentMode = Mode.SOLO;
        if (mode.contains("four_four")) currentMode = Mode.FOURS;
        if (mode.contains("eight_two")) currentMode = Mode.DOUBLES;
    }
}
