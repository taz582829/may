package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ITransformer;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.player.NameFormatEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;

public class EntityPlayerTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (!method.name.equals("getDisplayName") && !method.name.equals("func_145748_c_")) continue;
            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (insnNode.getOpcode() < Opcodes.IRETURN || insnNode.getOpcode() > Opcodes.RETURN) continue;
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insnList.add(new InsnNode(Opcodes.SWAP));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityPlayerTransformer.class), "may$transformName", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/IChatComponent;)Lnet/minecraft/util/IChatComponent;"));
                method.instructions.insertBefore(insnNode, insnList);
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
