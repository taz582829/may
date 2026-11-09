package lol.ethan.may.asm.transformers;

import lol.ethan.may.asm.Transformer;
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
                    a.invokespecial(RenderGameOverlayEvent.Pre.class, "<init>", "(F)V");
                    a.postEvent();
                    a.pop();
                }));
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(RenderGameOverlayEvent.Pre.class));
                    a.dup();
                    a.load(1, Type.FLOAT_TYPE);
                    a.invokespecial(RenderGameOverlayEvent.Pre.class, "<init>", "(F)V");
                    a.postEvent();
                    a.pop();
                }));
            }
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
