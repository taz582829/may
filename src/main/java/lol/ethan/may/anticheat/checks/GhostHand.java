package lol.ethan.may.anticheat.checks;

import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;

public class GhostHand extends AnticheatFlag {
    public GhostHand() {
        super(5, EnumChatFormatting.RED);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S0BPacketAnimation) {
            S0BPacketAnimation packet = (S0BPacketAnimation) event.getPacket();
            int entityId = packet.getEntityID();
            Entity entity = this.mc.theWorld.getEntityByID(entityId);
            if (!(entity instanceof EntityPlayer)) {
                return;
            }
            EntityPlayer player = (EntityPlayer) entity;
            MovingObjectPosition movingObjectPosition = player.rayTrace(4.5, 1.0f);
        }
    }
}

