package lol.ethan.may.asm.transformers.player;

import lol.ethan.may.asm.Transformer;
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
            if (!"handlePlayerListItem".equals(method.name) && !"func_147256_a".equals(method.name)) continue;
            for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                final MethodInsnNode mn = (MethodInsnNode) insnNode;
                if ("put".equals(mn.name)) {
                    method.instructions.insert(mn, asm(a -> {
                        a.pop();
                        a.anew(Type.getType(PlayerListEvent.Add.class));
                        a.dup();
                        a.load(3, Type.getType(S38PacketPlayerListItem.AddPlayerData.class));
                        a.invokespecial(Type.getInternalName(PlayerListEvent.Add.class), "<init>", "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V", false);
                        a.postEvent();
                    }));
                }
                if (!"remove".equals(mn.name)) continue;
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
