package lol.ethan.may.render.huds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class HUDHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final FontRenderer fr = HUDHelper.mc.fontRendererObj;

    public static void drawText(String text, float x, float y) {
        HUDHelper.drawText(text, x, y, Color.WHITE);
    }

    public static void drawText(String text, float x, float y, Color color) {
        String[] lines = text.split("\n");
        int maxWidth = 0;
        int lineHeight = HUDHelper.fr.FONT_HEIGHT + 2;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, fr.getStringWidth(line));
        }
        for (int i = 0; i < lines.length; ++i) {
            fr.drawStringWithShadow(lines[i], x, y + (float) (i * lineHeight) + 2.0f, color.getRGB());
        }
    }

    public static void drawTextWithBackground(String text, float x, float y, Color bgColor) {
        String[] lines = text.split("\n");
        int padding = 2;
        int lineHeight = HUDHelper.fr.FONT_HEIGHT + 2;
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, fr.getStringWidth(line));
        }
        Gui.drawRect((int) x - padding, (int) y - padding, (int) x + maxWidth + padding, (int) y + lines.length * lineHeight - 2 + padding, bgColor.getRGB());
        for (int i = 0; i < lines.length; ++i) {
            fr.drawStringWithShadow(lines[i], x, y + (float) (i * lineHeight) + 2.0f, -1);
        }
    }

    public static void drawTextWithBackground(String text, float x, float y, int padding) {
        String[] lines = text.split("\n");
        int lineHeight = HUDHelper.fr.FONT_HEIGHT + 2;
        int maxWidth = 0;
        for (String line : lines) {
            int measured = fr.getStringWidth(line);
            maxWidth = Math.max(maxWidth, measured);
        }
        Gui.drawRect((int) x - padding, (int) y - padding, (int) x + maxWidth + padding, (int) y + lines.length * lineHeight - 2 + padding, 0x40000000);
        for (int i = 0; i < lines.length; ++i) {
            fr.drawStringWithShadow(lines[i], x, y + (float) (i * lineHeight) + 2.0f, 0xFFFFFF);
        }
    }

    public static void drawRect(Number x, Number y, Number w, Number h, Color color) {
        Gui.drawRect(x.intValue(), y.intValue(), x.intValue() + w.intValue(), y.intValue() + h.intValue(), color.getRGB());
    }

    public static void drawRect(Number x, Number y, Number w, Number h, Number color) {
        Gui.drawRect(x.intValue(), y.intValue(), x.intValue() + w.intValue(), y.intValue() + h.intValue(), color.intValue());
    }

    public static float getCenteredX(String text) {
        ScaledResolution sr = new ScaledResolution(mc);
        float screenWidth = sr.getScaledWidth();
        return screenWidth / 2.0f - (float) fr.getStringWidth(text) / 2.0f;
    }

    public static float getCenteredY() {
        ScaledResolution sr = new ScaledResolution(mc);
        float screenHeight = sr.getScaledHeight();
        return screenHeight / 2.0f;
    }

    public static void drawCenteredText(String text) {
        HUDHelper.drawCenteredText(text, HUDHelper.getCenteredY());
    }

    public static void drawCenteredText(String text, float y) {
        ScaledResolution sr = new ScaledResolution(mc);
        float screenWidth = sr.getScaledWidth();
        float x = screenWidth / 2.0f - (float) fr.getStringWidth(text) / 2.0f;
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }

    public static void drawCenteredText(String text, float y, Color color) {
        ScaledResolution sr = new ScaledResolution(mc);
        float screenWidth = sr.getScaledWidth();
        float x = screenWidth / 2.0f - (float) fr.getStringWidth(text) / 2.0f;
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y, color.getRGB());
    }

    public static int getTextWidth(String text) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }

    public static int getHUDWidth(String[] lines) {
        int maxWidth = 0;
        for (String line : lines) {
            int measured = fr.getStringWidth(line);
            maxWidth = Math.max(maxWidth, measured);
        }
        return maxWidth;
    }

    public static int getHUDWidth(String line) {
        return HUDHelper.getTextWidth(line);
    }

    public static int getFontHeight() {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
}

