package lol.ethan.may.asm;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.RenderWorldEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class EntityRendererTransformer implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            MethodInsnNode min;
            if (method.name.equals("renderWorldPass")) {
                method.instructions.insert(ASMUtils.asm(a -> {
                    a.anew(ASMUtils.getType(RenderWorldEvent.class));
                    a.dup();
                    a.load(2, Type.FLOAT_TYPE);
                    a.invokevirtual(Type.getInternalName(RenderWorldEvent.class), "<init>", "(F)V", false);
                    ASMUtils.postEvent(a);
                    ASMUtils.returnIfEventCancelled(a);
                }));
            }
            if (method.name.equals("updateLightmap")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (!(insnNode instanceof MethodInsnNode)) continue;
                    min = (MethodInsnNode)insnNode;
                    if (!min.name.equals("isPotionActive")) continue;
                    method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityRendererTransformer.class), "updateLightmap_CancelNightVision", "(L" + Type.getInternalName(EntityPlayerSP.class) + ";L" + Type.getInternalName(Potion.class) + ";)Z"));
                }
            }
            if (!method.name.equals("updateFogColor")) continue;
            for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                min = (MethodInsnNode)insnNode;
                if (!min.name.equals("isPotionActive")) continue;
                method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityRendererTransformer.class), "updateFogColor_CancelNightVision", "(L" + Type.getInternalName(EntityLivingBase.class) + ";L" + Type.getInternalName(Potion.class) + ";)Z"));
            }
        }
    }

    public static boolean updateLightmap_CancelNightVision(final EntityPlayerSP player, final Potion potion) {
        if (potion == Potion.nightVision && Config.clearNightVision.enabled) {
            return false;
        }
        return player.isPotionActive(potion);
    }

    public static boolean updateFogColor_CancelNightVision(final EntityLivingBase entity, final Potion potion) {
        if (potion == Potion.nightVision && Config.clearNightVision.enabled) {
            return false;
        }
        return entity.isPotionActive(potion);
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.renderer.EntityRenderer");
    }
}
