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
    public void onPacketReceived(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof S23PacketBlockChange) {
            final S23PacketBlockChange packet = (S23PacketBlockChange) event.getPacket();
            final IBlockState state = packet.blockState;
            final BlockPos bp = packet.getBlockPosition();
            if (state.getBlock() == null || state.getBlock().getMaterial() == Material.air) {
                final AxisAlignedBB bb = new AxisAlignedBB(
                        bp.getX(),
                        bp.getY(),
                        bp.getZ(),
                        bp.getX() + 4.5,
                        bp.getY() + 4.5,
                        bp.getZ() + 4.5
                );
                final List<EntityPlayer> entities = mc.theWorld.getEntitiesWithinAABB(
                        EntityPlayer.class,
                        bb
                );
                final EntityPlayer breaker = getBreaker(entities);
                if (breaker == null) return;

                final BlockBreakEvent blockBreakEvent = new BlockBreakEvent(
                        breaker,
                        bp,
                        state
                );
                EventBus.postEvent(blockBreakEvent);
            }
        }
    }

    private EntityPlayer getBreaker(final List<EntityPlayer> candidates) {
        EntityPlayer candidate = null;
        candidates.sort(Comparator.comparingLong(obj -> {
            final AnticheatData.Entry data = AnticheatData.getEntry(obj.getUniqueID());
            return System.currentTimeMillis() - data.lastSwingAtBlock;
        }));
        for (final EntityPlayer player : candidates) {
            final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
            if (System.currentTimeMillis() - data.lastSwingAtBlock < 300) {
                candidate = player;
            }
        }

        return candidate;
    }
}
