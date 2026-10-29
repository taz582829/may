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
                    if (profile.getId().version() == 2) continue;

                    final UUID targetUUID;

                    if (profile.getId().version() == 1) {
                        final Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (denick == null) {
                            targetUUID = profile.getId();

                            this.tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    final IStats stats = this.tabStats.getStats(targetUUID);

                    if (stats == null && profile.getId().version() == 4) {
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
                    if (profile.getId().version() == 2) continue;

                    final UUID targetUUID;

                    if (profile.getId().version() == 1) {
                        final Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (denick == null) {
                            targetUUID = profile.getId();

                            this.tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    final IStats stats = this.tabStats.getStats(targetUUID);

                    if (stats == null && profile.getId().version() == 4) {
                        HypixelAPI.getPlayer(targetUUID).thenAccept(res ->
                                this.mc.addScheduledTask(() -> {
                                    try {
                                        this.tabStats.refreshPlayer(profile.getId(), res.player, "bedwars");
                                        DuelsUtils.showDuelsStats(res.player, DuelsUtils.DuelsMode.BEDWARS);
                                    } catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                        );
                    }
                }
            }, 5);
        } else if (msg.equals("Destroy the enemy bed and then eliminate them!")) {
            TimeUtils.setTickDelay(() -> {
                for (final NetworkPlayerInfo playerInfo : this.mc.getNetHandler().getPlayerInfoMap()) {
                    final GameProfile profile = playerInfo.getGameProfile();
                    if (profile.getId().equals(this.mc.thePlayer.getUniqueID())) continue;
                    if (profile.getId().version() == 2) continue;

                    final UUID targetUUID;

                    if (profile.getId().version() == 1) {
                        final Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
                        if (denick == null) {
                            targetUUID = profile.getId();

                            this.tabStats.setDisplayName(playerInfo, ScorePlayerTeam.formatPlayerName(playerInfo.getPlayerTeam(), playerInfo.getGameProfile().getName()) + " §c[N]");
                        } else {
                            targetUUID = UUID.fromString(denick.uuid);
                        }
                    } else {
                        targetUUID = profile.getId();
                    }
                    final IStats stats = this.tabStats.getStats(targetUUID);

                    if (stats == null && profile.getId().version() == 4) {
                        HypixelAPI.getPlayer(targetUUID).thenAccept(res ->
                                this.mc.addScheduledTask(() -> {
                                    try {
                                        this.tabStats.refreshPlayer(profile.getId(), res.player, "bedwars");
                                        DuelsUtils.showDuelsStats(res.player, DuelsUtils.DuelsMode.BEDWARS_RUSH);
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
            if (profile.getId().version() == 2) return;

            final IStats stats = this.tabStats.getStats(profile.getId());

            event.setText(this.tabStats.getDisplayName(event.playerInfo, stats));
        }
    }

    @SubscribeEvent
    public void onPing(final PlayerPingEvent event) {
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            final GameProfile profile = event.getPlayerInfo().getGameProfile();
            if (profile.getId().equals(this.mc.getSession().getProfile().getId())) return;
            if (profile.getId().version() == 2) return;

            final IStats stats = this.tabStats.getStats(profile.getId());

            if (stats != null && stats.getPing() != 1) {
                event.setPing(stats.getPing());
            }
        }
    }

    @SubscribeEvent
    public void onNameFormat(final NameFormatEvent event) {
        if (mc.getNetHandler() == null) return;
        if (DuelsUtils.isInDuels() || DuelsUtils.isInBwDuels()) {
            final EntityPlayer player = event.getPlayer();
            if (player.getUniqueID().equals(this.mc.getSession().getProfile().getId())) return;
            if (player.getUniqueID().version() == 2) return;

            final IStats stats = this.tabStats.getStats(player.getUniqueID());
            final NetworkPlayerInfo playerInfo = this.mc.getNetHandler().getPlayerInfo(player.getUniqueID());
            if (playerInfo == null) return;
            event.setText(this.tabStats.getDisplayName(playerInfo, stats));
        }
    }
}
