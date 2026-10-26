package lol.ethan.may;

import lol.ethan.may.events.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

public class ChatListener {
    @SubscribeEvent(priority = Priority.HIGHEST)
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) event.getPacket();
            ChatEvent.Received chatEvent = new ChatEvent.Received(packet.getChatComponent(), packet.getType());
            EventBus.postEvent(chatEvent);
            if (chatEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            event.setPacket(new S02PacketChat(chatEvent.getMessage(), chatEvent.getType()));
        }
    }

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

