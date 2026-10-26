package lol.ethan.may.render.huds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

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
        return (screenWidth / 2f) - (fr.getStringWidth(text) / 2f);
    }
    public static float getCenteredY() {
        final ScaledResolution sr = new ScaledResolution(mc);

        final float screenHeight = sr.getScaledHeight();
        return (screenHeight / 2f);
    }
    public static void drawCenteredText(final String text) {
        drawCenteredText(text, getCenteredY());
    }
    public static void drawCenteredText(final String text, final float y) {
        final ScaledResolution sr = new ScaledResolution(mc);

        final float screenWidth = sr.getScaledWidth();
        final float x = (screenWidth / 2f) - (fr.getStringWidth(text) / 2f);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }
    public static void drawCenteredText(final String text, final float y, final Color color) {
        final ScaledResolution sr = new ScaledResolution(mc);

        final float screenWidth = sr.getScaledWidth();
        final float x = (screenWidth / 2f) - (fr.getStringWidth(text) / 2f);
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
}