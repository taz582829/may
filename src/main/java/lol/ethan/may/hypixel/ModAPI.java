package lol.ethan.may.hypixel;

import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import io.netty.buffer.Unpooled;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.EventBus;
import com.lunarclient.apollo.mods.impl.*;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.lunar.ApolloUtils;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.HypixelModAPIImplementation;
import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import java.util.logging.Logger;

@SuppressWarnings("ApiStatus.Internal")
public class ModAPI implements HypixelModAPIImplementation {
    private static final Logger LOGGER = Logger.getLogger("frost-hypixel-modapi");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean isOnHypixel;

    public static void setIsOnHypixel(final boolean isOnHypixel) {
        Entry.debug("is on hypixel: " + isOnHypixel);
        ModAPI.isOnHypixel = isOnHypixel;
    }

    public static boolean isOnHypixel() {
        return isOnHypixel;
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof S3FPacketCustomPayload) {
            final S3FPacketCustomPayload packet = (S3FPacketCustomPayload) event.getPacket();
            final String channel = packet.getChannelName();

            if (!HypixelModAPI.getInstance().getRegistry().isRegistered(channel)) return;
            final PacketBuffer buffer = packet.getBufferData();
            mc.addScheduledTask(() -> {
                try {
                    HypixelModAPI.getInstance().handle(channel, new PacketSerializer(buffer));
                } catch (final Exception e) {
                    e.printStackTrace();
                } finally {
                    buffer.release();
                }
            });
        }
    }

    @SubscribeEvent
    public void onDisconnect(final lol.ethan.may.events.ClientDisconnectFromServerEvent event) {
        setIsOnHypixel(false);
    }

    @Override
    public void onInit() {
        HypixelModAPI.getInstance().createHandler(ClientboundHelloPacket.class, packet -> setIsOnHypixel(true));
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> {
            if (Config.forceFreelook.enabled) ApolloUtils.setModSetting(ModFreelook.ENABLED, Value.newBuilder().setBoolValue(true).build())
        });
        EventBus.subscribe(this);
    }

    @Override
    public boolean sendPacket(final HypixelPacket packet) {
        if (!isOnHypixel || mc.getNetHandler() == null) return false;

        final PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        final PacketSerializer serializer = new PacketSerializer(buffer);
        packet.write(serializer);
        final C17PacketCustomPayload payload = new C17PacketCustomPayload(packet.getIdentifier(), buffer);

        mc.getNetHandler().addToSendQueue(payload);
        return true;
    }

    @Override
    public boolean isConnectedToHypixel() {
        return isOnHypixel;
    }
}
