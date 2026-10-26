package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ITransformer;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.WorldEvent;
import lol.ethan.may.mappings.Mappings;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldTransformer implements ITransformer {
    Mappings.MappingType mappings = Mappings.getMappingType();

    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (!method.desc.equals("(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")) continue;
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(WorldTransformer.class), "onWorldLoad", "(L" + Type.getInternalName(WorldClient.class) + ";)V"));
            method.instructions.insert(insnList);
        }
    }

    public static void onWorldLoad(WorldClient world) {
        if (world == null) {
            EventBus.postEvent(new WorldEvent.Unload());
        } else {
            EventBus.postEvent(new WorldEvent.Load());
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.Minecraft");
    }
}
