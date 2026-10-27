package lol.ethan.may.hypixel;

import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class HypixelEmojis {
    private static final Map<String, String> emojis = new HashMap<>();

    static {
        emojis.put("<3", "❤");
        emojis.put(":star:", "✮");
        emojis.put("o/", " ( ﾟ◡ﾟ)/");
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent.Send event) {
//        if (!(event.getPacket() instanceof C01PacketChatMessage)) return;
//
//        C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
//        String replaced = packet.getMessage();
//        for (Map.Entry<String, String> entry : emojis.entrySet()) {
//            replaced = replaced.replace(entry.getKey(), entry.getValue());
//        }
//        event.setPacket(new C01PacketChatMessage(replaced));
    }
}
