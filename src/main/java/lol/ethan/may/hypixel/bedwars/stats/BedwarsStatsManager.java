package lol.ethan.may.hypixel.bedwars.stats;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.WorldEvent;
import lol.ethan.may.events.player.PlayerPingEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
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
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BedwarsStatsManager {
    Minecraft mc = Minecraft.getMinecraft();
    boolean inBedwars;
    public static Map<BedwarsTeam, List<UUID>> teamMap = new HashMap<>();
    public static final Map<UUID, PlayerResponse.Player> displayNameMap = new HashMap<>();
    private static final Pattern PREGAME_PATTERN = Pattern.compile("^(?:\\[.+]\\s)?(\\w{1,16}): (.*)");
    private static final Pattern LOBBY_CHAT_PATTERN = Pattern.compile("^.\\d+.{2}\\s(?:\\[.+]\\s)?\\w{1,16}: (.*)");
    private static final Map<UUID, Integer> pingMap = new HashMap<>();

    @SubscribeEvent
    public void onChat(final ChatEvent.Received event) {
        final String msg = event.getMessage().getUnformattedText().trim();
        if (msg.equals("Protect your bed and destroy the enemy beds.") || msg.equals("bw-test") || msg.equals("You will respawn in 10 seconds!")) {
            this.inBedwars = true;
            Entry.debug("in bedwars");

            TimeUtils.setTickDelay(() -> {
                teamMap = BedwarsUtils.getAllPlayerTeams();

                for (final NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    final GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().version() == 2) continue;

                    final UUID targetUUID;
                    if (profile.getId().version() == 1) {
                        final Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
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
                        final PlayerResponse.Player player = res.player;
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
            final Matcher matcher = PREGAME_PATTERN.matcher(msg);
            if (!matcher.find()) return;
            final String ign = matcher.group(1);

            HypixelAPI.getPlayer(ign).thenAccept(r -> BedwarsUtils.showStats(r.player));
        } else if (LocationData.getLobbyName() != null && LocationData.getGameType() == GameType.BEDWARS) {
            final Matcher matcher = LOBBY_CHAT_PATTERN.matcher(msg);
            if (!matcher.find()) return;
            final String ign = matcher.group(1);

            HypixelAPI.getPlayer(ign).thenAccept(r -> BedwarsUtils.showStats(r.player));
        }
    }
    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        this.inBedwars = false;
        teamMap.clear();
        displayNameMap.clear();
    }
    @SubscribeEvent
    public void onWorldLoad(final WorldEvent.Load event) {
        this.inBedwars = false;
        teamMap.clear();
        displayNameMap.clear();
    }
    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerInTabEvent event) {
        if (this.inBedwars && Config.bedwarsTabStats.enabled) {
            final GameProfile profile = event.getPlayerInfo().getGameProfile();
            if (profile.getId().version() == 2) return;
            if (!displayNameMap.containsKey(profile.getId())) return;
            final PlayerResponse.Player player = displayNameMap.get(profile.getId());
            event.setText(getDisplayName(player, event.getPlayerInfo()));
        }
    }

    @SubscribeEvent
    public void onPing(final PlayerPingEvent event) {
        final GameProfile profile = event.getPlayerInfo().getGameProfile();
        if (profile == null) return;
        if (pingMap.containsKey(profile.getId())) {
            event.setPing(pingMap.get(profile.getId()));
        }
    }

    public static String getDisplayName(final PlayerResponse.Player player, final NetworkPlayerInfo info) {
        final GameProfile profile = info.getGameProfile();
        final String originalDisplayName = ScorePlayerTeam.formatPlayerName(info.getPlayerTeam(), profile.getName());
        //if (profile.getId().version() == 1) return new String[]{ originalDisplayName, "§9[?]" };

        if (player == null || player.stats == null) return originalDisplayName + "§9[?]";
        final JsonObject bedwars = player.stats.getAsJsonObject("Bedwars");
        if (bedwars == null) return originalDisplayName + "§9[?]";

        final List<String> displayStats = new ArrayList<>();
        final int finals = JsonUtils.getInt(bedwars, "final_kills_bedwars", 0);
        final int fds = JsonUtils.getInt(bedwars, "final_deaths_bedwars", 0);

        String first = originalDisplayName;
        if (Config.bedwarsTabStats_showStars.enabled) {
            final int raw = JsonUtils.getInt(player.achievements, "bedwars_level", 0);
            final String stars = HypixelAPI.bedwarsLevelFormatted(raw, bedwars);
            first = stars + " §r" + originalDisplayName;
        }
        displayStats.add(first);
        if (Config.bedwarsTabStats_showFKDR.enabled) {
            final String fkdr = HypixelAPI.fkdrFormatted((double) finals / (fds == 0 ? 1 : fds));
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
        final String separator = StringEscapeUtils.unescapeJava(
                Config.bedwarsTabStats_seperator.value.replace("&", "§")
        );
        return String.join(" " + separator + " ", displayStats);
    }
}
