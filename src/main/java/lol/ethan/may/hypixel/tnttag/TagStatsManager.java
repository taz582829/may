package lol.ethan.may.hypixel.tnttag;/*
package lol.ethan.may.hypixel.tnttag;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.PlayerListEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import lol.ethan.may.events.player.PlayerEvent;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.stats.IStats;
//import lol.ethan.may.tag.TagTimer;
import net.minecraft.client.Minecraft;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagStatsManager {
    TNTTagTabStats tabStats = new TNTTagTabStats();
    Map<String, String> winMap = new HashMap<>();

    @SubscribeEvent
    public void onPlayerJoin(PlayerListEvent.Add event) {
        GameProfile profile = event.getPlayerData().getProfile();
        if (profile.getId().version() == 2) return;

        if (!TagUtils.isInTag()) return;
        UUID targetUUID;

        if (profile.getId().version() == 1) {
            Denicker.DenickResult denick = Denicker.getDenick(profile.getName());
            if (denick == null) {
                targetUUID = profile.getId();
            } else {
                targetUUID = UUID.fromString(denick.uuid);
            }
        } else {
            targetUUID = profile.getId();
        }
        TNTTagTabStats.TagStats stats = (TNTTagTabStats.TagStats) tabStats.getStats(targetUUID);

        if (stats == null && profile.getId().version() == 4) {
            HypixelAPI.getPlayer(targetUUID).thenAccept(res ->
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        try {
                            TNTTagTabStats.TagStats newStats = (TNTTagTabStats.TagStats) tabStats.refreshPlayer(profile.getId(), res.player);
                            winMap.put(profile.getName(), newStats.getWins());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            );
        } else if (stats != null && profile.getId().version() == 4) {
            winMap.put(profile.getName(), stats.getWins());
        }
    }
    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (event.getMessage().getUnformattedText().trim().startsWith("The TNT has been released")) {
            //TagTimer.timerRecords.clear();
        }
    }

    @SubscribeEvent
    public void onPing(PingEvent event) {
        GameProfile profile = event.getPlayerInfo().getGameProfile();
        if (profile == null) return;
        IStats stats = tabStats.getStats(profile.getId());
        //event.text = tabStats.getDisplayName(event.playerInfo, stats);
        if (stats != null && stats.getPing() != 1) {
            event.setPing(stats.getPing());
        }
    }

    @SubscribeEvent
    public void onNameFormat(NameFormatEvent event) {
        if (TagUtils.isInTag() && Config.tagTabStats.enabled) {
            String wins = winMap.get(event.getPlayer().getName());
            if (wins == null) return;

            event.setText(event.getText() + " " + wins);

        }
    }
    @SubscribeEvent
    public void onTabRender(RenderPlayerInTabEvent event) {
        if (TagUtils.isInTag() && Config.tagTabStats.enabled) {
            String wins = winMap.get(event.playerInfo.getGameProfile().getName());
            if (wins == null) return;

            event.text += " " + wins;

        }
    }
}

 */