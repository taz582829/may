package lol.ethan.may.asm;

import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ModTransformer implements ITransformer {
    public static String method7Name;
    public static String alert2iteratorName;
    private static boolean isFreelook(ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.desc.equals("()Ljava/lang/String;") || (method.access & ACC_STATIC) != 0) continue;
            AbstractInsnNode[] insns = Arrays.stream(method.instructions.toArray())
                    .filter(i -> i.getOpcode() > 0)
                    .toArray(AbstractInsnNode[]::new);

            if (insns.length == 2
                    && insns[0] instanceof LdcInsnNode && "FREELOOK".equals(((LdcInsnNode) insns[0]).cst)
                    && insns[1].getOpcode() == ARETURN) return true;
        }
        return false;
    }
    @Override
    public void transform(final ClassNode node) {
        System.out.println("freelook extends " + node.superName);
        //if (!isFreelook(node)) return;
        System.out.println(node.name + " is freelook");
        for (MethodNode m : node.methods) {
            if (!m.desc.equals("()Lcom/google/gson/JsonElement;") && (m.access & ACC_PUBLIC) == 0) continue;
            for (AbstractInsnNode in : m.instructions) {
                if (!(in instanceof MethodInsnNode)) continue;
                MethodInsnNode mn = (MethodInsnNode) in;
                if (mn.name.equals("valueOf") && mn.owner.equals("java/lang/Boolean")) {
                    InsnList insnList = new InsnList();
                    insnList.add(new InsnNode(POP));
                    insnList.add(new InsnNode(ICONST_0));
                    m.instructions.insertBefore(in, insnList);
                }
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.emptyList();
    }

    @Override
    public boolean shouldSave(String className) {
        return shouldTransform(className);
    }

    @Override
    public boolean shouldTransform(final String className) {
        return className.startsWith("com.moonsworth.lunar.client");
    }
}
