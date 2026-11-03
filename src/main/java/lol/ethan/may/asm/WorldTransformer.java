package lol.ethan.may.asm;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.WorldEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class WorldTransformer extends Transformer {
    public WorldTransformer() {
        super(Minecraft.class);
    }

    public static void onWorldLoad(final WorldClient world) {
        if (world == null) {
            EventBus.postEvent(new WorldEvent.Unload());
        } else {
            EventBus.postEvent(new WorldEvent.Load());
        }
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.desc.equals("(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")) continue;
            final InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(WorldTransformer.class), "onWorldLoad", "(L" + Type.getInternalName(WorldClient.class) + ";)V"));
            method.instructions.insert(insnList);
        }
    }
}
