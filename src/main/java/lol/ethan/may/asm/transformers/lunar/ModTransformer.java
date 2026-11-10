package lol.ethan.may.asm.transformers.lunar;

import lol.ethan.may.asm.Transformer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ACC_INTERFACE;

public class ModTransformer extends Transformer {
    private static final Set<String> modCandidates = new HashSet<>();
    public ModTransformer() {
        super("com.moonsworth.lunar.client.*");
    }

    @Override
    public void transform(final ClassNode node) {
        if ((node.access & ACC_INTERFACE) != 0) return;
        boolean hasPredicateMethod = false;
        boolean hasOptionalMethod = false;

        for (final MethodNode method : node.methods) {
            if (method.name.equals("getId")) {
                System.out.println("found " + findModId(node));
            }
            if (method.desc.startsWith("(Ljava/util/function/Predicate;")) {
                hasPredicateMethod = true;
            }
            if ("()Ljava/util/Optional;".equals(method.desc)) {
                hasOptionalMethod = true;
            }
        }
        boolean modified = false;
        if (hasPredicateMethod && hasOptionalMethod) {
            for (final MethodNode method : node.methods) {
                if ("()Ljava/util/Optional;".equals(method.desc)) {
                    method.instructions.clear();
                    if (method.tryCatchBlocks != null) method.tryCatchBlocks.clear();
                    if (method.localVariables != null) method.localVariables.clear();
                    method.attrs = null;

                    method.instructions.add(asm(a -> {
                        a.invokestatic("java/util/Optional", "empty", "()Ljava/util/Optional;", false);
                        a.areturn(Type.getType(Optional.class));
                    }));
                    modified = true;
                }
            }

            if (modified) {
                final String className = node.name.replace("/", ".");
                System.out.println("patched (" + className + ")");
                modCandidates.add(className);
            }
        }
    }
    private static String findModId(final ClassNode node) {
        for (final MethodNode m : node.methods) {
            if (!"getId".equals(m.name)) continue;

            for (final AbstractInsnNode insn : m.instructions) {
                if (insn instanceof LdcInsnNode) {
                    final LdcInsnNode ldc = (LdcInsnNode) insn;
                    return (String) ldc.cst;
                }
            }
        }
        return null;
    }

    @Override
    public boolean shouldSave(final String className) {
        return modCandidates.contains(className);
    }
}
