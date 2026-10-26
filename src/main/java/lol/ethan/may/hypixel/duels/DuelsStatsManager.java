package lol.ethan.may.hypixel.duels;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import lol.ethan.may.events.player.NameFormatInTabEvent;
import lol.ethan.may.events.player.PlayerPingEvent;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.stats.IStats;
import lol.ethan.may.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

import java.util.UUID;

public class DuelsStatsManager {
    Minecraft mc = Minecraft.getMinecraft();
    DuelsTabStats tabStats = new DuelsTabStats();

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        String msg = event.getMessage().getUnformattedText().trim();
        if (msg.equals("Eliminate your opponents!")) {
            TimeUtils.setTickDelay(() -> {
                for (NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    IStats stats;
                    UUID targetUUID;
                    GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().equals(this.mc.thePlayer.getUniqueID()) || profile.getId().version() == 2)
                        continue;
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
                    if ((stats = this.tabStats.getStats(targetUUID)) != null || profile.getId().version() != 4)
                        continue;
                    HypixelAPI.getPlayer(targetUUID).thenAccept(res -> this.mc.addScheduledTask(() -> {
                        try {
                            this.tabStats.refreshPlayer(profile.getId(), res.player);
                            DuelsUtils.showDuelsStats(res.player, DuelsUtils.getCurrentMode());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                }
            }, 5);
        } else if (msg.equals("Protect your bed and destroy the enemy bed.")) {
            TimeUtils.setTickDelay(() -> {
                for (NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    IStats stats;
                    UUID targetUUID;
                    GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().equals(this.mc.thePlayer.getUniqueID()) || profile.getId().version() == 2)
                        continue;
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
                    if ((stats = this.tabStats.getStats(targetUUID)) != null || profile.getId().version() != 4)
                        continue;
                    HypixelAPI.getPlayer(targetUUID).thenAccept(res -> this.mc.addScheduledTask(() -> {
                        try {
                            this.tabStats.refreshPlayer(profile.getId(), res.player, "bedwars");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                }
            }, 5);
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(NameFormatInTabEvent event) {
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            GameProfile profile = event.getPlayerInfo().getGameProfile();
            if (profile.getId().equals(this.mc.getSession().getProfile().getId())) {
                return;
            }
            if (profile.getId().version() == 2) {
                return;
            }
            IStats stats = this.tabStats.getStats(profile.getId());
            event.setText(this.tabStats.getDisplayName(event.getPlayerInfo(), stats));
        }
    }

    @SubscribeEvent
    public void onPing(PlayerPingEvent event) {
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            GameProfile profile = event.getPlayerInfo().getGameProfile();
            if (profile.getId().equals(this.mc.getSession().getProfile().getId())) {
                return;
            }
            if (profile.getId().version() == 2) {
                return;
            }
            IStats stats = this.tabStats.getStats(profile.getId());
            event.setPing(stats.getPing());
        }
    }

    @SubscribeEvent
    public void onNameFormat(NameFormatEvent event) {
        if (this.mc.getNetHandler() == null) {
            return;
        }
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            EntityPlayer player = event.getPlayer();
            if (player.getUniqueID().equals(this.mc.getSession().getProfile().getId())) {
                return;
            }
            if (player.getUniqueID().version() == 2) {
                return;
            }
            IStats stats = this.tabStats.getStats(player.getUniqueID());
            NetworkPlayerInfo playerInfo = this.mc.getNetHandler().getPlayerInfo(player.getUniqueID());
            if (playerInfo == null) {
                return;
            }
            event.setText(this.tabStats.getDisplayName(playerInfo, stats));
        }
    }
}

