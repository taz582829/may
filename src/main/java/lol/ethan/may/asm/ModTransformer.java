package lol.ethan.may.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

public class ModTransformer extends Transformer {
    private static String abstractFeatureName = null;
    public ModTransformer() {
        super("com.moonsworth.lunar.client.*");
    }

    @Override
    public void transform(final ClassNode node) {
        if ((node.access & ACC_INTERFACE) == 0) return;
        for (final MethodNode method : node.methods) {
            if (method.desc.equals("()Ljava/util/Optional;")) {
                abstractFeatureName = node.name;
                method.instructions.clear();
                method.instructions.add(asm(a -> {
                    a.invokestatic("java/util/Optional", "empty", "()Ljava/util/Optional;", false);
                    a.areturn(Type.getType(Optional.class));
                }));
            }

        }
    }

    @Override
    public boolean shouldSave(final String className) {
        return className.equals(abstractFeatureName.replace("/", "."));
    }
}
