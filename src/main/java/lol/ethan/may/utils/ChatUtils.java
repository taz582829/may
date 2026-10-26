package lol.ethan.may.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class ChatUtils {

    public static IChatComponent createChatButton(String text, String hoverText, ClickEvent.Action clickEvent, String clickMessage) {
        ChatStyle style = new ChatStyle();
        ChatComponentText msg = new ChatComponentText(text);
        style.setChatClickEvent(new ClickEvent(clickEvent, clickMessage));
        if (!hoverText.isEmpty()) {
            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText)));
        }
        msg.setChatStyle(style);
        return msg;
    }

    public static IChatComponent createChatButton(String text, ClickEvent.Action clickEvent, String clickMessage) {
        ChatStyle style = new ChatStyle();
        ChatComponentText msg = new ChatComponentText(text);
        style.setChatClickEvent(new ClickEvent(clickEvent, clickMessage));
        msg.setChatStyle(style);
        return msg;
    }

    public static void editChat(int messageId, IChatComponent newMessage) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(newMessage, messageId);
    }

    public static void delete(int messageId) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.getChatGUI().deleteChatLine(messageId);
    }

    public static String getChatBreak() {
        return "§m" + ChatUtils.getChatBreak("-");
    }

    public static String getChatBreak(String text) {
        StringBuilder res = new StringBuilder();
        Minecraft mc = Minecraft.getMinecraft();
        int length = mc.fontRendererObj.getStringWidth(text);
        int times = mc.ingameGUI.getChatGUI().getChatWidth() / length;
        for (int i = 0; i < times; i++) {
            res.append(text);
        }
        return res.toString();
    }
}

