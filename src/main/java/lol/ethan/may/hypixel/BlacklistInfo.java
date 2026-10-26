package lol.ethan.may.hypixel;

import com.mojang.authlib.GameProfile;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.PlayerListEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import lol.ethan.may.hypixel.api.urchin.TagsItem;
import lol.ethan.may.hypixel.api.urchin.UrchinAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlacklistInfo {
    private static final Map<String, String> urchinTagMap = new HashMap<>();
    //private static final Map<String, String> seraphTagMap = new HashMap<>();

    @SubscribeEvent
    public void onPlayerJoin(final PlayerListEvent.Add event) {
        if (null == Minecraft.getMinecraft().theWorld) return;
        if (!HypixelUtils.isOnHypixel()) return;
        if (null == Config.urchinKey.value || Config.urchinKey.value.isEmpty()) return;
        final GameProfile profile = event.getPlayerData().getProfile();
        if (2 == profile.getId().version()) return;

        UUID uuid = profile.getId();
        if (1 == uuid.version()) {
            final Denicker.DenickResult result = Denicker.getDenick(profile.getName());
            if (null == result) return;
            uuid = UUID.fromString(result.uuid);
        }
        UrchinAPI.getTag(uuid).whenComplete((res, err) -> {
            if (null != err) {
                err.printStackTrace();
                return;
            }
            if (null == res || null == res.getTags()) return;
            if (0 == res.getTags().size()) return;
            try {
                final NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(profile.getId());
                final String displayName = ScorePlayerTeam.formatPlayerName(
                        playerInfo.getPlayerTeam(),
                        profile.getName()
                );
                Entry.chat(UrchinAPI.formatTags(res, displayName));
                final StringBuilder icon = new StringBuilder();
                for (final TagsItem tag : res.getTags()) {
                    icon.append(UrchinAPI.getTagIcon(tag.getTagType()));
                }
                urchinTagMap.put(profile.getName().trim(), icon.toString());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SubscribeEvent
    public void onTabFormat(final RenderPlayerInTabEvent event) {
        if (!HypixelUtils.isOnHypixel()) return;
        final GameProfile profile = event.getPlayerInfo().getGameProfile();
        if (null == profile) return;
        final String ign = profile.getName();
        if (ign.isEmpty()) return;

        if (urchinTagMap.containsKey(ign)) {
            event.setText(event.getText() + " " + urchinTagMap.get(ign));
        }
    }
    @SubscribeEvent
    public void onNameFormat(final NameFormatEvent event) {
        if (!HypixelUtils.isOnHypixel()) return;
        final String ign = StringUtils.stripControlCodes(event.getPlayer().getName()).trim();
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
