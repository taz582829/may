package lol.ethan.may.asm.transformers;

import lol.ethan.may.asm.Transformer;
import lol.ethan.may.events.render.RenderParticleEvent;
import net.minecraft.client.particle.EntityFX;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityFXTransformer extends Transformer {
    public EntityFXTransformer() {
        super(EntityFX.class);
    }

    //WorldRenderer p_renderParticle_1_, Entity p_renderParticle_2_, float p_renderParticle_3_, float p_renderParticle_4_, float p_renderParticle_5_, float p_renderParticle_6_, float p_renderParticle_7_, float p_renderParticle_8_
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (method.name.equals("renderParticle")) {
                insertBeforeReturn(method, asm(a -> {
                    a.anew(Type.getType(RenderParticleEvent.Post.class));
                    a.dup();

                    for (int i = 3; i <= 8; i++) a.load(i, Type.FLOAT_TYPE);
                    a.load(0, Type.getType(EntityFX.class));
                    final String desc = Type.getConstructorDescriptor(
                            RenderParticleEvent.Post.class.getDeclaredConstructors()[0]);
                    a.invokespecial(RenderParticleEvent.Post.class, "<init>", desc);
                    a.postEvent();
                    a.pop();
                }));
                //int local = method.maxLocals++;
                method.instructions.insert(asm(a -> {
                    a.anew(Type.getType(RenderParticleEvent.Pre.class));
                    a.dup();

                    for (int i = 3; i <= 8; i++) a.load(i, Type.FLOAT_TYPE);
                    a.load(0, Type.getType(EntityFX.class));
                    final String desc = Type.getConstructorDescriptor(
                            RenderParticleEvent.Pre.class.getDeclaredConstructors()[0]);
                    a.invokespecial(RenderParticleEvent.Pre.class, "<init>", desc);
                    a.postEvent();
                    ifEventCancel(a, asm -> asm.areturn(Type.VOID_TYPE));
                }));
            }
        }
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
