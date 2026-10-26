package lol.ethan.may.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class FontRendererTransformer implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.name.equals("func_78255_a") && !method.name.equals("renderStringAtPos")) continue;
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.gui.FontRenderer");
    }
}
