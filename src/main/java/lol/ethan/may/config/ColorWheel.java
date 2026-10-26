package lol.ethan.may.config;

import lol.ethan.may.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class ColorWheel extends GuiScreen {
    private final BufferedImage image;
    private final int size = 200;
    private ResourceLocation texture = null;
    private int selectedColor = 65527;
    private int hoveredColor = -1;
    private final DynamicTexture dynamicTexture;
    private final Consumer<Integer> consumer;

    public ColorWheel(Consumer<Integer> consumer) {
        this.consumer = consumer;
        try (InputStream in = Entry.getResourceAsStream("assets/may/picker.png")) {
            this.image = ImageIO.read(in);
            this.dynamicTexture = new DynamicTexture(this.image);
            this.texture = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("color_picker", this.dynamicTexture);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getX() {
        return (this.width - 200) / 2;
    }

    private int getY() {
        return (this.height - 200) / 2;
    }

    public void drawScreen(int i, int i1, float v) {
        if (this.texture == null || this.image == null) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.drawScaledCustomSizeModalRect(this.getX(), this.getY(), 0.0f, 0.0f, this.image.getWidth(), this.image.getHeight(), 200, 200, (float) this.image.getWidth(), (float) this.image.getHeight());
        this.pick(i, i1);
        String hex = String.format("#%06X", this.hoveredColor);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(hex, (float) this.getX() + (float) (200 - this.fontRendererObj.getStringWidth(hex)) / 2.0f, (float) (this.getY() - 10), this.hoveredColor);
    }

    protected void mouseClicked(int i, int i1, int i2) {
        this.selectedColor = this.pick(i, i1);
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.dynamicTexture.deleteGlTexture();
        this.consumer.accept(this.selectedColor);
    }

    public int pick(int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY();
        if (mouseX < x || mouseY < y || mouseX >= x + 200 || mouseY >= y + 200) {
            return this.hoveredColor;
        }
        int offsetY = mouseY - y;
        if (offsetY < 4) {
            this.hoveredColor = 0;
        } else if (offsetY >= 196) {
            this.hoveredColor = 0xFFFFFF;
        } else {
            int px = this.snap(mouseX - x, this.image.getWidth());
            int py = this.snap(offsetY, this.image.getHeight());
            this.hoveredColor = this.image.getRGB(px, py) & 0xFFFFFF;
        }
        return this.hoveredColor;
    }

    private int snap(int offset, int max) {
        float t = (float) (offset - 4) / 191.0f;
        int p = Math.round(t * (float) (max - 1));
        return Math.max(0, Math.min(max - 1, p));
    }
}

