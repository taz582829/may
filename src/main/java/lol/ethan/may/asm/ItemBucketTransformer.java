package lol.ethan.may.asm;

import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class ItemBucketTransformer implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.name.equals("func_180616_a") && !method.name.equals("tryPlaceContainedLiquid")) continue;
            for (final AbstractInsnNode insn : method.instructions.toArray()) {
                if (!(insn instanceof MethodInsnNode)) continue;
                final MethodInsnNode methodInsn = (MethodInsnNode)insn;
                if (!methodInsn.name.equals("func_180501_a") && !methodInsn.name.equals("setBlockState")) continue;
                method.instructions.insert(methodInsn, ASMUtils.asm(a -> {
                    final Label skip = new Label();
                    a.invokestatic("net/minecraft/client/Minecraft", "getMinecraft", "()Lnet/minecraft/client/Minecraft;", false);
                    a.invokevirtual("net/minecraft/client/Minecraft", "isSingleplayer", "()Z", false);
                    a.ifeq(skip);
                    a.load(1, Type.getType(BlockPos.class));
                    a.load(2, Type.getType(BlockState.class));
                    a.load(0, Type.getType(ItemBucket.class));
                    a.iconst(3);
                    a.getfield("net/minecraft/item/ItemBucket", "isFull", "Lnet/minecraft/block/Block;");
                    a.invokevirtual("net/minecraft/block/Block", "getDefaultState", "()Lnet/minecraft/block/state/IBlockState;", false);
                    a.invokevirtual("net/minecraft/world/World", "setBlockState", "(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", false);
                    a.pop();
                    a.mark(skip);
                }));
                method.instructions.remove(methodInsn);
            }
        }
    }

    public static boolean may$tryPlaceContainedLiquid(final ItemBucket bucket, final World world, final BlockPos bp) {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            return bucket.tryPlaceContainedLiquid(world, bp);
        }
        return false;
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.item.ItemBucket");
    }
}
