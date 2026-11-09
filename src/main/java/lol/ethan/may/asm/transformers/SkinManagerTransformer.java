package lol.ethan.may.asm.transformers;

import lol.ethan.may.asm.Transformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SkinManagerTransformer extends Transformer {
    public SkinManagerTransformer() {
        super(SkinManagerTransformer.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.name.equals("loadSkinFromCache") || method.name.equals("func_152788_a")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode) {
                        final MethodInsnNode mn = (MethodInsnNode) insnNode;
                        if (mn.name.equals("getUnchecked")) {
                            mn.name = "get";
                        }
                    }
                }
            }
        }
    }
}
