package lol.ethan.may.render.huds;

import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicHud {
    protected final Minecraft mc = Minecraft.getMinecraft();

    public boolean enabled = true;
    public boolean background;
    public boolean shadow = true;
    public float x;
    public float y;
    public float scale = 1.0F;
    public int bgColor;
    public int bgAlpha = 50;
    public int padding = 2;
    public boolean rightAlign;
    public List<String> lineCache = new ArrayList<>();

    protected abstract List<String> getLines(boolean example);
    public boolean shouldShow() {
        return this.enabled;
    }
    public float[] overrideCoords(final float x, final float y) {
        return new float[]{x, y};
    }
    public BasicHud(final boolean enabled, final float x, final float y) {
        this.enabled = enabled;
        this.x = x;
        this.y = y;
    }
    public BasicHud(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    public BasicHud(final boolean enabled) {
        this.enabled = enabled;
    }

    public int getBackgroundColor() {
        return ColorUtils.setAlpha(this.bgColor, this.bgAlpha);
    }
    public int getTextColor() {
        return 0xFFFFFF;
    }

    /**
     * called every post tick
     */
    public void updateLineCache() {
        lineCache = this.getLines(this.mc.currentScreen instanceof ClickGUI);
    }

    public void drawText(final float x, final float y) {
        float textY = (y / this.scale) - this.padding;
        for (final String text : this.lineCache) {
            final int textWidth = this.mc.fontRendererObj.getStringWidth(text);
            final float textX = (this.rightAlign ? -this.padding - textWidth : this.padding) + (x / this.scale);
            this.mc.fontRendererObj.drawString(text, textX, textY, this.getTextColor(), this.shadow);
            textY += this.mc.fontRendererObj.FONT_HEIGHT + 1;
        }
    }
    public void draw() {
        if (!this.shouldShow()) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        if (lineCache == null || lineCache.size() == 0) return;

        GlStateManager.pushMatrix();
        final float[] coords = this.overrideCoords(x, y);
        GlStateManager.scale(this.scale, this.scale, 1.0f);
        final int padding = this.background ? this.padding : 0;
        final int bgHeight = this.lineCache.size() * 10 + padding;
        final int color = this.getBackgroundColor();
        int bgWidth = 0;
        for (final String text : this.lineCache) {
            bgWidth = Math.max(bgWidth, bgWidth + mc.fontRendererObj.getStringWidth(text));
        }
        final int left = (this.rightAlign ? -bgWidth : 0) + (int) (coords[0] / this.scale);
        bgWidth += padding;
        if (this.background) {
            Gui.drawRect(
                    left,
                    (int) (coords[1] / this.scale),
                    left + bgWidth,
                    bgHeight,
                    color
            );
        }
        this.drawText(coords[0], coords[1]);
        GlStateManager.popMatrix();
    }
}
