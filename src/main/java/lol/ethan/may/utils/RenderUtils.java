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
import net.minecraft.world.World;
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
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Timer getTimer() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            return (Timer) mc.getClass().getDeclaredField("timer").get(mc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static double[] getRenderPos() {
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        try {
            return new double[]{(Double) renderPosXField.get(manager), (Double) renderPosYField.get(manager), (Double) renderPosZField.get(manager)};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static double[] posToRenderCoords(Entity entity) {
        Timer timer = RenderUtils.getTimer();
        float pt = timer.renderPartialTicks;
        double renderX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) pt;
        double renderY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) pt;
        double renderZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) pt;
        return new double[]{renderX, renderY, renderZ};
    }

    public static double[] posToRenderCoords(Number x, Number y, Number z) {
        Minecraft mc = Minecraft.getMinecraft();
        double viewerX = mc.getRenderManager().viewerPosX;
        double viewerY = mc.getRenderManager().viewerPosY;
        double viewerZ = mc.getRenderManager().viewerPosZ;
        return new double[]{x.doubleValue() - viewerX, y.doubleValue() - viewerY, z.doubleValue() - viewerZ};
    }

    public static double lerpDouble(double current, double previous, double t) {
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

    public static void enableRenderState(boolean throughWalls) {
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

    public static void drawBoundingBox(AxisAlignedBB axisAlignedBB, Color color, float lineWidth) {
        if (axisAlignedBB == null) {
            return;
        }
        RenderUtils.enableRenderState();
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderGlobal.drawOutlinedBoundingBox(axisAlignedBB, red, green, blue, alpha);
        GL11.glDisable(2848);
        GL11.glLineWidth(2.0f);
        RenderUtils.disableRenderState();
    }

    public static void drawFilledBox(AxisAlignedBB axisAlignedBB, Color color) {
        RenderUtils.enableRenderState();
        int red = color.getRed();
        int blue = color.getBlue();
        int green = color.getGreen();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
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

    public static void drawEntityBoundingBox(Entity entity, Color color) {
        RenderUtils.drawEntityBoundingBox(entity, color, 1.0f);
    }

    public static void drawEntityBoundingBox(Entity entity, Color color, float lineWidth) {
        Timer timer = RenderUtils.getTimer();
        float partialTicks = timer.renderPartialTicks;
        double d2 = RenderUtils.lerpDouble(entity.posX, entity.lastTickPosX, partialTicks);
        double d3 = RenderUtils.lerpDouble(entity.posY, entity.lastTickPosY, partialTicks);
        double d4 = RenderUtils.lerpDouble(entity.posZ, entity.lastTickPosZ, partialTicks);
        double[] rp = RenderUtils.getRenderPos();
        AxisAlignedBB bb = entity.getEntityBoundingBox().offset(d2 - entity.posX, d3 - entity.posY, d4 - entity.posZ).offset(-rp[0], -rp[1], -rp[2]);
        RenderUtils.enableRenderState();
        GL11.glLineWidth(lineWidth);
        RenderGlobal.drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        RenderUtils.disableRenderState();
    }

    public static void drawTileEntityBox(TileEntity tileEntity, Color color) {
        AxisAlignedBB bb;
        BlockPos pos = tileEntity.getPos();
        if (tileEntity instanceof TileEntitySign) {
            IBlockState state = Minecraft.getMinecraft().theWorld.getBlockState(pos);
            bb = state.getBlock().getSelectedBoundingBox(Minecraft.getMinecraft().theWorld, pos);
        } else {
            bb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        }
        bb = bb.expand(0.1, 0.1, 0.1);
        double[] rp = RenderUtils.getRenderPos();
        bb = bb.offset(-rp[0], -rp[1], -rp[2]);
        RenderUtils.enableRenderState();
        RenderUtils.drawFilledBox(bb, color);
        RenderUtils.disableRenderState();
    }
}

