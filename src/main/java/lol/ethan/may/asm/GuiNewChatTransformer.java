package lol.ethan.may.asm;

import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.Event;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiNewChatTransformer extends Transformer {
    public GuiNewChatTransformer() {
        super(GuiNewChat.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.name.equals("printChatMessageWithOptionalDeletion") || method.name.equals("func_146234_a")) {
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(ChatEvent.Received.class));
                    a.dup();
                    a.load(1, Type.getType(IChatComponent.class));
                    a.invokespecial(Type.getInternalName(ChatEvent.Received.class), "<init>",
                            "(Lnet/minecraft/util/IChatComponent;)V", false);
                    a.dup();
                    postEvent(a);

                    final Label end = new Label();
                    a.invokevirtual(Type.getInternalName(Event.class), "isCancelled", "()Z", false);
                    a.ifeq(end);
                    a.pop();
                    a.areturn(Type.VOID_TYPE);
                    a.mark(end);

                    a.invokevirtual(Type.getInternalName(ChatEvent.Received.class), "getMessage",
                            "()Lnet/minecraft/util/IChatComponent;", false);
                    a.store(1, Type.getType(IChatComponent.class));
                }));
            }
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
