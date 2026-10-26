package lol.ethan.may.hypixel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BowDistance {
    final Minecraft mc = Minecraft.getMinecraft();
    final Pattern bowPattern = Pattern.compile("^(\\w{1,16}) is on [\\d.]+ HP!");

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        final String msg = event.getMessage().getUnformattedText();
        final Matcher matcher = bowPattern.matcher(msg);

        if (matcher.find()) {
            final String ign = matcher.group(1);
            final EntityPlayer player = mc.theWorld.getPlayerEntityByName(ign);
            if (player == null) return;

            final double dist = player.getDistanceToEntity(mc.thePlayer);
            event.getMessage().appendText(" §7(" + formatDistance(dist) + "m§7)");
        }
    }

    private String formatDistance(double dist) {
        EnumChatFormatting color;
        if (dist < 15) color = EnumChatFormatting.GREEN;
        else if (dist < 30) color = EnumChatFormatting.YELLOW;
        else if (dist < 45) color = EnumChatFormatting.GOLD;
        else color = EnumChatFormatting.RED;

        return color + String.format("%.2f", dist);
    }
}
