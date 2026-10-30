package lol.ethan.may.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class SkinManagerTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("loadSkinFromCache")) {
                for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode) {
                        MethodInsnNode mn = (MethodInsnNode) insnNode;
                        if (mn.name.equals("getUnchecked")) {
                            mn.name = "get";
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.resources.SkinManager");
    }
}
