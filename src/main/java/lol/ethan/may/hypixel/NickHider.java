package lol.ethan.may.hypixel;/*
package lol.ethan.may.hypixel;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.render.RenderStringEvent;
import lol.ethan.may.events.render.RenderXPBarEvent;
import net.minecraft.client.Minecraft;
import lol.ethan.may.events.RenderGameOverlayEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.weavemc.api.hook.RenderGameOverlayEventHook;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NickHider {
    Minecraft mc = Minecraft.getMinecraft();
    Pattern scoreboardDatePattern = Pattern.compile("\\d+/\\d+/\\d+ (.*)");

    @SubscribeEvent
    public void onStringRender(RenderStringEvent event) {
        if (Config.nickHider.enabled) {
            if (!Config.nickHider_newName.value.isEmpty() && event.getText().toLowerCase().contains(Config.nickHider_newName.value)) {
                event.replace(mc.getSession().getUsername(), Config.nickHider_newName.value);
            }
            Matcher matcher = scoreboardDatePattern.matcher(event.getText());
            if (matcher.find()) {
                String server = matcher.group(1);
                event.replace(server, "§k" + server);
            }
        }
    }
    @SubscribeEvent
    public void onXPBarRender(RenderXPBarEvent event) {
        if (Config.nickHider.enabled && Config.nickHider_hideXPBar.enabled) {
            event.setCancelled(true);
        }
    }
}

 */