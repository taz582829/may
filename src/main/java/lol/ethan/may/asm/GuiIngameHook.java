package lol.ethan.may.asm;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.ScoreboardLineEvent;
import lol.ethan.may.events.render.RenderGameOverlayEvent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.scoreboard.Team;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiIngameHook extends Transformer {
    private static final String CALLBACK_INFO_CLASS = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";

    public GuiIngameHook() {
        super(GuiIngame.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.name.equals("renderGameOverlay") || method.name.equals("func_175180_a")) {
                insertBeforeReturn(method, asm(a -> {
                    a.anew(Type.getType(RenderGameOverlayEvent.Post.class));
                    a.dup();
                    a.load(1, Type.FLOAT_TYPE);
                    ASMUtils.invokeConstructor(RenderGameOverlayEvent.Post.class, "(F)V", a);
                    ASMUtils.postEvent(a);
                    a.pop();
                }));
                method.instructions.insert(ASMUtils.asm(a -> {
                    a.anew(Type.getType(RenderGameOverlayEvent.Pre.class));
                    a.dup();
                    a.load(1, Type.FLOAT_TYPE);
                    ASMUtils.invokeConstructor(RenderGameOverlayEvent.Pre.class, "(F)V", a);
                    ASMUtils.postEvent(a);
                    a.pop();
                }));
            }

            if (!method.name.equals("renderScoreboard")) continue;
            for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                final MethodInsnNode min = (MethodInsnNode) insnNode;
                if (!min.name.equals("formatPlayerName")) continue;
                method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(GuiIngameHook.class), "getScoreboardLine", "(Lnet/minecraft/scoreboard/Team;Ljava/lang/String;)Ljava/lang/String;"));
            }
        }
    }

    public static boolean isScoreboardCancelled(final Object ci) {
        try {
            return (Boolean) ci.getClass().getMethod("isCancelled", new Class[0]).invoke(ci, new Object[0]);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            return false;
        }
    }

    public static String getScoreboardLine(final Team team, final String name) {
        final ScoreboardLineEvent event = new ScoreboardLineEvent(team, name);
        EventBus.postEvent(event);
        return event.getLine();
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
