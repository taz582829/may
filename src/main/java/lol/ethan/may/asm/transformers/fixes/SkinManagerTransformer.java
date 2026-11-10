package lol.ethan.may.asm.transformers.fixes;

import lol.ethan.may.asm.Transformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

// fixes a game crash by not letting the game render invalid heads
public class SkinManagerTransformer extends Transformer {
    public SkinManagerTransformer() {
        super(SkinManagerTransformer.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if ("loadSkinFromCache".equals(method.name) || "func_152788_a".equals(method.name)) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode) {
                        final MethodInsnNode mn = (MethodInsnNode) insnNode;
                        if ("getUnchecked".equals(mn.name)) {
                            mn.name = "get";
                        }
                    }
                }
            }
        }
    }
}
