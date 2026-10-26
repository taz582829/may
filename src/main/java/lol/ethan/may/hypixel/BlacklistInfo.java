package lol.ethan.may.hypixel;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.PlayerListEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import lol.ethan.may.hypixel.api.urchin.TagsItem;
import lol.ethan.may.hypixel.api.urchin.UrchinAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.StringUtils;
import lol.ethan.may.events.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlacklistInfo {
    private static final Map<String, String> urchinTagMap = new HashMap<>();
    //private static final Map<String, String> seraphTagMap = new HashMap<>();

    @SubscribeEvent
    public void onPlayerJoin(PlayerListEvent.Add event) {
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (!HypixelUtils.isOnHypixel()) return;
        if (Config.urchinKey.value == null || Config.urchinKey.value.isEmpty()) return;
        final GameProfile profile = event.getPlayerData().getProfile();
        if (profile.getId().version() == 2) return;

        UUID uuid = profile.getId();
        if (uuid.version() == 1) {
            Denicker.DenickResult result = Denicker.getDenick(profile.getName());
            if (result == null) return;
            uuid = UUID.fromString(result.uuid);
        }
        UrchinAPI.getTag(uuid).whenComplete((res, err) -> {
            if (err != null) {
                err.printStackTrace();
                return;
            }
            if (res == null || res.getTags() == null) return;
            if (res.getTags().size() == 0) return;
            try {
                NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(profile.getId());
                String displayName = ScorePlayerTeam.formatPlayerName(
                        playerInfo.getPlayerTeam(),
                        profile.getName()
                );
                Entry.chat(UrchinAPI.formatTags(res, displayName));
                StringBuilder icon = new StringBuilder();
                for (TagsItem tag : res.getTags()) {
                    icon.append(UrchinAPI.getTagIcon(tag.getTagType()));
                }
                urchinTagMap.put(profile.getName().trim(), icon.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SubscribeEvent
    public void onTabFormat(RenderPlayerInTabEvent event) {
        if (!HypixelUtils.isOnHypixel()) return;
        GameProfile profile = event.getPlayerInfo().getGameProfile();
        if (profile == null) return;
        String ign = profile.getName();
        if (ign.isEmpty()) return;

        if (urchinTagMap.containsKey(ign)) {
            event.setText(event.getText() + " " + urchinTagMap.get(ign));
        }
    }
    @SubscribeEvent
    public void onNameFormat(NameFormatEvent event) {
        if (!HypixelUtils.isOnHypixel()) return;
        String ign = StringUtils.stripControlCodes(event.getPlayer().getName()).trim();
        if (ign.isEmpty()) return;
        String text = event.getText();

        if (urchinTagMap.containsKey(ign)) {
            text += " ";
            if (urchinTagMap.containsKey(ign)) {
                text += urchinTagMap.get(ign);
            }
        }
        event.setText(text);
    }
}
