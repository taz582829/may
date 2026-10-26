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
    public boolean background = false;
    public boolean shadow = true;
    public float x;
    public float y;
    public float scale = 1.0f;
    public int bgColor = 0;
    public int bgAlpha = 50;
    public int padding = 2;
    public boolean rightAlign = false;
    public List<String> lineCache = new ArrayList<>();

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

    protected abstract List<String> getLines(boolean var1);

    public boolean shouldShow() {
        return this.enabled;
    }

    public float[] overrideCoords(float x, float y) {
        return new float[]{x, y};
    }

    public int getBackgroundColor() {
        return ColorUtils.setAlpha(this.bgColor, this.bgAlpha);
    }

    public int getTextColor() {
        return 0xFFFFFF;
    }

    public void updateLineCache() {
        this.lineCache = this.getLines(this.mc.currentScreen instanceof ClickGUI);
    }

    public void drawText(float x, float y) {
        float textY = y / this.scale - (float) this.padding;
        for (String text : this.lineCache) {
            int textWidth = this.mc.fontRendererObj.getStringWidth(text);
            float textX = (float) (this.rightAlign ? -this.padding - textWidth : this.padding) + x / this.scale;
            this.mc.fontRendererObj.drawString(text, textX, textY, this.getTextColor(), this.shadow);
            textY += (float) (this.mc.fontRendererObj.FONT_HEIGHT + 1);
        }
    }

    private int[] getRawDimensions() {
        int pad = this.background ? this.padding : 0;
        int h = this.lineCache.size() * 10 + pad;
        int w = 0;
        for (String text : this.lineCache) {
            w = Math.max(w, this.mc.fontRendererObj.getStringWidth(text));
        }
        return new int[]{w + pad * 2, h};
    }

    public int[] getDimensions() {
        int[] raw = this.getRawDimensions();
        return new int[]{(int) ((float) raw[0] * this.scale), (int) ((float) raw[1] * this.scale)};
    }

    public void draw() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            return;
        }
        if (this.lineCache == null || this.lineCache.size() == 0) {
            return;
        }
        GlStateManager.pushMatrix();
        float[] coords = this.overrideCoords(this.x, this.y);
        GlStateManager.scale(this.scale, this.scale, 1.0f);
        int color = this.getBackgroundColor();
        int[] dims = this.getDimensions();
        int bgWidth = dims[0];
        int bgHeight = dims[1];
        int left = (this.rightAlign ? -bgWidth : 0) + (int) (coords[0] / this.scale);
        if (this.background) {
            Gui.drawRect(left, (int) (coords[1] / this.scale), left + bgWidth, bgHeight, color);
        }
        this.drawText(coords[0], coords[1]);
        GlStateManager.popMatrix();
    }
}

