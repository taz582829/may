package lol.ethan.may.anticheat.data;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.player.BlockBreakEvent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.Comparator;
import java.util.List;

public class BlockBreakData {
    Minecraft mc = Minecraft.getMinecraft();
    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event.getPacket() instanceof S23PacketBlockChange) {
            S23PacketBlockChange packet = (S23PacketBlockChange) event.getPacket();
            IBlockState state = packet.blockState;
            BlockPos bp = packet.getBlockPosition();
            if (state.getBlock() == null || state.getBlock().getMaterial() == Material.air) {
                AxisAlignedBB bb = new AxisAlignedBB(
                        bp.getX(),
                        bp.getY(),
                        bp.getZ(),
                        bp.getX() + 4.5,
                        bp.getY() + 4.5,
                        bp.getZ() + 4.5
                );
                List<EntityPlayer> entities = mc.theWorld.getEntitiesWithinAABB(
                        EntityPlayer.class,
                        bb
                );
                EntityPlayer breaker = getBreaker(entities);
                if (breaker == null) return;

                BlockBreakEvent blockBreakEvent = new BlockBreakEvent(
                        breaker,
                        bp,
                        state
                );
                EventBus.postEvent(blockBreakEvent);
            }
        }
    }

    private EntityPlayer getBreaker(List<EntityPlayer> candidates) {
        EntityPlayer candidate = null;
        candidates.sort(Comparator.comparingLong(obj -> {
            AnticheatData.Entry data = AnticheatData.getEntry(obj.getUniqueID());
            return System.currentTimeMillis() - data.lastSwingAtBlock;
        }));
        for (EntityPlayer player : candidates) {
            AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
            if (System.currentTimeMillis() - data.lastSwingAtBlock < 300) {
                candidate = player;
            }
        }

        return candidate;
    }
}
