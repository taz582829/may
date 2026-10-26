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
    public static Entry getEntry(UUID uuid) {
        return entryMap.computeIfAbsent(uuid, id -> new Entry());
    }
    @SubscribeEvent
    public void onTick(TickEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player.getUniqueID().version() == 2) continue;
            if (player == mc.thePlayer) continue;

            Entry data = getEntry(player.getUniqueID());
            double dx = player.posX - player.prevPosX;
            double dz = player.posZ - player.prevPosZ;
            data.moveSpeed = Math.sqrt(dx * dx + dz * dz) * 20;
            data.breakDelay = Math.max(0, data.breakDelay - 1);

            if (player.isUsingItem()) {
                data.useItemTicks++;
            } else {
                data.useItemTicks = 0;
            }
            long time = System.currentTimeMillis();
            if (player.isSneaking() && !data.wasShifting) {

                ShiftEvent shiftEvent = new ShiftEvent(
                        ShiftEvent.Type.START,
                        time,
                        player.posX,
                        player.posY,
                        player.posZ
                );
                data.lastShiftEvent = time;
                data.shiftEvents.add(shiftEvent);
            } else if (!player.isSneaking() && data.wasShifting) {
                long duration = time - data.lastShiftEvent;
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
            if (data.shiftEvents.size() > 50) {
                data.shiftEvents.remove(data.shiftEvents.size() - 1);
            }
        }
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        for (Map.Entry<UUID, Entry> player : entryMap.entrySet()) {
            player.getValue().violations.clear();
            player.getValue().flags.clear();
        }
    }
    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        if (event.getPacket() instanceof S0BPacketAnimation) {
            S0BPacketAnimation packet = (S0BPacketAnimation) event.getPacket();
            int entityId = packet.getEntityID();


            if (packet.getAnimationType() == 0) {

                Entity entity = mc.theWorld.getEntityByID(entityId);
                if (!(entity instanceof EntityPlayer)) return;

                EntityPlayer player = (EntityPlayer) entity;
                if (player.getUniqueID().version() == 2) return;
                if (player == mc.thePlayer) return;

                getEntry(player.getUniqueID()).lastSwing = System.currentTimeMillis();
            }
        }
        if (event.getPacket() instanceof S0BPacketAnimation) {
            S0BPacketAnimation packet = (S0BPacketAnimation) event.getPacket();
            int entityId = packet.getEntityID();
            int animationType = packet.getAnimationType();

            if (animationType == 0) {
                Entity entity = mc.theWorld.getEntityByID(entityId);
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    Entry dataEntry = getEntry(player.getUniqueID());
                    long now = System.currentTimeMillis();
                    dataEntry.lastSwing = now;
                    MovingObjectPosition mop = player.rayTrace(
                            5.0D,
                            1.0F
                    );

                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        dataEntry.lastSwingAtBlock = now;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onNameFormat(NameFormatEvent event) {
        EntityPlayer player = event.getPlayer();
        Entry entry = getEntry(player.getUniqueID());
        StringBuilder text = new StringBuilder(event.getText());

        for (AnticheatFlag vio : entry.violations.keySet()) {
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

        public RotationEvent(long timestamp, float yaw, float pitch) {
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

        public ShiftEvent(Type type, long timestamp, double positionX, double positionY, double positionZ) {
            this.type = type;
            this.timestamp = timestamp;
            this.positionX = positionX;
            this.positionY = positionY;
            this.positionZ = positionZ;
        }
        public ShiftEvent(Type type, long timestamp, double positionX, double positionY, double positionZ, double duration) {
            this.type = type;
            this.timestamp = timestamp;
            this.positionX = positionX;
            this.positionY = positionY;
            this.positionZ = positionZ;
            this.duration = duration;
        }
    }


}
