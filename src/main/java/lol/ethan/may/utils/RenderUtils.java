package lol.ethan.may.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;

public class RenderUtils {
    private static final Field renderPosXField;
    private static final Field renderPosYField;
    private static final Field renderPosZField;

    static {
        try {
            renderPosXField = RenderManager.class.getDeclaredField("renderPosX");
            renderPosYField = RenderManager.class.getDeclaredField("renderPosY");
            renderPosZField = RenderManager.class.getDeclaredField("renderPosZ");
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Timer getTimer() {
        try {
            final Minecraft mc = Minecraft.getMinecraft();
            return (Timer) mc.getClass().getDeclaredField("timer").get(mc);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static double[] getRenderPos() {
        final RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        try {
            return new double[]{(Double) renderPosXField.get(manager), (Double) renderPosYField.get(manager), (Double) renderPosZField.get(manager)};
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double[] posToRenderCoords(final Entity entity) {
        final Timer timer = RenderUtils.getTimer();
        final float pt = timer.renderPartialTicks;
        final double renderX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) pt;
        final double renderY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) pt;
        final double renderZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) pt;
        return new double[]{renderX, renderY, renderZ};
    }

    public static double[] posToRenderCoords(final Number x, final Number y, final Number z) {
        final Minecraft mc = Minecraft.getMinecraft();
        final double viewerX = mc.getRenderManager().viewerPosX;
        final double viewerY = mc.getRenderManager().viewerPosY;
        final double viewerZ = mc.getRenderManager().viewerPosZ;
        return new double[]{x.doubleValue() - viewerX, y.doubleValue() - viewerY, z.doubleValue() - viewerZ};
    }

    public static double lerpDouble(final double current, final double previous, final double t) {
        return previous + (current - previous) * t;
    }

    public static void enableRenderState() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void enableRenderState(final boolean throughWalls) {
        if (throughWalls) {
            RenderUtils.enableRenderState();
            return;
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
    }

    public static void disableRenderState() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBoundingBox(final AxisAlignedBB axisAlignedBB, final Color color, final float lineWidth) {
        if (null == axisAlignedBB) {
            return;
        }
        RenderUtils.enableRenderState();
        final int red = color.getRed();
        final int green = color.getGreen();
        final int blue = color.getBlue();
        final int alpha = color.getAlpha();
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderGlobal.drawOutlinedBoundingBox(axisAlignedBB, red, green, blue, alpha);
        GL11.glDisable(2848);
        GL11.glLineWidth(2.0f);
        RenderUtils.disableRenderState();
    }

    public static void drawFilledBox(final AxisAlignedBB axisAlignedBB, final Color color) {
        RenderUtils.enableRenderState();
        final int red = color.getRed();
        final int blue = color.getBlue();
        final int green = color.getGreen();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(red, green, blue, 63).endVertex();
        tessellator.draw();
        RenderUtils.disableRenderState();
    }

    public static void drawEntityBoundingBox(final Entity entity, final Color color) {
        RenderUtils.drawEntityBoundingBox(entity, color, 1.0f);
    }

    public static void drawEntityBoundingBox(final Entity entity, final Color color, final float lineWidth) {
        final Timer timer = RenderUtils.getTimer();
        final float partialTicks = timer.renderPartialTicks;
        final double d2 = RenderUtils.lerpDouble(entity.posX, entity.lastTickPosX, partialTicks);
        final double d3 = RenderUtils.lerpDouble(entity.posY, entity.lastTickPosY, partialTicks);
        final double d4 = RenderUtils.lerpDouble(entity.posZ, entity.lastTickPosZ, partialTicks);
        final double[] rp = RenderUtils.getRenderPos();
        final AxisAlignedBB bb = entity.getEntityBoundingBox().offset(d2 - entity.posX, d3 - entity.posY, d4 - entity.posZ).offset(-rp[0], -rp[1], -rp[2]);
        RenderUtils.enableRenderState();
        GL11.glLineWidth(lineWidth);
        RenderGlobal.drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        RenderUtils.disableRenderState();
    }

    public static void drawTileEntityBox(final TileEntity tileEntity, final Color color) {
        AxisAlignedBB bb;
        final BlockPos pos = tileEntity.getPos();
        if (tileEntity instanceof TileEntitySign) {
            final IBlockState state = Minecraft.getMinecraft().theWorld.getBlockState(pos);
            bb = state.getBlock().getSelectedBoundingBox(Minecraft.getMinecraft().theWorld, pos);
        } else {
            bb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        }
        bb = bb.expand(0.1, 0.1, 0.1);
        final double[] rp = RenderUtils.getRenderPos();
        bb = bb.offset(-rp[0], -rp[1], -rp[2]);
        RenderUtils.enableRenderState();
        RenderUtils.drawFilledBox(bb, color);
        RenderUtils.disableRenderState();
    }
}

