package lol.ethan.may.asm;

import lol.ethan.may.events.player.PlayerListEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class NetHandlerPlayClientTransformer extends Transformer {
    public NetHandlerPlayClientTransformer() {
        super(NetHandlerPlayClient.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.name.equals("handlePlayerListItem") && !method.name.equals("func_147256_a")) continue;
            for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                final MethodInsnNode mn = (MethodInsnNode) insnNode;
                if (mn.name.equals("put")) {
                    method.instructions.insert(mn, asm(a -> {
                        a.pop();
                        a.anew(Type.getType(PlayerListEvent.Add.class));
                        a.dup();
                        a.load(3, Type.getType(S38PacketPlayerListItem.AddPlayerData.class));
                        a.invokespecial(Type.getInternalName(PlayerListEvent.Add.class), "<init>", "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V", false);
                        a.postEvent();
                    }));
                }
                if (!mn.name.equals("remove")) continue;
                method.instructions.insert(mn, asm(a -> {
                    a.pop();
                    a.anew(Type.getType(PlayerListEvent.Remove.class));
                    a.dup();
                    a.load(3, Type.getType(S38PacketPlayerListItem.AddPlayerData.class));
                    a.invokespecial(Type.getInternalName(PlayerListEvent.Remove.class), "<init>", "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V", false);
                    a.postEvent();
                }));
            }
        }
    }
}
