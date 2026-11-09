package lol.ethan.may.events.player;

import lol.ethan.may.events.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class BlockBreakEvent extends Event {
    private final EntityPlayer player;
    private final BlockPos blockPos;
    private final IBlockState state;

    public BlockBreakEvent(final EntityPlayer player, final BlockPos blockPos, final IBlockState state) {
        this.player = player;
        this.blockPos = blockPos;
        this.state = state;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public IBlockState getState() {
        return state;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}
