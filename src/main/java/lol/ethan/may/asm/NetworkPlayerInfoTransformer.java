package lol.ethan.may.asm;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.player.PlayerPingEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;

public class NetworkPlayerInfoTransformer implements ITransformer {
    private final String formatNameDesc = "(Lnet/minecraft/util/IChatComponent;Lnet/minecraft/client/network/NetworkPlayerInfo;)L" + Type.getInternalName(IChatComponent.class) + ";";

    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("getDisplayName") || method.name.equals("func_178854_k")) {
                for (AbstractInsnNode insnNode : method.instructions) {
                    if (insnNode.getOpcode() != Opcodes.ARETURN) continue;
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(NetworkPlayerInfoTransformer.class), "may$formatTabName", this.formatNameDesc));
                    method.instructions.insertBefore(insnNode, insnList);
                }
            }
            if (!method.name.equals("func_178853_c") && !method.name.equals("getResponseTime")) continue;
            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (insnNode.getOpcode() < Opcodes.IRETURN || insnNode.getOpcode() > Opcodes.RETURN) continue;
                method.instructions.insertBefore(insnNode, ASMUtils.asm(a -> {
                    a.load(0, ASMUtils.getType(NetworkPlayerInfo.class));
                    a.invokestatic(Type.getInternalName(NetworkPlayerInfoTransformer.class), "may$setPlayerPing", "(IL" + Type.getInternalName(NetworkPlayerInfo.class) + ";)I", false);
                }));
            }
        }
    }

    public static IChatComponent may$formatTabName(IChatComponent displayName, NetworkPlayerInfo playerInfo) {
        RenderPlayerInTabEvent event = EventBus.postEvent(new RenderPlayerInTabEvent(displayName, playerInfo));
        return event.getTextAsComponent();
    }

    public static int may$setPlayerPing(int ping, NetworkPlayerInfo playerInfo) {
        PlayerPingEvent event = EventBus.postEvent(new PlayerPingEvent(ping, playerInfo));
        return event.getPing();
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.network.NetworkPlayerInfo");
    }
}
