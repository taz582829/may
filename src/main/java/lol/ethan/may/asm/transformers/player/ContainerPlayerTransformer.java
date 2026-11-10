package lol.ethan.may.asm.transformers.player;

import lol.ethan.may.asm.Transformer;
import lol.ethan.may.housing.HousingUtils;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ContainerPlayerTransformer extends Transformer {
    public ContainerPlayerTransformer() {
        super("net.minecraft.inventory.ContainerPlayer$1");
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if ("isItemValid".equals(method.name)) {
                method.instructions.insert(asm(a -> {
                    final Label skip = new Label();
                    a.load(1, Type.getType(ItemStack.class));
                    a.ifnull(skip);

                    a.invokestatic(HousingUtils.class, "isInCreative", "()Z");
                    a.ifeq(skip);
                    a.iconst(1);
                    a.areturn(Type.BOOLEAN_TYPE);

                    a.mark(skip);
                }));
            }
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
