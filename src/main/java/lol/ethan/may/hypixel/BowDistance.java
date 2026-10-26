package lol.ethan.may.hypixel;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BowDistance {
    final Minecraft mc = Minecraft.getMinecraft();
    final Pattern bowPattern = Pattern.compile("^(\\w{1,16}) is on [\\d.]+ HP!");

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (this.mc.theWorld == null || this.mc.thePlayer == null || Config.bowDistance.enabled) {
            return;
        }
        String msg = event.getMessage().getUnformattedText();
        Matcher matcher = this.bowPattern.matcher(msg);
        if (matcher.find()) {
            String ign = matcher.group(1);
            EntityPlayer player = this.mc.theWorld.getPlayerEntityByName(ign);
            if (player == null) {
                return;
            }
            double dist = player.getDistanceToEntity(this.mc.thePlayer);
            event.getMessage().appendText(" §7(" + this.formatDistance(dist) + "m§7)");
        }
    }

    private String formatDistance(double dist) {
        EnumChatFormatting color = dist < 15.0 ? EnumChatFormatting.GREEN : (dist < 30.0 ? EnumChatFormatting.YELLOW : (dist < 45.0 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED));
        return color + String.format("%.2f", dist);
    }
}

