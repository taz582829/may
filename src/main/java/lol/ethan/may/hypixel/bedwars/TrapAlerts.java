package lol.ethan.may.hypixel.bedwars;

import lol.ethan.may.Entry;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;

public class TrapAlerts {
    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        S1DPacketEntityEffect packet;
        if (event.getPacket() instanceof S1DPacketEntityEffect && BedwarsUtils.isInBedwars() && (packet = (S1DPacketEntityEffect) event.getPacket()).getEffectId() == Potion.digSlowdown.getId()) {
            Entry.chat("§dMay §8» §r§cYou activated a Mining Fatigue trap!");
        }
    }
}

