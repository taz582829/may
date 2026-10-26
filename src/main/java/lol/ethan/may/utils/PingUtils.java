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
        Minecraft mc = Minecraft.getMinecraft();
        final long packetSendTime = System.currentTimeMillis();
        EventBus.subscribe(new Object() {

            @SubscribeEvent
            public void onPacketReceive(PacketEvent.Receive event) {
                if (event.getPacket() instanceof S37PacketStatistics) {
                    consumer.accept((int) (System.currentTimeMillis() - packetSendTime));
                    EventBus.unsubscribe(this);
                }
            }
        });
        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
    }

    public static String formatPing(int ping) {
        if (ping <= 50) {
            return "§2" + ping + "ms";
        }
        if (ping <= 100) {
            return "§a" + ping + "ms";
        }
        if (ping <= 150) {
            return "§e" + ping + "ms";
        }
        if (ping <= 200) {
            return "§c" + ping + "ms";
        }
        return "§4" + ping + "ms";
    }
}

