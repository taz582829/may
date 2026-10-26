package lol.ethan.may.hypixel;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.RenderXPEvent;
import lol.ethan.may.events.SubscribeEvent;

public class NickHider {
    @SubscribeEvent
    public void onRenderXP(RenderXPEvent event) {
        if (Config.hideXP.enabled) {
            event.setCancelled(true);
        }
    }
}

