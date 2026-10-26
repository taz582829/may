package lol.ethan.may.anticheat.checks;

import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public class BedNuker {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final EnumFacing[] HORIZONTALS = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST};

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
            BlockPos blockPos = this.rayTrace(player);
        }
    }

    private BlockPos rayTrace(EntityPlayer player) {
        MovingObjectPosition mop = player.rayTrace(5.0, 1.0f);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return null;
        }
        return mop.getBlockPos();
    }

    private BlockPos getNearbyBed(BlockPos pos) {
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    BlockPos check = pos.add(x, y, z);
                    if (this.mc.theWorld.getBlockState(check).getBlock() != Blocks.bed) continue;
                    return check;
                }
            }
        }
        return null;
    }

    private BlockPos getOtherBedHalf(BlockPos bed) {
        for (EnumFacing facing : this.HORIZONTALS) {
            BlockPos check = bed.offset(facing);
            if (this.mc.theWorld.getBlockState(check).getBlock() != Blocks.bed) continue;
            return check;
        }
        return null;
    }
}

