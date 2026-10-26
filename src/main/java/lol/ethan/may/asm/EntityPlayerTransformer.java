package lol.ethan.may.asm;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.player.NameFormatEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

import static lol.ethan.may.asm.ASMUtils.asm;

public class EntityPlayerTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (!method.name.equals("getDisplayName") && !method.name.equals("func_145748_c_")) continue;
            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (insnNode.getOpcode() < Opcodes.IRETURN || insnNode.getOpcode() > Opcodes.RETURN) continue;
                method.instructions.insertBefore(insnNode, asm(a -> {
                    a.load(0, Type.getType(EntityPlayer.class));
                    a.invokestatic(Type.getInternalName(EntityPlayerTransformer.class), "may$transformName",
                            "(" + Type.getInternalName(EntityPlayer.class) + ";L" + Type.getInternalName(IChatComponent.class) + ";)V", false);
                }));
            }
        }
    }

    public static IChatComponent may$transformName(EntityPlayer player, IChatComponent og) {
        NameFormatEvent event = new NameFormatEvent(player, og.getFormattedText());
        EventBus.postEvent(event);
        return event.getTextAsComponent();
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.entity.player.EntityPlayer");
    }
}
