package lol.ethan.may.asm;

import lol.ethan.may.events.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;

public class MinecraftTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("runTick") || method.name.equals("func_71407_l")) {
                for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode.getOpcode() >= Opcodes.IRETURN && insnNode.getOpcode() <= Opcodes.RETURN) {
                        method.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MinecraftTransformer.class), "postTickEvent", "()V"));
                    }
                    if (!(insnNode instanceof MethodInsnNode)) continue;
                    MethodInsnNode mn = (MethodInsnNode)insnNode;
                    if (!mn.owner.equals(Type.getInternalName(Mouse.class)) || !mn.name.equals("next")) continue;
                    LabelNode top = new LabelNode();
                    method.instructions.insertBefore(mn, top);
                    InsnList insnList = ASMUtils.asm(a -> {
                        a.anew(ASMUtils.getType(MouseEvent.class));
                        a.dup();
                        a.invokespecial(Type.getInternalName(MouseEvent.class), "<init>", "()V", false);
                        ASMUtils.postEvent(a);
                        a.invokevirtual(Type.getInternalName(Event.class), "isCancelled", "()Z", false);
                    });
                    insnList.add(new JumpInsnNode(Opcodes.IFNE, top));
                    method.instructions.insert(mn.getNext(), insnList);
                }
                method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MinecraftTransformer.class), "preTickEvent", "()V"));
            }
            if (method.name.equals("func_152348_aa") || method.name.equals("dispatchKeypresses")) {
                for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (!(insnNode instanceof VarInsnNode)) continue;
                    VarInsnNode vin = (VarInsnNode)insnNode;
                    if (vin.var != 1 || vin.getOpcode() != Opcodes.ISTORE) continue;
                    InsnList insnList = new InsnList();
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MinecraftTransformer.class), "getKeypresses", "()L" + Type.getInternalName(KeyboardEvent.class) + ";"));
                    insnList.add(new InsnNode(Opcodes.DUP));
                    LabelNode label = new LabelNode();
                    insnList.add(new JumpInsnNode(Opcodes.IFNONNULL, label));
                    insnList.add(new InsnNode(Opcodes.POP));
                    insnList.add(new InsnNode(Opcodes.RETURN));
                    insnList.add(label);
                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(KeyboardEvent.class), "getBind", "()I"));
                    insnList.add(new VarInsnNode(Opcodes.ISTORE, 1));
                    method.instructions.insert(insnNode, insnList);
                    break;
                }
            }
            if (method.name.equals("func_71405_e") || method.name.equals("shutdownMinecraftApplet")) {
                InsnList insnList = new InsnList();
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MinecraftTransformer.class), "onShutdown", "()V"));
                method.instructions.insert(insnList);
            }
            if (!method.name.equals("displayGuiScreen")) continue;
            method.instructions.insert(ASMUtils.asm(a -> {
                Label top = new Label();
                a.load(0, ASMUtils.getType(Minecraft.class));
                a.getfield("net/minecraft/client/Minecraft", "currentScreen", "Lnet/minecraft/client/gui/GuiScreen;");
                a.ifnull(top);
                a.anew(ASMUtils.getType(GuiCloseEvent.class));
                a.dup();
                a.invokespecial(Type.getInternalName(GuiCloseEvent.class), "<init>", "()V", false);
                ASMUtils.postEvent(a);
                a.pop();
                a.mark(top);
                a.anew(ASMUtils.getType(GuiOpenEvent.class));
                a.dup();
                a.load(1, ASMUtils.getType(GuiScreen.class));
                a.invokespecial(Type.getInternalName(GuiOpenEvent.class), "<init>", "(Lnet/minecraft/client/gui/GuiScreen;)V", false);
                ASMUtils.postEvent(a);
                a.pop();
            }));
        }
    }

    public static void onShutdown() {
        EventBus.postEvent(new ShutdownEvent());
    }

    public static void tickEvent() {
        EventBus.postEvent(new TickEvent());
    }

    public static void postTickEvent() {
        EventBus.postEvent(new TickEvent.Post());
        MinecraftTransformer.tickEvent();
    }

    public static void preTickEvent() {
        EventBus.postEvent(new TickEvent.Pre());
        MinecraftTransformer.tickEvent();
    }

    public static KeyboardEvent getKeypresses() {
        int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
        KeyboardEvent event = new KeyboardEvent(key, Keyboard.getEventKeyState());
        EventBus.postEvent(event);
        if (event.isCancelled()) {
            return null;
        }
        return event;
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.Minecraft");
    }
}
