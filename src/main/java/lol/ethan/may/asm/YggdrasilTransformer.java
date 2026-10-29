package lol.ethan.may.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class YggdrasilTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("isWhitelistedDomain")) {
                for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode) {
                        MethodInsnNode mn = (MethodInsnNode) insnNode;
                        if (mn.getOpcode() == Opcodes.INVOKEVIRTUAL && mn.name.equals("endsWith") && mn.owner.equals("java/lang/String")) {
                            method.instructions.set(mn, new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    Type.getInternalName(YggdrasilTransformer.class),
                                    "may$urlEndsWith",
                                    "(Ljava/lang/String;Ljava/lang/String;)Z"
                            ));
                        }
                    }
                }
            }
        }
    }

    public static boolean may$urlEndsWith(String domain, String wl_url) {
        try {
            return domain != null && domain.endsWith(wl_url);
        } catch (RuntimeException e) {
            return false;
        }
    }
    @Override
    public List<String> getTargets() {
        return Collections.singletonList("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
    }
}
