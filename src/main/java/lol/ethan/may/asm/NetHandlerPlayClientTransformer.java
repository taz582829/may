package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ASMUtils;
import lol.ethan.may.asm.ITransformer;
import lol.ethan.may.events.PlayerListEvent;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

public class NetHandlerPlayClientTransformer
implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (!method.name.equals("handlePlayerListItem") && !method.name.equals("func_147256_a")) continue;
            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                MethodInsnNode mn = (MethodInsnNode)insnNode;
                if (mn.name.equals("put")) {
                    method.instructions.insert(mn, ASMUtils.asm(a -> {
                        a.pop();
                        a.anew(ASMUtils.getType(PlayerListEvent.Add.class));
                        a.dup();
                        a.load(3, ASMUtils.getType(S38PacketPlayerListItem.AddPlayerData.class));
                        a.invokespecial(Type.getInternalName(PlayerListEvent.Add.class), "<init>", "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V", false);
                        ASMUtils.postEvent(a);
                    }));
                }
                if (!mn.name.equals("remove")) continue;
                method.instructions.insert(mn, ASMUtils.asm(a -> {
                    a.pop();
                    a.anew(ASMUtils.getType(PlayerListEvent.Remove.class));
                    a.dup();
                    a.load(3, ASMUtils.getType(S38PacketPlayerListItem.AddPlayerData.class));
                    a.invokespecial(Type.getInternalName(PlayerListEvent.Remove.class), "<init>", "(Lnet/minecraft/network/play/server/S38PacketPlayerListItem$AddPlayerData;)V", false);
                    ASMUtils.postEvent(a);
                }));
            }
        }
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.network.NetHandlerPlayClient");
    }
}
