package lol.ethan.may.hypixel.duels;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import lol.ethan.may.events.player.PlayerPingEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.stats.IStats;
import lol.ethan.may.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.UUID;

public class DuelsStatsManager {
    Minecraft mc = Minecraft.getMinecraft();
    DuelsTabStats tabStats = new DuelsTabStats();

    @SubscribeEvent
    public void onChat(final ChatEvent.Received event) {
        final String msg = event.getMessage().getUnformattedText().trim();
        if (msg.equals("Eliminate your opponents!")) {
            TimeUtils.setTickDelay(() -> {
                for (final NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    final GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().equals(this.mc.thePlayer.getUniqueID())) continue;
                    if (2 == profile.getId().version()) continue;

                    final UUID targetUUID;

                    if (1 == profile.getId().version()) {
                        final Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (null == denick) {
                            targetUUID = profile.getId();

                            this.tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    final IStats stats = this.tabStats.getStats(targetUUID);

                    if (null == stats && 4 == profile.getId().version()) {
                        HypixelAPI.getPlayer(targetUUID).thenAccept(res ->
                                this.mc.addScheduledTask(() -> {
                                    try {
                                        this.tabStats.refreshPlayer(profile.getId(), res.player);
                                        DuelsUtils.showDuelsStats(res.player, DuelsUtils.getCurrentMode());
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                        );
                    }
                }
            }, 5);
        } else if (msg.equals("Protect your bed and destroy the enemy bed.")) {
            TimeUtils.setTickDelay(() -> {
                for (final NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    final GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().equals(this.mc.thePlayer.getUniqueID())) continue;
                    if (2 == profile.getId().version()) continue;

                    final UUID targetUUID;

                    if (1 == profile.getId().version()) {
                        final Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (null == denick) {
                            targetUUID = profile.getId();

                            this.tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    final IStats stats = this.tabStats.getStats(targetUUID);

                    if (null == stats && 4 == profile.getId().version()) {
                        HypixelAPI.getPlayer(targetUUID).thenAccept(res ->
                                this.mc.addScheduledTask(() -> {
                                    try {
                                        this.tabStats.refreshPlayer(profile.getId(), res.player, "bedwars");
                                    } catch (final Exception e) {
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
    public void onRenderPlayer(final RenderPlayerInTabEvent event) {
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            final GameProfile profile = event.playerInfo.getGameProfile();
            if (profile.getId().equals(this.mc.getSession().getProfile().getId())) return;
            if (2 == profile.getId().version()) return;

            final IStats stats = this.tabStats.getStats(profile.getId());

            event.text = this.tabStats.getDisplayName(event.playerInfo, stats);
        }
    }

    @SubscribeEvent
    public void onPing(final PlayerPingEvent event) {
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            final GameProfile profile = event.getPlayerInfo().getGameProfile();
            if (profile.getId().equals(this.mc.getSession().getProfile().getId())) return;
            if (2 == profile.getId().version()) return;

            final IStats stats = this.tabStats.getStats(profile.getId());

            if (null != stats && 1 != stats.getPing()) {
                event.setPing(stats.getPing());
            }
        }
    }

    @SubscribeEvent
    public void onNameFormat(final NameFormatEvent event) {
        if (null == mc.getNetHandler()) return;
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            final EntityPlayer player = event.getPlayer();
            if (player.getUniqueID().equals(this.mc.getSession().getProfile().getId())) return;
            if (2 == player.getUniqueID().version()) return;

            final IStats stats = this.tabStats.getStats(player.getUniqueID());
            final NetworkPlayerInfo playerInfo = this.mc.getNetHandler().getPlayerInfo(player.getUniqueID());
            if (null == playerInfo) return;
            event.setText(this.tabStats.getDisplayName(playerInfo, stats));
        }
    }
}
