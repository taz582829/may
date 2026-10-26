package lol.ethan.may.asm;

import java.util.Collections;
import java.util.List;
import lol.ethan.may.asm.ITransformer;
import lol.ethan.may.config.Config;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import lol.ethan.may.utils.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class HitboxesHook implements ITransformer {
    @Override
    public void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (!method.name.equals("func_85094_b") && !method.name.equals("renderDebugBoundingBox")) continue;
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insnList.add(new VarInsnNode(Opcodes.DLOAD, 2));
            insnList.add(new VarInsnNode(Opcodes.DLOAD, 4));
            insnList.add(new VarInsnNode(Opcodes.DLOAD, 6));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(HitboxesHook.class), "may$renderHitboxes", "(Lnet/minecraft/entity/Entity;DDD)Z"));
            LabelNode label = new LabelNode();
            insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
            insnList.add(new InsnNode(Opcodes.RETURN));
            insnList.add(label);
            method.instructions.insert(insnList);
        }
    }

    public static boolean may$renderHitboxes(Entity entity, double x, double y, double z) {
        if (entity instanceof EntityPlayer && Config.modifyPlayerHitboxes.enabled) {
            EntityPlayer player = (EntityPlayer)entity;
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GL11.glLineWidth(1.0f);
            AxisAlignedBB bb = entity.getEntityBoundingBox();
            float f = entity.getCollisionBorderSize();
            AxisAlignedBB bb1 = new AxisAlignedBB(bb.minX - entity.posX + x, bb.minY - entity.posY + y, bb.minZ - entity.posZ + z, bb.maxX - entity.posX + x, bb.maxY - entity.posY + y, bb.maxZ - entity.posZ + z);
            if (Config.accuratePlayerHitboxes.enabled) {
                bb1 = bb1.expand(f, f, f);
            }
            int color = Config.hitboxColor.color;
            if (Config.teamColoredPlayerHitboxes.enabled) {
                color = Integer.parseInt(ColorUtils.getColorFromTeam((ScorePlayerTeam)player.getTeam()), 16);
            }
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            RenderGlobal.drawOutlinedBoundingBox(bb1, r, g, b, 255);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            return true;
        }
        if (entity instanceof EntityArrow && Config.modifyArrowHitboxes.enabled) {
            EntityArrow arrow = (EntityArrow)entity;
            Entity shooter = arrow.shootingEntity;
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GL11.glLineWidth(1.0f);
            AxisAlignedBB bb = entity.getEntityBoundingBox();
            float f = entity.getCollisionBorderSize();
            AxisAlignedBB bb1 = new AxisAlignedBB(bb.minX - entity.posX + x, bb.minY - entity.posY + y, bb.minZ - entity.posZ + z, bb.maxX - entity.posX + x, bb.maxY - entity.posY + y, bb.maxZ - entity.posZ + z);
            if (Config.accurateArrowHitboxes.enabled) {
                bb1 = bb1.expand(f, f, f);
            }
            int color = Config.hitboxColor.color;
            ScorePlayerTeam team = (ScorePlayerTeam)(shooter instanceof EntityPlayer ? ((EntityPlayer)shooter).getTeam() : null);
            if (Config.teamColoredArrowHitboxes.enabled) {
                color = Integer.parseInt(ColorUtils.getColorFromTeam(team), 16);
            }
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            RenderGlobal.drawOutlinedBoundingBox(bb1, r, g, b, 255);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getTargets() {
        return Collections.singletonList("net.minecraft.client.renderer.entity.RenderManager");
    }

    @Override
    public boolean shouldComputeFrames() {
        return true;
    }
}
