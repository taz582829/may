package lol.ethan.may.asm;

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
            if (method.name.equals("isItemValid")) {
                method.instructions.clear();
                method.instructions.add(ASMUtils.asm(a -> {
                    a.invokestatic(
                            Type.getInternalName(ContainerPlayerTransformer.class),
                            "may$isItemValid",
                            "(Lnet/minecraft/item/ItemStack;)Z",
                            false
                    );
                    a.areturn(Type.BOOLEAN_TYPE);
                }));
            }
        }
    }
}
