package lol.ethan.may.asm;

import lol.ethan.may.config.Config;
import lol.ethan.may.utils.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.List;

public class HitboxesHook implements ITransformer {
    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if (!method.name.equals("func_85094_b") && !method.name.equals("renderDebugBoundingBox")) continue;
            method.instructions.insert(ASMUtils.asm(a -> {
                a.load(1, Type.getType(Entity.class));
                a.load(2, Type.DOUBLE_TYPE);
                a.load(4, Type.DOUBLE_TYPE);
                a.load(6, Type.DOUBLE_TYPE);
                a.invokestatic(Type.getInternalName(HitboxesHook.class), "may$renderHitboxes",
                        "(Lnet/minecraft/entity/Entity;DDD)Z", false);
                final Label end = new Label();
                a.ifeq(end);
                a.areturn(Type.VOID_TYPE);
                a.mark(end);
            }));
        }
    }

    public static boolean may$renderHitboxes(final Entity entity, final double x, final double y, final double z) {
        if (entity instanceof EntityPlayer && Config.modifyPlayerHitboxes.enabled) {
            final EntityPlayer player = (EntityPlayer)entity;
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GL11.glLineWidth(1.0f);
            final AxisAlignedBB bb = entity.getEntityBoundingBox();
            final float f = entity.getCollisionBorderSize();
            AxisAlignedBB bb1 = new AxisAlignedBB(bb.minX - entity.posX + x, bb.minY - entity.posY + y, bb.minZ - entity.posZ + z, bb.maxX - entity.posX + x, bb.maxY - entity.posY + y, bb.maxZ - entity.posZ + z);
            if (Config.accuratePlayerHitboxes.enabled) {
                bb1 = bb1.expand(f, f, f);
            }
            int color = Config.hitboxColor.color;
            if (Config.teamColoredPlayerHitboxes.enabled) {
                color = Integer.parseInt(ColorUtils.getColorFromTeam((ScorePlayerTeam)player.getTeam()), 16);
            }
            final int r = color >> 16 & 0xFF;
            final int g = color >> 8 & 0xFF;
            final int b = color & 0xFF;
            RenderGlobal.drawOutlinedBoundingBox(bb1, r, g, b, 255);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            return true;
        }
        if (entity instanceof EntityArrow && Config.modifyArrowHitboxes.enabled) {
            final EntityArrow arrow = (EntityArrow)entity;
            final Entity shooter = arrow.shootingEntity;
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GL11.glLineWidth(1.0f);
            final AxisAlignedBB bb = entity.getEntityBoundingBox();
            final float f = entity.getCollisionBorderSize();
            AxisAlignedBB bb1 = new AxisAlignedBB(bb.minX - entity.posX + x, bb.minY - entity.posY + y, bb.minZ - entity.posZ + z, bb.maxX - entity.posX + x, bb.maxY - entity.posY + y, bb.maxZ - entity.posZ + z);
            if (Config.accurateArrowHitboxes.enabled) {
                bb1 = bb1.expand(f, f, f);
            }
            int color = Config.hitboxColor.color;
            final ScorePlayerTeam team = (ScorePlayerTeam)(shooter instanceof EntityPlayer ? ((EntityPlayer)shooter).getTeam() : null);
            if (Config.teamColoredArrowHitboxes.enabled) {
                color = Integer.parseInt(ColorUtils.getColorFromTeam(team), 16);
            }
            final int r = color >> 16 & 0xFF;
            final int g = color >> 8 & 0xFF;
            final int b = color & 0xFF;
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
