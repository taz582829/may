package lol.ethan.may.asm.transformers;

import lol.ethan.may.Entry;
import lol.ethan.may.asm.Transformer;
import lol.ethan.may.events.Event;
import lol.ethan.may.events.gui.SlotClickEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.inventory.Slot;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiContainerTransformer extends Transformer {
    private static final String SLOT_CLICK_EVENT = Type.getInternalName(SlotClickEvent.class);
    private static final Type SLOT_CLICK_TYPE = Type.getType(SlotClickEvent.class);

    public GuiContainerTransformer() {
        super(GuiContainer.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (insnNode instanceof MethodInsnNode) {
                    final MethodInsnNode mn = (MethodInsnNode) insnNode;
                    if (mn.owner.equals(Type.getInternalName(PlayerControllerMP.class)) && (mn.name.equals("func_78753_a") || mn.name.equals("windowClick"))) {
                        final int eventLocal = method.maxLocals;
                        method.maxLocals++;

                        method.instructions.insert(asm(a -> {
                            a.visitLdcInsn("slot clicked");
                            a.invokestatic(
                                    Type.getInternalName(Entry.class),
                                    "debug",
                                    "(Ljava/lang/String;)V",
                                    false
                            );
                            a.anew(SLOT_CLICK_TYPE);
                            a.dup();
                            a.load(1, Type.getType(Slot.class));
                            a.load(3, Type.INT_TYPE);
                            a.load(4, Type.INT_TYPE);
                            a.invokespecial(SLOT_CLICK_EVENT, "<init>", "(Lnet/minecraft/inventory/Slot;II)V", false);

                            a.dup();
                            a.postEvent();
                            a.checkcast(SLOT_CLICK_TYPE);
                            a.store(eventLocal, SLOT_CLICK_TYPE);

                            final Label end = new Label();
                            a.load(eventLocal, SLOT_CLICK_TYPE);
                            a.invokevirtual(Type.getInternalName(Event.class), "isCancelled", "()Z", false);
                            a.ifeq(end);
                            a.areturn(Type.VOID_TYPE);
                            a.mark(end);

                            a.load(eventLocal, SLOT_CLICK_TYPE);
                            a.invokevirtual(SLOT_CLICK_EVENT, "getClickedButton", "()I", false);
                            a.store(3, Type.INT_TYPE);
                            a.load(eventLocal, SLOT_CLICK_TYPE);
                            a.invokevirtual(SLOT_CLICK_EVENT, "getClickType", "()I", false);
                            a.store(4, Type.INT_TYPE);
                        }));
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
