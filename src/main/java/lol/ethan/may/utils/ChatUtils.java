package lol.ethan.may.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class ChatUtils {

    public static IChatComponent createChatButton(final String text, final String hoverText, final ClickEvent.Action clickEvent, final String clickMessage) {
        final ChatStyle style = new ChatStyle();
        final ChatComponentText msg = new ChatComponentText(text);
        style.setChatClickEvent(new ClickEvent(clickEvent, clickMessage));
        if (!hoverText.isEmpty()) {
            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText)));
        }
        msg.setChatStyle(style);
        return msg;
    }

    public static IChatComponent createChatButton(final String text, final ClickEvent.Action clickEvent, final String clickMessage) {
        final ChatStyle style = new ChatStyle();
        final ChatComponentText msg = new ChatComponentText(text);
        style.setChatClickEvent(new ClickEvent(clickEvent, clickMessage));
        msg.setChatStyle(style);
        return msg;
    }

    public static void editChat(final int messageId, final IChatComponent newMessage) {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(newMessage, messageId);
    }

    public static void delete(final int messageId) {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.getChatGUI().deleteChatLine(messageId);
    }

    public static String getChatBreak() {
        return "§m" + ChatUtils.getChatBreak("-");
    }

    public static String getChatBreak(final String text) {
        final StringBuilder res = new StringBuilder();
        final Minecraft mc = Minecraft.getMinecraft();
        final int length = mc.fontRendererObj.getStringWidth(text);
        final int times = mc.ingameGUI.getChatGUI().getChatWidth() / length;
        for (int i = 0; i < times; i++) {
            res.append(text);
        }
        return res.toString();
    }
}

