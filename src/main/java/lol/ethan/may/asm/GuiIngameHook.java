package lol.ethan.may.asm;

import lol.ethan.may.events.ActionBarEvent;
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
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.name.equals("renderGameOverlay") || method.name.equals("func_175180_a")) {
                insertBeforeReturn(method, asm(a -> {
                    a.anew(Type.getType(RenderGameOverlayEvent.Post.class));
                    a.dup();
                    a.load(1, Type.FLOAT_TYPE);
                    a.invokespecial(Type.getInternalName(RenderGameOverlayEvent.Post.class), "<init>",
                            "(F)V", false);
                    postEvent(a);
                    a.pop();
                }));
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(RenderGameOverlayEvent.Pre.class));
                    a.dup();
                    a.load(1, Type.FLOAT_TYPE);
                    a.invokespecial(Type.getInternalName(RenderGameOverlayEvent.Pre.class), "<init>",
                            "(F)V", false);
                    postEvent(a);
                    a.pop();
                }));
            }

            if (method.name.equals("setRecordPlaying") && method.desc.equals("(Ljava/lang/String;Z)V")) {
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(ActionBarEvent.class));
                    a.dup();
                    a.load(1, Type.getType(String.class));
                    a.invokespecial(Type.getInternalName(ActionBarEvent.class), "<init>",
                            "(Ljava/lang/String;)V", false);
                    postEvent(a);
                    ifEventCancel(a, asm -> {
                        asm.pop();
                        asm.areturn(Type.VOID_TYPE);
                    });
                    a.checkcast(Type.getType(ActionBarEvent.class));
                    a.invokevirtual(Type.getInternalName(ActionBarEvent.class), "getMessage",
                            "()Ljava/lang/String;", false);
                    a.store(1, Type.getType(String.class));
                }));
            }
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
