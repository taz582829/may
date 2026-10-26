package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.RenderGameOverlayEvent;
import lol.ethan.may.events.RenderXPEvent;
import lol.ethan.may.events.ScoreboardLineEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import net.minecraft.scoreboard.Team;

public class GuiIngameHook
implements ITransformer {
    private static final String CALLBACK_INFO_CLASS = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";

    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("renderGameOverlay") || method.name.equals("func_175180_a")) {
                InsnList preInsnList = new InsnList();
                preInsnList.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(RenderGameOverlayEvent.Pre.class)));
                preInsnList.add(new InsnNode(Opcodes.DUP));
                preInsnList.add(new VarInsnNode(Opcodes.FLOAD, 1));
                preInsnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(RenderGameOverlayEvent.Pre.class), "<init>", "(F)V"));
                preInsnList.add(ASMUtils.postEvent());
                preInsnList.add(new InsnNode(Opcodes.POP));
                method.instructions.insert(preInsnList);
                AbstractInsnNode[] abstractInsnNodeArray = method.instructions.toArray();
                int n = abstractInsnNodeArray.length;
                for (int i = 0; i < n; i++) {
                    AbstractInsnNode insnNode = abstractInsnNodeArray[i];
                    if (insnNode.getOpcode() < Opcodes.IRETURN || insnNode.getOpcode() > Opcodes.RETURN) continue;
                    InsnList postInsnList = new InsnList();
                    postInsnList.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(RenderGameOverlayEvent.Post.class)));
                    postInsnList.add(new InsnNode(Opcodes.DUP));
                    postInsnList.add(new VarInsnNode(Opcodes.FLOAD, 1));
                    postInsnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(RenderGameOverlayEvent.Post.class), "<init>", "(F)V"));
                    postInsnList.add(ASMUtils.postEvent());
                    postInsnList.add(new InsnNode(Opcodes.POP));
                    method.instructions.insertBefore(insnNode, postInsnList);
                }
            }
            if (method.name.equals("renderExpBar") || method.name.equals("func_175176_b")) {
                InsnList insnList = new InsnList();
                insnList.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(RenderXPEvent.class)));
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(RenderXPEvent.class), "<init>", "()V"));
                insnList.add(ASMUtils.postEvent());
                insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(RenderXPEvent.class)));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(RenderXPEvent.class), "isCancelled", "()Z"));
                LabelNode label = new LabelNode();
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
                insnList.add(new InsnNode(Opcodes.RETURN));
                insnList.add(label);
                method.instructions.insert(insnList);
            }
            if (!method.name.equals("renderScoreboard")) continue;
            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                MethodInsnNode min = (MethodInsnNode)insnNode;
                if (!min.name.equals("formatPlayerName")) continue;
                method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(GuiIngameHook.class), "getScoreboardLine", "(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;"));
            }
        }
    }

    public static boolean isScoreboardCancelled(Object ci) {
        try {
            return (Boolean)ci.getClass().getMethod("isCancelled", new Class[0]).invoke(ci, new Object[0]);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getScoreboardLine(Team team, String name) {
        ScoreboardLineEvent event = new ScoreboardLineEvent(team, name);
        EventBus.postEvent(event);
        return event.getLine();
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.gui.GuiIngame");
    }
}
