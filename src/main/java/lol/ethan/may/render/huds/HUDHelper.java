package lol.ethan.may.render.huds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class HUDHelper {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final FontRenderer fr = mc.fontRendererObj;
    public static void drawText(final String text, final float x, final float y) {
        drawText(text, x, y, Color.WHITE);
    }
    public static void drawText(final String text, final float x, final float y, final Color color) {
        final String[] lines = text.split("\n");
        int maxWidth = 0;
        final int lineHeight = fr.FONT_HEIGHT + 2;

        for (final String line : lines) {
            maxWidth = Math.max(maxWidth, fr.getStringWidth(line));
        }

        for (int i = 0; i < lines.length; i++) {
            fr.drawStringWithShadow(lines[i], x, (y + (i * lineHeight)) + 2, color.getRGB());
        }
    }
    public static void drawTextWithBackground(final String text, final float x, final float y, final Color bgColor) {

        final String[] lines = text.split("\n");
        final int padding = 2;
        final int lineHeight = fr.FONT_HEIGHT + 2;

        int maxWidth = 0;
        for (final String line : lines) {
            maxWidth = Math.max(maxWidth, fr.getStringWidth(line));
        }
        Gui.drawRect(
                (int) x - padding,
                (int) y - padding,
                (int) x + maxWidth + padding,
                (int) y + (lines.length * lineHeight) - 2 + padding,
                bgColor.getRGB()
        );
        for (int i = 0; i < lines.length; i++) {
            fr.drawStringWithShadow(lines[i], x, (y + (i * lineHeight)) + 2, -1);
        }
    }

    public static void drawTextWithBackground(final String text, final float x, final float y, final int padding) {

        final String[] lines = text.split("\n");
        final int lineHeight = fr.FONT_HEIGHT + 2;

        int maxWidth = 0;
        for (final String line : lines) {
            final int measured = fr.getStringWidth(line);
            maxWidth = Math.max(maxWidth, measured);
        }

        Gui.drawRect(
                (int) x - padding,
                (int) y - padding,
                (int) x + maxWidth + padding,
                (int) y + (lines.length * lineHeight) - 2 + padding,
                0x40000000
        );
        for (int i = 0; i < lines.length; i++) {
            fr.drawStringWithShadow(lines[i], x, (y + (i * lineHeight)) + 2, 0xFFFFFF);
        }
    }
    public static void drawRect(final Number x, final Number y, final Number w, final Number h, final Color color) {
        Gui.drawRect(x.intValue(), y.intValue(), x.intValue() + w.intValue(), y.intValue() + h.intValue(), color.getRGB());
    }
    public static void drawRect(final Number x, final Number y, final Number w, final Number h, final Number color) {
        Gui.drawRect(x.intValue(), y.intValue(), x.intValue() + w.intValue(), y.intValue() + h.intValue(), color.intValue());
    }

    public static float getCenteredX(final String text) {
        final ScaledResolution sr = new ScaledResolution(mc);

        final float screenWidth = sr.getScaledWidth();
        return (screenWidth / 2.0f) - (fr.getStringWidth(text) / 2.0f);
    }
    public static float getCenteredY() {
        final ScaledResolution sr = new ScaledResolution(mc);

        final float screenHeight = sr.getScaledHeight();
        return (screenHeight / 2.0f);
    }
    public static void drawCenteredText(final String text) {
        drawCenteredText(text, getCenteredY());
    }
    public static void drawCenteredText(final String text, final float y) {
        final ScaledResolution sr = new ScaledResolution(mc);

        final float screenWidth = sr.getScaledWidth();
        final float x = (screenWidth / 2.0f) - (fr.getStringWidth(text) / 2.0f);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }
    public static void drawCenteredText(final String text, final float y, final Color color) {
        final ScaledResolution sr = new ScaledResolution(mc);

        final float screenWidth = sr.getScaledWidth();
        final float x = (screenWidth / 2.0f) - (fr.getStringWidth(text) / 2.0f);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y, color.getRGB());
    }
    public static int getTextWidth(final String text) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }

    public static int getHUDWidth(final String[] lines) {
        int maxWidth = 0;
        for (final String line : lines) {
            final int measured = fr.getStringWidth(line);
            maxWidth = Math.max(maxWidth, measured);
        }
        return maxWidth;
    }
    public static int getHUDWidth(final String line) {
        return getTextWidth(line);
    }
    public static int getFontHeight() {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
    public static void drawRoundedRect(double left, double top, double right, double bottom, final double radius, final int color) {
        final float alpha = (color >> 24 & 255) / 255.0F;
        final float red = (color >> 16 & 255) / 255.0F;
        final float green = (color >> 8 & 255) / 255.0F;
        final float blue = (color & 255) / 255.0F;

        if (left < right) {
            final double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            final double j = top;
            top = bottom;
            bottom = j;
        }

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_TRUE, GL_FALSE);
        GlStateManager.color(red, green, blue, alpha);

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer wr = tessellator.getWorldRenderer();

        wr.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);

        wr.pos(left + (right - left) / 2.0D, top + (bottom - top) / 2.0D, 0.0D).endVertex();

        for (int i = 0; i <= 90; i += 3) {
            wr.pos(right - radius + Math.sin(Math.toRadians(i)) * radius, top + radius - Math.cos(Math.toRadians(i)) * radius, 0.0D).endVertex();
        }
        for (int i = 90; i <= 180; i += 3) {
            wr.pos(right - radius + Math.sin(Math.toRadians(i)) * radius, bottom - radius - Math.cos(Math.toRadians(i)) * radius, 0.0D).endVertex();
        }
        for (int i = 180; i <= 270; i += 3) {
            wr.pos(left + radius + Math.sin(Math.toRadians(i)) * radius, bottom - radius - Math.cos(Math.toRadians(i)) * radius, 0.0D).endVertex();
        }
        for (int i = 270; i <= 360; i += 3) {
            wr.pos(left + radius + Math.sin(Math.toRadians(i)) * radius, top + radius - Math.cos(Math.toRadians(i)) * radius, 0.0D).endVertex();
        }

        tessellator.draw();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}