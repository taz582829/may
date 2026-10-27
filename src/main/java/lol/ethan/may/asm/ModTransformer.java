package lol.ethan.may.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;

public class ModTransformer implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                final MethodInsnNode methodInsn = (MethodInsnNode) insnNode;
                if (!"addProperty".equals(methodInsn.name)) continue;
                System.out.println("patching freelook");
                boolean foundAllowed = false;
                AbstractInsnNode ldcNode = null;
                for (AbstractInsnNode cursor = methodInsn.getPrevious(); cursor != null; cursor = cursor.getPrevious()) {
                    if (!(cursor instanceof LdcInsnNode) || !"allowed".equals(((LdcInsnNode) cursor).cst)) continue;
                    foundAllowed = true;
                    ldcNode = cursor;
                    break;
                }
                if (!foundAllowed) continue;
                System.out.println("Successfully patched 'allowed' property in class: " + node.name);
                final InsnList replacement = new InsnList();
                replacement.add(new InsnNode(Opcodes.ICONST_1));
                replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
                final AbstractInsnNode startRemove = ldcNode.getNext();
                method.instructions.insertBefore(methodInsn, replacement);
                AbstractInsnNode current = startRemove;
                while (current != methodInsn) {
                    final AbstractInsnNode next = current.getNext();
                    method.instructions.remove(current);
                    current = next;
                }
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.emptyList();
    }

    @Override
    public boolean shouldTransform(final String className) {
        return className.contains("moonsworth");
    }
}
