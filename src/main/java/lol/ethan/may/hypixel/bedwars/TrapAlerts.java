package lol.ethan.may.hypixel.bedwars;

import lol.ethan.may.Entry;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;

public class TrapAlerts {
    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S1DPacketEntityEffect && BedwarsUtils.isInBedwars()) {
            S1DPacketEntityEffect packet = (S1DPacketEntityEffect) event.getPacket();
            if (packet.getEffectId() == Potion.digSlowdown.getId()) {
                Entry.chat(Entry.PREFIX + "§cYou activated a Mining Fatigue trap!");
            }
        }
    }
}
