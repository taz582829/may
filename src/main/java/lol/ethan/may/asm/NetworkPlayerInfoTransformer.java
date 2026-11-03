package lol.ethan.may.asm;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.player.PlayerPingEvent;
import lol.ethan.may.events.player.RenderPlayerInTabEvent;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class NetworkPlayerInfoTransformer extends Transformer {
    private final String formatNameDesc = "(Lnet/minecraft/util/IChatComponent;Lnet/minecraft/client/network/NetworkPlayerInfo;)L" + Type.getInternalName(IChatComponent.class) + ";";

    public NetworkPlayerInfoTransformer() {
        super(NetworkPlayerInfo.class);
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
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            for (final AbstractInsnNode insnNode : method.instructions) {
                if (insnNode instanceof FieldInsnNode) {
                    final FieldInsnNode fn = (FieldInsnNode) insnNode;
                    if ((fn.name.equals("displayName") || fn.name.equals("field_178872_h")) && fn.getOpcode() == Opcodes.GETFIELD) {
                        method.instructions.insert(insnNode, asm(a -> {
                            a.load(0, getType(NetworkPlayerInfo.class));
                            a.invokestatic(Type.getInternalName(NetworkPlayerInfoTransformer.class), "may$formatTabName",
                                    formatNameDesc, false);
                        }));
                        //method.instructions.remove(insnNode);
                    }
                }
            }
            if (method.name.equals("getResponseTime") || method.name.equals("func_178853_c")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode.getOpcode() < Opcodes.IRETURN || insnNode.getOpcode() > Opcodes.RETURN) continue;
                    method.instructions.insertBefore(insnNode, asm(a -> {
                        a.load(0, getType(NetworkPlayerInfo.class));
                        a.invokestatic(Type.getInternalName(NetworkPlayerInfoTransformer.class), "may$setPlayerPing", "(IL" + Type.getInternalName(NetworkPlayerInfo.class) + ";)I", false);
                    }));
                }
            }

        }
    }
}
