package lol.ethan.may.asm;

import lol.ethan.may.events.*;
import lol.ethan.may.events.gui.GuiCloseEvent;
import lol.ethan.may.events.gui.GuiOpenEvent;
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
                    postEvent(a);
                    a.pop();
                }));
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(TickEvent.Pre.class));
                    a.dup();
                    a.invokespecial(Type.getInternalName(TickEvent.Pre.class), "<init>",
                            "()V", false);
                    postEvent(a);
                    a.pop();
                }));
            }
            if (method.name.equals("func_152348_aa") || method.name.equals("dispatchKeypresses")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (insnNode instanceof VarInsnNode) {
                        final VarInsnNode vin = (VarInsnNode) insnNode;
                        if (vin.var == 1 && vin.getOpcode() == Opcodes.ISTORE) {
                            method.instructions.insert(vin, ASMUtils.asm(a -> {
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
                method.instructions.insert(ASMUtils.asm(a -> {
                    final Label top = new Label();
                    a.load(0, ASMUtils.getType(Minecraft.class));
                    a.getfield("net/minecraft/client/Minecraft", mapField(Minecraft.class, "currentScreen"), "Lnet/minecraft/client/gui/GuiScreen;");
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
                    a.checkcast(Type.getType(GuiOpenEvent.class));
                    a.dup();
                    ASMUtils.ifEventCancel(a, asm -> {
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
