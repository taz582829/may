package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ASMUtils;
import lol.ethan.may.asm.ITransformer;
import lol.ethan.may.events.ClientDisconnectFromServerEvent;
import lol.ethan.may.events.Event;
import lol.ethan.may.events.PacketEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class NetworkManagerHook implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            LabelNode end;
            InsnList insnList;
            if (method.name.equals("sendPacket") || method.name.equals("func_179288_a") || method.name.equals("func_179290_a")) {
                insnList = new InsnList();
                insnList.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(PacketEvent.Send.class)));
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(PacketEvent.Send.class), "<init>", "(Lnet/minecraft/network/Packet;)V"));
                insnList.add(new InsnNode(Opcodes.DUP));
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
            insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insnList.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(ClientDisconnectFromServerEvent.class)));
            insnList.add(new InsnNode(Opcodes.DUP));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(ClientDisconnectFromServerEvent.class), "<init>", "(Lnet/minecraft/util/IChatComponent;)V"));
            insnList.add(new InsnNode(Opcodes.DUP));
            insnList.add(ASMUtils.postEvent());
            insnList.add(new InsnNode(Opcodes.DUP));
            insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(ClientDisconnectFromServerEvent.class), "getReason", "()Lnet/minecraft/util/IChatComponent;"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.network.NetworkManagerHook");
    }
}
