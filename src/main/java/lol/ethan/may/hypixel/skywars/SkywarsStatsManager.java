package lol.ethan.may.hypixel.skywars;/*
package lol.ethan.may.hypixel.skywars;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.events.LocationEvent;
import lol.ethan.may.events.Rend;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.modapi.LocationData;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.stats.IStats;
import lol.ethan.may.mixin.accessors.NetworkPlayerInfoAccessor;
import lol.ethan.may.utils.TimeUtils;
import net.hypixel.data.type.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.WorldEvent;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkywarsStatsManager {
    SkywarsTabStats tabStats = new SkywarsTabStats();
    Minecraft mc = Minecraft.getMinecraft();
    final Pattern joinPattern = Pattern.compile("^(\\w+) has joined ");
    public boolean isInSkywars() {
        return HypixelUtils.isOnHypixel()
                && LocationData.getGameType() == GameType.SKYWARS
                && LocationData.getLobbyName() == null
                && LocationData.getMode() != null;

    }
    @SubscribeEvent
    public void onLocation(LocationEvent event) {
        if (event.isLobby()) return;
        if (event.getGameType() == GameType.SKYWARS && event.getMode() != null) {
            tabStats.setMini(event.getMode().toLowerCase().contains("mini"));
            TimeUtils.setTickDelay(() -> {
                for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().version() == 2) continue;

                    UUID targetUUID;

                    if (profile.getId().version() == 1) {
                        Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (denick == null) {
                            targetUUID = profile.getId();
                            tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    IStats stats = tabStats.getStats(targetUUID);

                    if (stats == null && targetUUID.version() == 4) {
                        HypixelAPI.getPlayer(targetUUID).thenAccept(res ->
                                mc.addScheduledTask(() -> {
                                    try {
                                        tabStats.refreshPlayer(profile.getId(), res.player);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                        );
                    }
                }
            }, 5);
        }
    }

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        String msg = event.getMessage().getUnformattedText().trim();
        if (!isInSkywars()) return;
        Matcher matcher = joinPattern.matcher(msg);
        if (matcher.find()) {
            String ign = matcher.group(1);
            TimeUtils.setTickDelay(() -> {
                for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().version() == 2) continue;
                    if (profile.getName().equals(ign)) {
                        UUID uuid = playerInfo.getGameProfile().getId();
                        UUID targetUUID;

                        if (uuid.version() == 1) {
                            Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                            if (denick == null) {
                                targetUUID = uuid;
                                tabStats.setDisplayName(
                                        playerInfo,
                                        ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), profile.getName()) + " §c[N]"
                                );
                            } else {
                                targetUUID = UUID.fromString(denick.uuid);
                            }
                        } else {
                            targetUUID = profile.getId();
                        }
                        IStats stats = tabStats.getStats(targetUUID);

                        if (stats == null && targetUUID.version() == 4) {
                            HypixelAPI.getPlayer(targetUUID).thenAccept(res ->
                                    mc.addScheduledTask(() -> {
                                        try {
                                            tabStats.refreshPlayer(profile.getId(), res.player);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    })
                            );
                        }
                        break;
                    }
                }
            }, 2);
        }
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        tabStats.clear();
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Load event) {
        tabStats.clear();
    }
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerInTabEvent event) {
        if (isInSkywars()) {
            GameProfile profile = event.playerInfo.getGameProfile();
            if (profile.getId().version() == 2) return;

            IStats stats = tabStats.getStats(profile.getId());

            event.text = tabStats.getDisplayName(event.playerInfo, stats);
            if (stats != null && stats.getPing() != 1) {
                ((NetworkPlayerInfoAccessor) event.playerInfo).setResponseTime(stats.getPing());
            }
        }
    }
}

 */