package lol.ethan.may.asm;

import lol.ethan.may.events.ChatEvent;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

import static lol.ethan.may.asm.ASMUtils.*;

public class GuiNewChatTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("printChatMessageWithOptionalDeletion")) {
                int eventSlot = method.maxLocals;
                method.maxLocals++;
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(ChatEvent.Received.class));
                    a.dup();
                    a.load(1, Type.getType(IChatComponent.class));
                    a.invokespecial(Type.getInternalName(ChatEvent.Received.class), "<init>",
                            "(Lnet/minecraft/util/IChatComponent;)V", false);
                    a.store(eventSlot, Type.getType(ChatEvent.Received.class));
                    postEvent(a);
                    returnIfEventCancelled(a);
                    a.load(eventSlot, Type.getType(ChatEvent.Received.class));
                    a.invokevirtual(Type.getInternalName(ChatEvent.Received.class), "getMessage",
                            "()Lnet/minecraft/util/IChatComponent;", false);
                    a.store(1, Type.getType(IChatComponent.class));
                }));
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.gui.GuiNewChat");
    }
}
