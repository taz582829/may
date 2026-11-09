package lol.ethan.may.asm;

import lol.ethan.may.events.user.ClientDisconnectFromServerEvent;
import lol.ethan.may.events.PacketEvent;
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
            if (method.name.equals("sendPacket") || method.name.equals("func_179288_a") || method.name.equals("func_179290_a")) {
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
            if (method.name.equals("channelRead0") && method.desc.equals("(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V")) {
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
            if (!method.name.equals("func_150718_a") && !method.name.equals("closeChannel")) continue;
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
