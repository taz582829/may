package lol.ethan.may.render.huds;

import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicHud {
    protected final Minecraft mc = Minecraft.getMinecraft();

    public boolean enabled = true;
    public boolean background = false;
    public boolean shadow = true;
    public float x;
    public float y;
    public float scale = 1.0F;
    public int bgColor = 0;
    public int bgAlpha = 50;
    public int padding = 2;
    public boolean rightAlign = false;
    public transient List<String> lineCache = new ArrayList<>();

    protected abstract List<String> getLines(boolean example);
    public boolean shouldShow() {
        return enabled;
    }
    public float[] overrideCoords(float x, float y) {
        return new float[]{x, y};
    }
    public BasicHud(boolean enabled, float x, float y) {
        this.enabled = enabled;
        this.x = x;
        this.y = y;
    }
    public BasicHud(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public BasicHud(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBackgroundColor() {
        return ColorUtils.setAlpha(bgColor, bgAlpha);
    }
    public int getTextColor() {
        return 0xFFFFFF;
    }

    /**
     * called every post tick
     */
    public void updateLineCache() {
        this.lineCache = getLines(mc.currentScreen instanceof ClickGUI);
    }

    public void drawText(float x, float y) {
        float textY = (y / scale) - padding;
        for (String text : lineCache) {
            int textWidth = mc.fontRendererObj.getStringWidth(text);
            float textX = (rightAlign ? -padding - textWidth : padding) + (x / scale);
            mc.fontRendererObj.drawString(text, textX, textY, getTextColor(), shadow);
            textY += mc.fontRendererObj.FONT_HEIGHT + 1;
        }
    }
    public void draw() {
        if (!shouldShow()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        if (lineCache == null || lineCache.size() == 0) return;

        GlStateManager.pushMatrix();
        float[] coords = overrideCoords(this.x, this.y);
        GlStateManager.scale(scale, scale, 1.0f);
        int padding = background ? this.padding : 0;
        int bgHeight = lineCache.size() * 10 + padding;
        int color = getBackgroundColor();
        int bgWidth = 0;
        for (String text : lineCache) {
            bgWidth = Math.max(bgWidth, bgWidth + mc.fontRendererObj.getStringWidth(text));
        }
        int left = (rightAlign ? -bgWidth : 0) + (int) (coords[0] / scale);
        bgWidth += padding;
        if (background) {
            Gui.drawRect(
                    left,
                    (int) (coords[1] / scale),
                    left + bgWidth,
                    bgHeight,
                    color
            );
        }
        drawText(coords[0], coords[1]);
        GlStateManager.popMatrix();
    }
}
