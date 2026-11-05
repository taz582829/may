package lol.ethan.may.asm;

import lol.ethan.may.events.player.NameFormatEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityPlayerTransformer extends Transformer {
    private static final Type CHAT_COMPONENT_TYPE = Type.getType(IChatComponent.class);
    private static final Type PLAYER_TYPE = Type.getType(EntityPlayer.class);
    private static final Type EVENT_TYPE = Type.getType(NameFormatEvent.class);
    public EntityPlayerTransformer() {
        super(EntityPlayer.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.name.equals("getDisplayName") && !method.name.equals("func_145748_c_")) continue;
            insertBeforeReturn(method, asm(a -> {
                final int tempSlot = method.maxLocals;
                method.maxLocals++;
                a.store(tempSlot, CHAT_COMPONENT_TYPE);
                a.anew(EVENT_TYPE);
                a.dup();
                a.load(tempSlot, CHAT_COMPONENT_TYPE);
                a.load(0, PLAYER_TYPE);
                a.invokespecial(EVENT_TYPE.getInternalName(), "<init>",
                        "(L" + CHAT_COMPONENT_TYPE.getInternalName() + ";L" + PLAYER_TYPE.getInternalName() + ";)V", false);
                postEvent(a);
                a.checkcast(EVENT_TYPE);
                a.invokevirtual(EVENT_TYPE.getInternalName(), "getTextAsComponent",
                        "()Lnet/minecraft/util/IChatComponent;", false);
            }));
        }
    }
}
