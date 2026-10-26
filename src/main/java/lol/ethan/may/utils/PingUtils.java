package lol.ethan.may.utils;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S37PacketStatistics;

import java.util.function.Consumer;

public class PingUtils {

    public static void getPing(final Consumer<Integer> consumer) {
        final Minecraft mc = Minecraft.getMinecraft();
        final long packetSendTime = System.currentTimeMillis();
        EventBus.subscribe(new Object() {

            @SubscribeEvent
            public void onPacketReceive(final PacketEvent.Receive event) {
                if (event.getPacket() instanceof S37PacketStatistics) {
                    consumer.accept((int) (System.currentTimeMillis() - packetSendTime));
                    EventBus.unsubscribe(this);
                }
            }
        });
        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
    }

    public static String formatPing(final int ping) {
        if (50 >= ping) {
            return "§2" + ping + "ms";
        }
        if (100 >= ping) {
            return "§a" + ping + "ms";
        }
        if (150 >= ping) {
            return "§e" + ping + "ms";
        }
        if (200 >= ping) {
            return "§c" + ping + "ms";
        }
        return "§4" + ping + "ms";
    }
}

