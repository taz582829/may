package lol.ethan.may.hypixel.tnttag;/*
package lol.ethan.may.hypixel.tnttag;

import lol.ethan.may.Entry;
import lol.ethan.may.events.player.EffectEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;

public class EffectAlert {
    @SubscribeEvent
    public void onEffect(EffectEvent.Add event) {
        if (TagUtils.isInTag() && event.effect.getPotionID() == Potion.moveSlowdown.getId() && event.target.equals(Minecraft.getMinecraft().thePlayer)) {
            Entry.playSound("random.burp", 1, 0.45F);
        }
    }
    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (TagUtils.isInTag()) {
            String msg = event.getMessage().getUnformattedText();
            if (msg.toLowerCase().contains("you got lucky")) Entry.playSound("random.burp", 1, 1.5F);
        }
    }
}

 */