package lol.ethan.may.asm;

import net.minecraft.inventory.ContainerPlayer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class ContainerPlayerTransformer implements ITransformer {

    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("isItemValid")) {
                method.instructions.clear();
                method.instructions.add(ASMUtils.asm(a -> {
                    a.iconst(1);
                    a.areturn(Type.BOOLEAN_TYPE);
                }));
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.inventory.ContainerPlayer$1");
    }
}
