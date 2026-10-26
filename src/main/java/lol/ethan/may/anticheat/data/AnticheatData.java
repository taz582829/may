package lol.ethan.may.anticheat.data;

import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import lol.ethan.may.events.WorldEvent;
import lol.ethan.may.events.player.NameFormatEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.util.MovingObjectPosition;

import java.util.*;

public class AnticheatData {
    public static final String FLAG_ICON = "⚑";
    private static final Map<UUID, Entry> entryMap = new HashMap<>();
    public static Entry getEntry(final UUID uuid) {
        return entryMap.computeIfAbsent(uuid, id -> new Entry());
    }
    @SubscribeEvent
    public void onTick(final TickEvent.Post event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (null == mc.theWorld) return;

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (2 == player.getUniqueID().version()) continue;
            if (player == mc.thePlayer) continue;

            final Entry data = getEntry(player.getUniqueID());
            final double dx = player.posX - player.prevPosX;
            final double dz = player.posZ - player.prevPosZ;
            data.moveSpeed = Math.sqrt(dx * dx + dz * dz) * 20;
            data.breakDelay = Math.max(0, data.breakDelay - 1);

            if (player.isUsingItem()) {
                data.useItemTicks++;
            } else {
                data.useItemTicks = 0;
            }
            final long time = System.currentTimeMillis();
            if (player.isSneaking() && !data.wasShifting) {

                final ShiftEvent shiftEvent = new ShiftEvent(
                        ShiftEvent.Type.START,
                        time,
                        player.posX,
                        player.posY,
                        player.posZ
                );
                data.lastShiftEvent = time;
                data.shiftEvents.add(shiftEvent);
            } else if (!player.isSneaking() && data.wasShifting) {
                final long duration = time - data.lastShiftEvent;
                data.shiftEvents.add(new ShiftEvent(
                        ShiftEvent.Type.STOP,
                        time,
                        player.posX,
                        player.posY,
                        player.posZ,
                        duration
                ));
            }
            if (!player.onGround) data.lastAirTime = time;
            data.wasShifting = player.isSneaking();
            if (50 < data.shiftEvents.size()) {
                data.shiftEvents.remove(data.shiftEvents.size() - 1);
            }
        }
    }
    @SubscribeEvent
    public void onWorldUnload(final WorldEvent.Unload event) {
        for (final Map.Entry<UUID, Entry> player : entryMap.entrySet()) {
            player.getValue().violations.clear();
            player.getValue().flags.clear();
        }
    }
    @SubscribeEvent
    public void onPacket(final PacketEvent.Receive event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (null == mc.theWorld || null == mc.thePlayer) return;

        if (event.getPacket() instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packet = (S0BPacketAnimation) event.getPacket();
            final int entityId = packet.getEntityID();


            if (0 == packet.getAnimationType()) {

                final Entity entity = mc.theWorld.getEntityByID(entityId);
                if (!(entity instanceof EntityPlayer)) return;

                final EntityPlayer player = (EntityPlayer) entity;
                if (2 == player.getUniqueID().version()) return;
                if (player == mc.thePlayer) return;

                getEntry(player.getUniqueID()).lastSwing = System.currentTimeMillis();
            }
        }
        if (event.getPacket() instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packet = (S0BPacketAnimation) event.getPacket();
            final int entityId = packet.getEntityID();
            final int animationType = packet.getAnimationType();

            if (0 == animationType) {
                final Entity entity = mc.theWorld.getEntityByID(entityId);
                if (entity instanceof EntityPlayer) {
                    final EntityPlayer player = (EntityPlayer) entity;
                    final Entry dataEntry = getEntry(player.getUniqueID());
                    final long now = System.currentTimeMillis();
                    dataEntry.lastSwing = now;
                    final MovingObjectPosition mop = player.rayTrace(
                            5.0D,
                            1.0F
                    );

                    if (null != mop && MovingObjectPosition.MovingObjectType.BLOCK == mop.typeOfHit) {
                        dataEntry.lastSwingAtBlock = now;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onNameFormat(final NameFormatEvent event) {
        final EntityPlayer player = event.getPlayer();
        final Entry entry = getEntry(player.getUniqueID());
        final StringBuilder text = new StringBuilder(event.getText());

        for (final AnticheatFlag vio : entry.violations.keySet()) {
            text.append(" ").append(vio.color).append(FLAG_ICON);
        }
        event.setText(text.toString());
    }

    public static class Entry {
        public long lastSwing;
        public long lastShiftEvent;
        public int useItemTicks;
        public double moveSpeed;
        public boolean wasShifting;
        public List<ShiftEvent> shiftEvents = new ArrayList<>();
        public final Map<AnticheatFlag, Integer> flags = new HashMap<>();
        public final Map<AnticheatFlag, Integer> violations = new HashMap<>();
        public long lastSwingAtBlock;
        public int breakDelay;
        public long lastAirTime;
        public float lastYaw;
        public int displaceStage;
        public int ticksSinceDisplace;

        public List<RotationEvent> rotationEvents = new ArrayList<>();
    }
    public static class RotationEvent {
        public long timestamp;
        public float yaw;
        public float pitch;

        public RotationEvent(final long timestamp, final float yaw, final float pitch) {
            this.timestamp = timestamp;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
    public static class ShiftEvent {
        public enum Type{
            START,
            STOP
        }
        public Type type;
        public long timestamp;
        public double positionX;
        public double positionY;
        public double positionZ;
        public double duration;

        public ShiftEvent(final Type type, final long timestamp, final double positionX, final double positionY, final double positionZ) {
            this.type = type;
            this.timestamp = timestamp;
            this.positionX = positionX;
            this.positionY = positionY;
            this.positionZ = positionZ;
        }
        public ShiftEvent(final Type type, final long timestamp, final double positionX, final double positionY, final double positionZ, final double duration) {
            this.type = type;
            this.timestamp = timestamp;
            this.positionX = positionX;
            this.positionY = positionY;
            this.positionZ = positionZ;
            this.duration = duration;
        }
    }


}
