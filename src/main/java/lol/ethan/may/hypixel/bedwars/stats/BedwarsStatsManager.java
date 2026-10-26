package lol.ethan.may.hypixel.bedwars.stats;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import lol.ethan.may.events.player.PlayerPingEvent;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.api.aurora.AuroraAPI;
import lol.ethan.may.hypixel.api.responses.PlayerResponse;
import lol.ethan.may.hypixel.bedwars.BedwarsUtils;
import lol.ethan.may.hypixel.bedwars.enums.BedwarsTeam;
import lol.ethan.may.hypixel.modapi.LocationData;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.TimeUtils;
import net.hypixel.data.type.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.WorldEvent;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BedwarsStatsManager {
    Minecraft mc = Minecraft.getMinecraft();
    boolean inBedwars = false;
    public static Map<BedwarsTeam, List<UUID>> teamMap = new HashMap<>();
    public static final Map<UUID, PlayerResponse.Player> displayNameMap = new HashMap<>();
    private static final Pattern PREGAME_PATTERN = Pattern.compile("^(?:\\[.+]\\s)?(\\w{1,16}): (.*)");
    private static final Pattern LOBBY_CHAT_PATTERN = Pattern.compile("^.\\d+.{2}\\s(?:\\[.+]\\s)?\\w{1,16}: (.*)");
    private static final Map<UUID, Integer> pingMap = new HashMap<>();

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        String msg = event.getMessage().getUnformattedText().trim();
        if (msg.equals("Protect your bed and destroy the enemy beds.") || msg.equals("frost-bw-test") || msg.equals("You will respawn in 10 seconds!")) {
            inBedwars = true;
            Entry.debug("in bedwars");

            TimeUtils.setTickDelay(() -> {
                teamMap = BedwarsUtils.getAllPlayerTeams();

                for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().version() == 2) continue;

                    UUID targetUUID;
                    if (profile.getId().version() == 1) {
                        Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (denick != null) {
                            targetUUID = UUID.fromString(denick.uuid);
                        } else {
                            targetUUID = profile.getId();
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    HypixelAPI.getPlayer(targetUUID).thenAccept(res -> {
                        if (!res.isSuccess()) return;
                        PlayerResponse.Player player = res.player;
                        if (player.stats != null) {
                            displayNameMap.put(profile.getId(), player);
                        }
                    });
                    AuroraAPI.getPing(targetUUID).thenAccept(res -> {
                        if (!res.isSuccess()) return;
                        if (res.getData() == null || res.getData().size() == 0 || res.getData().get(0) == null) return;
                        pingMap.put(profile.getId(), res.getData().get(0).getAvg());
                    });
                }
            }, 10);
        } else if (BedwarsUtils.isInPregame()) {
            Matcher matcher = PREGAME_PATTERN.matcher(msg);
            if (!matcher.find()) return;
            String ign = matcher.group(1);

            HypixelAPI.getPlayer(ign).thenAccept(r -> BedwarsUtils.showStats(r.player));
        } else if (LocationData.getLobbyName() != null && LocationData.getGameType() == GameType.BEDWARS) {
            Matcher matcher = LOBBY_CHAT_PATTERN.matcher(msg);
            if (!matcher.find()) return;
            String ign = matcher.group(1);

            HypixelAPI.getPlayer(ign).thenAccept(r -> BedwarsUtils.showStats(r.player));
        }
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        inBedwars = false;
        teamMap.clear();
        displayNameMap.clear();
    }
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        inBedwars = false;
        teamMap.clear();
        displayNameMap.clear();
    }
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerInTabEvent event) {
        if (inBedwars && Config.bedwarsTabStats.enabled) {
            GameProfile profile = event.getPlayerInfo().getGameProfile();
            if (profile.getId().version() == 2) return;
            if (!displayNameMap.containsKey(profile.getId())) return;
            PlayerResponse.Player player = displayNameMap.get(profile.getId());
            event.setText(getDisplayName(player, event.getPlayerInfo()));
        }
    }

    @SubscribeEvent
    public void onPing(PlayerPingEvent event) {
        GameProfile profile = event.getPlayerInfo().getGameProfile();
        if (profile == null) return;
        if (pingMap.containsKey(profile.getId())) {
            event.setPing(pingMap.get(profile.getId()));
        }
    }

    public static String getDisplayName(PlayerResponse.Player player, NetworkPlayerInfo info) {
        GameProfile profile = info.getGameProfile();
        String originalDisplayName = ScorePlayerTeam.formatPlayerName(info.getPlayerTeam(), profile.getName());
        //if (profile.getId().version() == 1) return new String[]{ originalDisplayName, "§9[?]" };

        if (player == null || player.stats == null) return originalDisplayName + "§9[?]";
        JsonObject bedwars = player.stats.getAsJsonObject("Bedwars");
        if (bedwars == null) return originalDisplayName + "§9[?]";

        List<String> displayStats = new ArrayList<>();
        int finals = JsonUtils.getInt(bedwars, "final_kills_bedwars", 0);
        int fds = JsonUtils.getInt(bedwars, "final_deaths_bedwars", 0);

        String first = originalDisplayName;
        if (Config.bedwarsTabStats_showStars.enabled) {
            int raw = JsonUtils.getInt(player.achievements, "bedwars_level", 0);
            String stars = HypixelAPI.bedwarsLevelFormatted(raw, bedwars);
            first = stars + " §r" + originalDisplayName;
        }
        displayStats.add(first);
        if (Config.bedwarsTabStats_showFKDR.enabled) {
            String fkdr = HypixelAPI.fkdrFormatted((double) finals / (fds == 0 ? 1 : fds));
            displayStats.add(fkdr);
        }
        if (Config.bedwarsTabStats_showFinals.enabled) {
            displayStats.add(HypixelAPI.finalsFormatted(finals));
        }
        if (Config.bedwarsTabStats_showWinstreak.enabled) {
            int ws = JsonUtils.getInt(bedwars, "winstreak", 0);
            if (HypixelAPI.isAPIHidden(player.stats)) {
                if (JsonUtils.getInt(bedwars, "losses_bedwars", 0) == 0) {
                    ws = JsonUtils.getInt(bedwars, "wins_bedwars", 0);
                }
            }

            displayStats.add("§e" + ws);
        }
        String separator = StringEscapeUtils.unescapeJava(
                Config.bedwarsTabStats_seperator.value.replace("&", "§")
        );
        return String.join(" " + separator + " ", displayStats);
    }
}
