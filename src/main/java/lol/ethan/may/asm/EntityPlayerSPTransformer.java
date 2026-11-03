package lol.ethan.may.asm;

import lol.ethan.may.events.ChatEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityPlayerSPTransformer extends Transformer {
    public EntityPlayerSPTransformer() {
        super(EntityPlayerSP.class);
    }

    @Override
    public void transform(final ClassNode node) {

        for (final MethodNode method : node.methods) {
            if (method.name.equals(mapMethod(EntityPlayerSP.class, "sendChatMessage", "()V"))) {
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(ChatEvent.Send.class));
                    a.dup();
                    a.load(1, Type.getType(String.class));
                    a.invokespecial(Type.getInternalName(ChatEvent.Send.class), "<init>",
                            "(Ljava/lang/String;)V", false);
                    a.dup();
                    postEvent(a);

                    ifEventCancel(a, i -> {
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
    public boolean shouldComputeFrames() {
        return true;
    }
}
