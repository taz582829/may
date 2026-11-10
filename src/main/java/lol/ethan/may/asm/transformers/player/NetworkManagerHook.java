package lol.ethan.may.asm.transformers.player;

import lol.ethan.may.asm.Transformer;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.user.ClientDisconnectFromServerEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class NetworkManagerHook extends Transformer {
    public NetworkManagerHook() {
        super(NetworkManager.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if ("sendPacket".equals(method.name) || "func_179288_a".equals(method.name) || "func_179290_a".equals(method.name)) {
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(PacketEvent.Send.class));
                    a.dup();
                    a.load(1, Type.getType(Packet.class));
                    a.invokespecial(Type.getInternalName(PacketEvent.Send.class), "<init>",
                            "(Lnet/minecraft/network/Packet;)V", false);
                    a.dup();
                    a.postEvent();
                    ifEventCancel(a, asm -> {
                        asm.pop();
                        asm.areturn(Type.VOID_TYPE);
                    });
                    a.checkcast(Type.getType(PacketEvent.Send.class));
                    a.invokevirtual(Type.getInternalName(PacketEvent.Send.class), "getPacket",
                            "()Lnet/minecraft/network/Packet;", false);
                    a.store(1, Type.getType(Packet.class));
                }));
            }
            if ("channelRead0".equals(method.name) && "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V".equals(method.desc)) {
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(PacketEvent.Receive.class));
                    a.dup();
                    a.load(2, Type.getType(Packet.class));
                    a.invokespecial(Type.getInternalName(PacketEvent.Receive.class), "<init>",
                            "(Lnet/minecraft/network/Packet;)V", false);
                    a.dup();
                    a.postEvent();
                    ifEventCancel(a, asm -> {
                        asm.pop();
                        asm.areturn(Type.VOID_TYPE);
                    });
                    a.checkcast(Type.getType(PacketEvent.Receive.class));
                    a.invokevirtual(Type.getInternalName(PacketEvent.Receive.class), "getPacket",
                            "()Lnet/minecraft/network/Packet;", false);
                    a.store(2, Type.getType(Packet.class));
                }));
            }
            if (!"func_150718_a".equals(method.name) && !"closeChannel".equals(method.name)) continue;
            method.instructions.insert(asm(a -> {
                a.anew(Type.getType(ClientDisconnectFromServerEvent.class));
                a.dup();
                a.load(1, Type.getType(IChatComponent.class));
                a.invokespecial(Type.getInternalName(ClientDisconnectFromServerEvent.class), "<init>",
                        "(Lnet/minecraft/util/IChatComponent;)V", false);
                a.postEvent();
                a.checkcast(Type.getType(ClientDisconnectFromServerEvent.class));
                a.dup();
                a.invokevirtual(Type.getInternalName(ClientDisconnectFromServerEvent.class), "getReason",
                        "()Lnet/minecraft/util/IChatComponent;", false);
                a.store(1, Type.getType(IChatComponent.class));
            }));
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
