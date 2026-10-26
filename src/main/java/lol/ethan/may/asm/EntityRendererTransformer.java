package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ASMUtils;
import lol.ethan.may.asm.ITransformer;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.RenderWorldEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class EntityRendererTransformer implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
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
                for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (!(insnNode instanceof MethodInsnNode)) continue;
                    min = (MethodInsnNode)insnNode;
                    if (!min.name.equals("isPotionActive")) continue;
                    method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityRendererTransformer.class), "updateLightmap_CancelNightVision", "(L" + Type.getInternalName(EntityPlayerSP.class) + ";L" + Type.getInternalName(Potion.class) + ";)Z"));
                }
            }
            if (!method.name.equals("updateFogColor")) continue;
            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (!(insnNode instanceof MethodInsnNode)) continue;
                min = (MethodInsnNode)insnNode;
                if (!min.name.equals("isPotionActive")) continue;
                method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityRendererTransformer.class), "updateFogColor_CancelNightVision", "(L" + Type.getInternalName(EntityLivingBase.class) + ";L" + Type.getInternalName(Potion.class) + ";)Z"));
            }
        }
    }

    public static boolean updateLightmap_CancelNightVision(EntityPlayerSP player, Potion potion) {
        if (potion == Potion.nightVision && Config.clearNightVision.enabled) {
            return false;
        }
        return player.isPotionActive(potion);
    }

    public static boolean updateFogColor_CancelNightVision(EntityLivingBase entity, Potion potion) {
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
