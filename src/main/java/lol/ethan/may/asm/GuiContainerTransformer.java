package lol.ethan.may.asm;

import lol.ethan.may.events.SlotClickEvent;
import net.minecraft.inventory.Slot;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class GuiContainerTransformer implements ITransformer {
    private static final String SLOT_CLICK_EVENT = Type.getInternalName(SlotClickEvent.class);
    private static final Type SLOT_CLICK_TYPE = Type.getType(SlotClickEvent.class);

    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (!method.name.equals("handleMouseClick")) continue;
            int eventLocal = method.maxLocals;
            method.maxLocals++;
            method.instructions.insert(ASMUtils.asm(a -> {
                a.anew(SLOT_CLICK_TYPE);
                a.dup();
                a.load(1, Type.getType(Slot.class));
                a.load(3, Type.INT_TYPE);
                a.load(4, Type.INT_TYPE);
                a.invokespecial(SLOT_CLICK_EVENT, "<init>", "(Lnet/minecraft/inventory/Slot;II)V", false);
                a.store(eventLocal, SLOT_CLICK_TYPE);
                ASMUtils.postEvent(a);
                ASMUtils.returnIfEventCancelled(a);
                a.load(eventLocal, SLOT_CLICK_TYPE);
                a.invokevirtual(SLOT_CLICK_EVENT, "getClickedButton", "()I", false);
                a.store(3, Type.INT_TYPE);
                a.load(eventLocal, SLOT_CLICK_TYPE);
                a.invokevirtual(SLOT_CLICK_EVENT, "getClickType", "()I", false);
                a.store(4, Type.INT_TYPE);
            }));
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.gui.GuiContainer");
    }
}
