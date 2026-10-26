package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ITransformer;
import lol.ethan.may.mappings.Mappings;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Remapper
implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        if (Mappings.getMappingType() != Mappings.MappingType.MCP) {
            return;
        }
        System.out.println("remapping...");
        for (MethodNode method : node.methods) {
            for (AbstractInsnNode insnNode : method.instructions) {
                if (insnNode instanceof MethodInsnNode) {
                    MethodInsnNode insn = (MethodInsnNode)insnNode;
                    insn.name = Mappings.getMethod(insn.owner, insn.name, insn.desc);
                    continue;
                }
                if (insnNode instanceof FieldInsnNode) {
                    FieldInsnNode f = (FieldInsnNode)insnNode;
                    f.name = Mappings.mapFieldInHierarchy(f.owner, f.name, f.name);
                    continue;
                }
                if (!(insnNode instanceof InvokeDynamicInsnNode)) continue;
                InvokeDynamicInsnNode dynamic = (InvokeDynamicInsnNode)insnNode;
                for (int i = 0; i < dynamic.bsmArgs.length; ++i) {
                    if (!(dynamic.bsmArgs[i] instanceof Handle)) continue;
                    Handle h = (Handle)dynamic.bsmArgs[i];
                    String name = h.getTag() <= 4 ? Mappings.mapFieldInHierarchy(h.getOwner(), h.getName(), h.getName()) : Mappings.mapMethodInHierarchy(h.getOwner(), h.getName(), h.getDesc(), h.getName());
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
    public boolean shouldTransform(String name) {
        return name.startsWith("lol.ethan.may.") && !name.startsWith("lol.ethan.may.mappings.") && !name.startsWith("lol.ethan.may.shaded") && !name.equals("lol.ethan.may.Main");
    }
}
