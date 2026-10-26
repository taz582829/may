package lol.ethan.may;

import lol.ethan.may.events.*;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class ChatListener {
    @SubscribeEvent(priority = Priority.HIGHEST)
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
            ChatEvent.Send chatEvent = new ChatEvent.Send(packet.getMessage());
            EventBus.postEvent(chatEvent);
            if (chatEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            event.setPacket(new C01PacketChatMessage(chatEvent.getMessage()));
        }
    }
}

