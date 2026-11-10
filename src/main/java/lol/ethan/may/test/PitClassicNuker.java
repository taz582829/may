package lol.ethan.may.test;

import lol.ethan.may.Entry;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import lol.ethan.may.utils.GuiUtils;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PitClassicNuker {
    public static boolean enabled;
    Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(final TickEvent.Post event) {
        if (enabled && Entry.getServerIP().contains("pitclassic")) {
            final BlockPos center = mc.thePlayer.getPosition();
            final double radius = 4.5;
            final int r = 5;

            final BlockPos min = center.add(-r, -r, -r);
            final BlockPos max = center.add(r, r, r);

            final double radiusSq = radius * radius;

            for (final BlockPos pos : BlockPos.getAllInBox(min, max)) {
                if (center.distanceSq(pos) <= radiusSq) {
                    final IBlockState state = mc.theWorld.getBlockState(pos);
                    if (state.getBlock() == Blocks.wheat && state.getValue(BlockCrops.AGE) == 7) {
                        final boolean swing = mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
                        if (swing) mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    } else if (state.getBlock() == Blocks.furnace) {
                        final boolean hasWheat = GuiUtils.searchInventory(i -> i != null && i.getItem() == Items.wheat && i.stackSize == 64) != -1;
                        if (hasWheat) {
                            final boolean swing = mc.playerController.onPlayerRightClick(
                                    mc.thePlayer,
                                    mc.theWorld,
                                    mc.thePlayer.getHeldItem(),
                                    pos,
                                    EnumFacing.UP,
                                    mc.thePlayer.getLookVec()
                            );
                            if (swing) mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onChat(final ChatEvent.Send event) {
        if ("!nuke".equals(event.getMessage())) {
            event.setCancelled(true);
            enabled = !enabled;
            Entry.chat(enabled);
        }
    }
}
