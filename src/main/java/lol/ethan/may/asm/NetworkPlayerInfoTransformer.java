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
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.name.equals("getDisplayName") || method.name.equals("func_178854_k")) {
                for (final AbstractInsnNode insnNode : method.instructions) {
                    if (Opcodes.ARETURN != insnNode.getOpcode()) continue;
                    final InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(NetworkPlayerInfoTransformer.class), "may$formatTabName", formatNameDesc));
                    method.instructions.insertBefore(insnNode, insnList);
                }
            }
            if (!method.name.equals("func_178853_c") && !method.name.equals("getResponseTime")) continue;
            for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (Opcodes.IRETURN > insnNode.getOpcode() || Opcodes.RETURN < insnNode.getOpcode()) continue;
                method.instructions.insertBefore(insnNode, ASMUtils.asm(a -> {
                    a.load(0, ASMUtils.getType(NetworkPlayerInfo.class));
                    a.invokestatic(Type.getInternalName(NetworkPlayerInfoTransformer.class), "may$setPlayerPing", "(IL" + Type.getInternalName(NetworkPlayerInfo.class) + ";)I", false);
                }));
            }
        }
    }

    public static IChatComponent may$formatTabName(final IChatComponent displayName, final NetworkPlayerInfo playerInfo) {
        final RenderPlayerInTabEvent event = EventBus.postEvent(new RenderPlayerInTabEvent(displayName, playerInfo));
        return event.getTextAsComponent();
    }

    public static int may$setPlayerPing(final int ping, final NetworkPlayerInfo playerInfo) {
        final PlayerPingEvent event = EventBus.postEvent(new PlayerPingEvent(ping, playerInfo));
        return event.getPing();
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.network.NetworkPlayerInfo");
    }
}
