package lol.ethan.may.asm;

import lol.ethan.may.mappings.Mappings;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;

public class Remapper implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        if (Mappings.getMappingType() != Mappings.MappingType.MCP) {
            return;
        }
        System.out.println("remapping " + node.name);
        for (final MethodNode method : node.methods) {
            for (final AbstractInsnNode insnNode : method.instructions) {
                if (insnNode instanceof MethodInsnNode) {
                    final MethodInsnNode insn = (MethodInsnNode) insnNode;
                    if (!insn.owner.contains("net/minecraft") && !insn.owner.contains("com/mojang")) continue;
                    insn.name = Mappings.mapMethodInHierarchy(insn.owner, insn.name, insn.desc);
                    continue;
                }
                if (insnNode instanceof FieldInsnNode) {
                    final FieldInsnNode f = (FieldInsnNode) insnNode;
                    if (!f.owner.contains("net/minecraft") && !f.owner.contains("com/mojang")) continue;
                    f.name = Mappings.mapFieldInHierarchy(f.owner, f.name, f.name);
                    continue;
                }
                if (!(insnNode instanceof InvokeDynamicInsnNode)) continue;
                final InvokeDynamicInsnNode dynamic = (InvokeDynamicInsnNode) insnNode;
                for (int i = 0; i < dynamic.bsmArgs.length; i++) {
                    if (!(dynamic.bsmArgs[i] instanceof Handle)) continue;
                    final Handle h = (Handle) dynamic.bsmArgs[i];
                    if (!h.getOwner().contains("net/minecraft") && !h.getOwner().contains("com/mojang")) continue;
                    final String name = h.getTag() <= 4 ? Mappings.mapFieldInHierarchy(h.getOwner(), h.getName(), h.getName()) : Mappings.mapMethodInHierarchy(h.getOwner(), h.getName(), h.getDesc(), h.getName());
                    dynamic.bsmArgs[i] = new Handle(h.getTag(), h.getOwner(), name, h.getDesc(), h.isInterface());
                }
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.emptyList();
    }

    @Override
    public boolean shouldTransform(final String name) {
        return name.startsWith("lol.ethan.may.") && !name.startsWith("lol.ethan.may.mappings.") && !name.startsWith("lol.ethan.may.shaded") && !name.equals("lol.ethan.may.Main");
    }
}
