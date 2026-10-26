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
    private ResourceLocation texture;
    private int selectedColor = 65527;
    private int hoveredColor = -1;
    private final DynamicTexture dynamicTexture;
    private final Consumer<Integer> consumer;

    public ColorWheel(final Consumer<Integer> consumer) {
        this.consumer = consumer;
        try (final InputStream in = Entry.getResourceAsStream("assets/may/picker.png")) {
            image = ImageIO.read(in);
            dynamicTexture = new DynamicTexture(image);
            texture = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("color_picker", dynamicTexture);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getX() {
        return (width - 200) / 2;
    }

    private int getY() {
        return (height - 200) / 2;
    }

    public void drawScreen(final int i, final int i1, final float v) {
        if (null == this.texture || null == this.image) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.drawScaledCustomSizeModalRect(getX(), getY(), 0.0f, 0.0f, image.getWidth(), image.getHeight(), 200, 200, (float) image.getWidth(), (float) image.getHeight());
        pick(i, i1);
        final String hex = String.format("#%06X", hoveredColor);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(hex, (float) getX() + (float) (200 - fontRendererObj.getStringWidth(hex)) / 2.0f, (float) (getY() - 10), hoveredColor);
    }

    protected void mouseClicked(final int i, final int i1, final int i2) {
        selectedColor = pick(i, i1);
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        dynamicTexture.deleteGlTexture();
        consumer.accept(selectedColor);
    }

    public int pick(final int mouseX, final int mouseY) {
        final int x = getX();
        final int y = getY();
        if (mouseX < x || mouseY < y || mouseX >= x + 200 || mouseY >= y + 200) {
            return hoveredColor;
        }
        final int offsetY = mouseY - y;
        if (4 > offsetY) {
            hoveredColor = 0;
        } else if (196 <= offsetY) {
            hoveredColor = 0xFFFFFF;
        } else {
            final int px = snap(mouseX - x, image.getWidth());
            final int py = snap(offsetY, image.getHeight());
            hoveredColor = image.getRGB(px, py) & 0xFFFFFF;
        }
        return hoveredColor;
    }

    private int snap(final int offset, final int max) {
        final float t = (float) (offset - 4) / 191.0f;
        final int p = Math.round(t * (float) (max - 1));
        return Math.max(0, Math.min(max - 1, p));
    }
}

