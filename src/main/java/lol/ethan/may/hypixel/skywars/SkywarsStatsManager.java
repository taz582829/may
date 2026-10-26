package lol.ethan.may.hypixel.skywars;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.LocationEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.WorldEvent;
import lol.ethan.may.events.player.NameFormatInTabEvent;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.modapi.LocationData;
import lol.ethan.may.hypixel.stats.IStats;
import lol.ethan.may.utils.TimeUtils;
import net.hypixel.data.type.GameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkywarsStatsManager {
    final Pattern joinPattern = Pattern.compile("^(\\w+) has joined ");
    SkywarsTabStats tabStats = new SkywarsTabStats();
    Minecraft mc = Minecraft.getMinecraft();

    public boolean isInSkywars() {
        return HypixelUtils.isOnHypixel() && LocationData.getGameType() == GameType.SKYWARS && LocationData.getLobbyName() == null && LocationData.getMode() != null;
    }

    @SubscribeEvent
    public void onLocation(LocationEvent event) {
        if (event.isLobby()) {
            return;
        }
        if (event.getGameType() == GameType.SKYWARS && event.getMode() != null) {
            this.tabStats.setMini(event.getMode().toLowerCase().contains("mini"));
            TimeUtils.setTickDelay(() -> {
                for (NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    IStats stats;
                    UUID targetUUID;
                    GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().version() == 2) continue;
                    if (profile.getId().version() == 1) {
                        Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (denick == null) {
                            targetUUID = profile.getId();
                            this.tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    if ((stats = this.tabStats.getStats(targetUUID)) != null || targetUUID.version() != 4) continue;
                    HypixelAPI.getPlayer(targetUUID).thenAccept(res -> this.mc.addScheduledTask(() -> {
                        try {
                            this.tabStats.refreshPlayer(profile.getId(), res.player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                }
            }, 5);
        }
    }

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        String msg = event.getMessage().getUnformattedText().trim();
        if (!this.isInSkywars()) {
            return;
        }
        Matcher matcher = this.joinPattern.matcher(msg);
        if (matcher.find()) {
            String ign = matcher.group(1);
            TimeUtils.setTickDelay(() -> {
                for (NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    UUID targetUUID;
                    GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().version() == 2 || !profile.getName().equals(ign)) continue;
                    UUID uuid = playerInfo.getGameProfile().getId();
                    if (uuid.version() == 1) {
                        Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (denick == null) {
                            targetUUID = uuid;
                            this.tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), profile.getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    IStats stats = this.tabStats.getStats(targetUUID);
                    if (stats != null || targetUUID.version() != 4) break;
                    HypixelAPI.getPlayer(targetUUID).thenAccept(res -> this.mc.addScheduledTask(() -> {
                        try {
                            this.tabStats.refreshPlayer(profile.getId(), res.player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                    break;
                }
            }, 2);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        this.tabStats.clear();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Load event) {
        this.tabStats.clear();
    }

    @SubscribeEvent
    public void onRenderPlayer(NameFormatInTabEvent event) {
        if (this.isInSkywars()) {
            GameProfile profile = event.getPlayerInfo().getGameProfile();
            if (profile.getId().version() == 2) {
                return;
            }
            IStats stats = this.tabStats.getStats(profile.getId());
            event.setText(this.tabStats.getDisplayName(event.getPlayerInfo(), stats));
        }
    }
}

