package lol.ethan.may.hypixel.api.urchin;

import lol.ethan.may.config.Config;
import lol.ethan.may.utils.Cache;
import lol.ethan.may.utils.HttpUtils;
import lol.ethan.may.utils.JsonUtils;
import lol.ethan.may.utils.MayStringUtils;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UrchinAPI {
    public static final ZoneId URCHIN_TIME_ZONE = ZoneOffset.UTC;
    private static final Cache<String, UrchinTagsResponse> urchinTags = new Cache(600000L);

    public static CompletableFuture<UrchinTagsResponse> getTag(UUID uuid) {
        return UrchinAPI.getTag(uuid.toString());
    }

    public static CompletableFuture<UrchinTagsResponse> getTag(String identifier) {
        if (urchinTags.has(identifier)) {
            return CompletableFuture.completedFuture(urchinTags.get(identifier));
        }
        return HttpUtils.get("https://api.urchin.gg/v3/player/tags?player=" + identifier + "&key=" + Config.urchinKey.value).thenApply(r -> {
            UrchinTagsResponse res = JsonUtils.gson.fromJson(r, UrchinTagsResponse.class);
            if (Config.isDebugLogging()) {
                System.out.println(identifier + ": " + r);
            }
            urchinTags.put(identifier, res);
            return res;
        });
    }

    public static IChatComponent formatTags(UrchinTagsResponse res, String dpn) {
        List<TagsItem> tags = res.getTags();
        String displayName = res.getDisplayname() == null ? dpn : res.getDisplayname();
        ChatComponentText comp = new ChatComponentText("§dMay §8» §r" + displayName + " §fhas §5" + tags.size() + " §fUrchin tag" + (tags.size() > 1 ? "s" : "") + ":");
        for (TagsItem tag : tags) {
            comp.appendText("\n");
            ChatComponentText newComponent = new ChatComponentText("§5» §r");
            ChatStyle style = new ChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§7" + tag.getReason())));
            long timestamp = tag.getAddedOn();
            String ago = MayStringUtils.getTimeSince(timestamp, System.currentTimeMillis());
            String tagText = MayStringUtils.capitalizeText(tag.getTagType());
            newComponent.appendText(UrchinAPI.getTagColor(tag.getTagType()) + tagText + " §8(" + ago + ")");
            if (!tag.getTagType().equals("Replays Needed")) {
                newComponent.setChatStyle(style);
            }
            comp.appendSibling(newComponent);
        }
        return comp;
    }

    public static IChatComponent formatTags(UrchinTagsResponse res) {
        return UrchinAPI.formatTags(res, null);
    }

    public static EnumChatFormatting getTagColor(String tag) {
        switch (tag) {
            case "confirmed_cheater": {
                return EnumChatFormatting.DARK_PURPLE;
            }
            case "blatant_cheater": {
                return EnumChatFormatting.GOLD;
            }
            case "closet_cheater": {
                return EnumChatFormatting.YELLOW;
            }
            case "sniper": {
                return EnumChatFormatting.DARK_RED;
            }
            case "replays_needed": {
                return EnumChatFormatting.GRAY;
            }
        }
        return EnumChatFormatting.LIGHT_PURPLE;
    }

    public static String getTagIcon(String tag) {
        if (tag.equals("caution")) {
            return EnumChatFormatting.GOLD + "⚠" + EnumChatFormatting.RESET;
        }
        return UrchinAPI.getTagColor(tag) + "✹" + EnumChatFormatting.RESET;
    }
}

