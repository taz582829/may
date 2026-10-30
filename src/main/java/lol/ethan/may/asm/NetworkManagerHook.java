package lol.ethan.may.asm;

import lol.ethan.may.events.ClientDisconnectFromServerEvent;
import lol.ethan.may.events.Event;
import lol.ethan.may.events.PacketEvent;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;

public class NetworkManagerHook implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            LabelNode end;
            InsnList insnList;
            if (method.name.equals("sendPacket") || method.name.equals("func_179288_a") || method.name.equals("func_179290_a")) {
                insnList = new InsnList();
                insnList.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(PacketEvent.Send.class)));
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(PacketEvent.Send.class), "<init>", "(Lnet/minecraft/network/Packet;)V"));
                insnList.add(ASMUtils.postEvent());
                end = new LabelNode();
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Event.class), "isCancelled", "()Z"));
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, end));
                insnList.add(new InsnNode(Opcodes.POP));
                insnList.add(new InsnNode(Opcodes.RETURN));
                insnList.add(end);
                insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(PacketEvent.Send.class)));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(PacketEvent.Send.class), "getPacket", "()Lnet/minecraft/network/Packet;"));
                insnList.add(new VarInsnNode(Opcodes.ASTORE, 1));
                method.instructions.insert(insnList);
            }
            if (method.name.equals("channelRead0") && method.desc.equals("(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V")) {
                insnList = new InsnList();
                insnList.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(PacketEvent.Receive.class)));
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(PacketEvent.Receive.class), "<init>", "(Lnet/minecraft/network/Packet;)V"));
                insnList.add(ASMUtils.postEvent());
                end = new LabelNode();
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Event.class), "isCancelled", "()Z"));
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, end));
                insnList.add(new InsnNode(Opcodes.POP));
                insnList.add(new InsnNode(Opcodes.RETURN));
                insnList.add(end);
                insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(PacketEvent.Receive.class)));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(PacketEvent.Receive.class), "getPacket", "()Lnet/minecraft/network/Packet;"));
                insnList.add(new VarInsnNode(Opcodes.ASTORE, 2));
                method.instructions.insert(insnList);
            }
            if (!method.name.equals("func_150718_a") && !method.name.equals("closeChannel")) continue;
            method.instructions.insert(ASMUtils.asm(a -> {
                a.anew(Type.getType(ClientDisconnectFromServerEvent.class));
                a.dup();
                a.load(1, Type.getType(IChatComponent.class));
                a.invokespecial(Type.getInternalName(ClientDisconnectFromServerEvent.class), "<init>",
                        "(Lnet/minecraft/util/IChatComponent;)V", false);
                ASMUtils.postEvent(a);
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

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.network.NetworkManager");
    }
}
