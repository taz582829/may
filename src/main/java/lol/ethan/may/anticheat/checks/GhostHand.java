package lol.ethan.may.anticheat.checks;

import lol.ethan.may.anticheat.AnticheatFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;

// detects if a player broke a block through an entity
public class GhostHand extends AnticheatFlag {
    public GhostHand() {
        super(5, EnumChatFormatting.RED);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S0BPacketAnimation) {
            S0BPacketAnimation packet = (S0BPacketAnimation) event.getPacket();
            int entityId = packet.getEntityID();

            Entity entity = mc.theWorld.getEntityByID(entityId);
            if (!(entity instanceof EntityPlayer)) return;

            EntityPlayer player = (EntityPlayer) entity;
            MovingObjectPosition mop = player.rayTrace(
                    4.5,
                    1
            );
//            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
//                mop
//            }
        }
    }
}
