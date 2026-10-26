package lol.ethan.may.hypixel;

import io.netty.buffer.Unpooled;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.HypixelModAPIImplementation;
import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import java.util.logging.Logger;

@SuppressWarnings(value = "ApiStatus.Internal")
public class ModAPI implements HypixelModAPIImplementation {
    private static final Logger LOGGER = Logger.getLogger("frost-hypixel-modapi");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean isOnHypixel = false;

    public static void setIsOnHypixel(boolean isOnHypixel) {
        ModAPI.isOnHypixel = isOnHypixel;
    }

    public static boolean isOnHypixel() {
        return isOnHypixel;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload packet = (S3FPacketCustomPayload) event.getPacket();
            String channel = packet.getChannelName();

            if (!HypixelModAPI.getInstance().getRegistry().isRegistered(channel)) return;
            PacketBuffer buffer = packet.getBufferData();
            mc.addScheduledTask(() -> {
                try {
                    HypixelModAPI.getInstance().handle(channel, new PacketSerializer(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    buffer.release();
                }
            });
        }
    }

    @SubscribeEvent
    public void onDisconnect(lol.ethan.may.events.ClientDisconnectFromServerEvent event) {
        isOnHypixel = false;
    }

    @Override
    public void onInit() {
        HypixelModAPI.getInstance().createHandler(ClientboundHelloPacket.class, packet -> isOnHypixel = true);
        EventBus.subscribe(this);
    }

    @Override
    public boolean sendPacket(HypixelPacket packet) {
        if (!isOnHypixel) return false;

        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        PacketSerializer serializer = new PacketSerializer(buffer);
        packet.write(serializer);
        C17PacketCustomPayload payload = new C17PacketCustomPayload(packet.getIdentifier(), buffer);

        mc.getNetHandler().addToSendQueue(payload);
        return true;
    }

    @Override
    public boolean isConnectedToHypixel() {
        return isOnHypixel;
    }
}
