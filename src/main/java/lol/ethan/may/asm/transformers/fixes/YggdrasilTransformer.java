package lol.ethan.may.asm.transformers.fixes;

import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import lol.ethan.may.asm.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

// fixes a game crash by not letting the game render invalid skins
public class YggdrasilTransformer extends Transformer {
    public YggdrasilTransformer() {
        super(YggdrasilMinecraftSessionService.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if ("isWhitelistedDomain".equals(method.name)) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode instanceof MethodInsnNode) {
                        final MethodInsnNode mn = (MethodInsnNode) insnNode;
                        if (mn.getOpcode() == Opcodes.INVOKEVIRTUAL && "endsWith".equals(mn.name) && "java/lang/String".equals(mn.owner)) {
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

    public static boolean may$urlEndsWith(final String domain, final String wl_url) {
        try {
            return domain != null && domain.endsWith(wl_url);
        } catch (final RuntimeException e) {
            return false;
        }
    }
}
