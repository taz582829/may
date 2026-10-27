package lol.ethan.may.asm;

import lol.ethan.may.events.ChatEvent;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class EntityPlayerSPTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("sendChatMessage")) {
                method.instructions.insert(ASMUtils.asm(a -> {
                    a.anew(Type.getType(ChatEvent.Send.class));
                    a.dup();
                    a.load(1, Type.getType(String.class));
                    a.invokespecial(Type.getInternalName(ChatEvent.Send.class), "<init>",
                            "(Ljava/lang/String;)V", false);
                    a.dup();
                    ASMUtils.postEvent(a);

                    ASMUtils.ifEventCancel(a, i -> {
                        i.pop();
                        i.areturn(Type.VOID_TYPE);
                    });
                    a.invokevirtual(Type.getInternalName(ChatEvent.Send.class), "getMessage",
                            "()Ljava/lang/String;", false);
                    a.store(1, Type.getType(String.class));
                }));
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.entity.EntityPlayerSP");
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
