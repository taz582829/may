package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ModTransformer
implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                MethodInsnNode methodInsn = (MethodInsnNode)insnNode;
                if (!"addProperty".equals(methodInsn.name)) continue;
                System.out.println("patching freelook");
                boolean foundAllowed = false;
                AbstractInsnNode ldcNode = null;
                for (AbstractInsnNode cursor = methodInsn.getPrevious(); cursor != null; cursor = cursor.getPrevious()) {
                    if (!(cursor instanceof LdcInsnNode) || !"allowed".equals(((LdcInsnNode)cursor).cst)) continue;
                    foundAllowed = true;
                    ldcNode = cursor;
                    break;
                }
                if (!foundAllowed) continue;
                System.out.println("Successfully patched 'allowed' property in class: " + node.name);
                InsnList replacement = new InsnList();
                replacement.add(new InsnNode(Opcodes.ICONST_1));
                replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false));
                AbstractInsnNode startRemove = ldcNode.getNext();
                method.instructions.insertBefore(methodInsn, replacement);
                AbstractInsnNode current = startRemove;
                while (current != methodInsn) {
                    AbstractInsnNode next = current.getNext();
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
    public boolean shouldTransform(String className) {
        return className.contains("moonsworth");
    }
}
