package lol.ethan.may.asm;

import lol.ethan.may.config.Config;
import lol.ethan.may.events.render.RenderWorldEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;

public class EntityRendererTransformer implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            MethodInsnNode min;
            if (method.name.equals("renderWorldPass")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (!(insnNode instanceof LdcInsnNode)) continue;
                    LdcInsnNode ldc = (LdcInsnNode) insnNode;
                    if ("hand".equals(ldc.cst)) {
                        method.instructions.insertBefore(ldc, ASMUtils.asm(a -> {
                            a.anew(ASMUtils.getType(RenderWorldEvent.class));
                            a.dup();
                            a.load(2, Type.FLOAT_TYPE);
                            a.invokespecial(Type.getInternalName(RenderWorldEvent.class), "<init>", "(F)V", false);
                            ASMUtils.postEvent(a);
                            a.pop();
                        }));
                    }
                }

            }
            if (method.name.equals("updateLightmap")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (!(insnNode instanceof MethodInsnNode)) continue;
                    min = (MethodInsnNode) insnNode;
                    if (!min.name.equals("isPotionActive")) continue;
                    method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityRendererTransformer.class), "updateLightmap_CancelNightVision", "(L" + Type.getInternalName(EntityPlayerSP.class) + ";L" + Type.getInternalName(Potion.class) + ";)Z"));
                }
            }
            if (method.name.equals("updateFogColor")) {
                for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
                    if (!(insnNode instanceof MethodInsnNode)) continue;
                    min = (MethodInsnNode) insnNode;
                    if (!min.name.equals("isPotionActive")) continue;
                    method.instructions.set(min, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EntityRendererTransformer.class), "updateFogColor_CancelNightVision", "(L" + Type.getInternalName(EntityLivingBase.class) + ";L" + Type.getInternalName(Potion.class) + ";)Z"));
                }
            }

        }
    }

    public static boolean updateLightmap_CancelNightVision(final EntityPlayerSP player, final Potion potion) {
        if (player == null || potion == null) {
            return false;
        }
        if (potion == Potion.nightVision && Config.clearNightVision != null && Config.clearNightVision.enabled) {
            return false;
        }
        return player.isPotionActive(potion);
    }

    public static boolean updateFogColor_CancelNightVision(final EntityLivingBase entity, final Potion potion) {
        if (entity == null || potion == null) {
            return false;
        }
        if (potion == Potion.nightVision && Config.clearNightVision != null && Config.clearNightVision.enabled) {
            return false;
        }
        return entity.isPotionActive(potion);
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.renderer.EntityRenderer");
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
