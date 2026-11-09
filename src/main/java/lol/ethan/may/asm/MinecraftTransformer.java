package lol.ethan.may.asm;

import lol.ethan.may.events.Event;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.ShutdownEvent;
import lol.ethan.may.events.TickEvent;
import lol.ethan.may.events.gui.GuiCloseEvent;
import lol.ethan.may.events.gui.GuiOpenEvent;
import lol.ethan.may.events.user.KeyboardEvent;
import lol.ethan.may.events.user.MouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class MinecraftTransformer extends Transformer {
    public MinecraftTransformer() {
        super(Minecraft.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.name.equals("runTick") || method.name.equals("func_71407_l")) {
                insertBeforeReturn(method, asm(a -> {
                    a.anew(Type.getType(TickEvent.Post.class));
                    a.dup();
                    a.invokespecial(Type.getInternalName(TickEvent.Post.class), "<init>",
                            "()V", false);
                    a.postEvent();
                    a.pop();
                }));
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(TickEvent.Pre.class));
                    a.dup();
                    a.invokespecial(Type.getInternalName(TickEvent.Pre.class), "<init>",
                            "()V", false);
                    a.postEvent();
                    a.pop();
                }));
                for (final AbstractInsnNode insn : method.instructions.toArray()) {
                    if (insn instanceof MethodInsnNode) {
                        final MethodInsnNode mn = (MethodInsnNode) insn;
                        if (mn.owner.equals(Type.getInternalName(Mouse.class)) && mn.name.equals("next")) {
                            final Label top = new Label();
                            final LabelNode ln = new LabelNode(top);
                            top.info = ln;

                            method.instructions.insertBefore(mn, ln);
                            method.instructions.insert(mn.getNext(), asm(a -> {
                                a.anew(MouseEvent.class);
                                a.dup();
                                a.invokespecial(MouseEvent.class, "<init>", "()V");
                                a.postEvent();
                                a.invokevirtual(Event.class, "isCancelled", "()Z");
                                a.ifne(top);
                            }));
                        }
                    }
                }
            }
            if (method.name.equals("func_152348_aa") || method.name.equals("dispatchKeypresses")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode instanceof VarInsnNode) {
                        final VarInsnNode vin = (VarInsnNode) insnNode;
                        if (vin.var == 1 && vin.getOpcode() == Opcodes.ISTORE) {
                            method.instructions.insert(vin, asm(a -> {
                                a.invokestatic(Type.getInternalName(MinecraftTransformer.class), "getKeypresses",
                                        "()L" + Type.getInternalName(KeyboardEvent.class) + ";", false);
                                a.dup();
                                final Label top = new Label();
                                a.ifnonnull(top);
                                a.pop();
                                a.areturn(Type.VOID_TYPE);
                                a.mark(top);
                                a.invokevirtual(Type.getInternalName(KeyboardEvent.class), "getBind",
                                        "()I", false);
                                a.store(1, Type.INT_TYPE);
                            }));
                        }
                    }
                }
            }
            if (method.name.equals("func_71405_e") || method.name.equals("shutdownMinecraftApplet")) {
                final InsnList insnList = new InsnList();
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MinecraftTransformer.class), "onShutdown", "()V"));
                method.instructions.insert(insnList);
            }
            if (method.name.equals("displayGuiScreen") || method.name.equals("func_147108_a")) {
                method.instructions.insert(asm(a -> {
                    final Label top = new Label();
                    a.load(0, Type.getType(Minecraft.class));
                    a.getfield("net/minecraft/client/Minecraft", mapField(Minecraft.class, "currentScreen"), "Lnet/minecraft/client/gui/GuiScreen;");
                    a.ifnull(top);

                    a.anew(Type.getType(GuiCloseEvent.class));
                    a.dup();
                    a.invokespecial(Type.getInternalName(GuiCloseEvent.class), "<init>", "()V", false);
                    a.postEvent();
                    a.pop();

                    a.mark(top);

                    a.anew(Type.getType(GuiOpenEvent.class));
                    a.dup();
                    a.load(1, Type.getType(GuiScreen.class));
                    a.invokespecial(Type.getInternalName(GuiOpenEvent.class), "<init>", "(Lnet/minecraft/client/gui/GuiScreen;)V", false);

                    a.postEvent();
                    a.checkcast(Type.getType(GuiOpenEvent.class));
                    a.dup();
                    ifEventCancel(a, asm -> {
                        asm.pop();
                        asm.areturn(Type.VOID_TYPE);
                    });
                    a.pop();
                }));
            }

        }
    }

    public static void onShutdown() {
        EventBus.postEvent(new ShutdownEvent());
    }

    public static KeyboardEvent getKeypresses() {
        final int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
        final KeyboardEvent event = new KeyboardEvent(key, Keyboard.getEventKeyState());
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
}
