package lol.ethan.may.config;

import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.render.huds.HUDHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class DragElement extends GuiScreen {
    public static boolean active;
    BasicHud hud;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean dragging;
    private String text = "§aExample Text";
    private float x;
    private float y;

    public DragElement(final String text, final float x, final float y, final BasicHud hud) {
        active = true;
        this.text = text;
        this.x = x;
        this.y = y;
        this.hud = hud;
    }

    public void drawScreen(final int mX, final int mY, final float pt) {
        drawDefaultBackground();
        if (hud.background) {
            HUDHelper.drawTextWithBackground(text, x, y, hud.getBackgroundColor());
        } else {
            HUDHelper.drawText(text, x, y);
        }
        final int size = mc.fontRendererObj.getStringWidth("§8Dragging text!");
        final ScaledResolution sr = new ScaledResolution(mc);
        HUDHelper.drawText("§8Dragging text!", sr.getScaledWidth() - size - 5, 15.0f);
    }

    public void mouseClicked(final int mX, final int mY, final int button) {
        if (button == 0) {
            dragOffsetX = (float) mX - x;
            dragOffsetY = (float) mY - y;
            dragging = true;
        }
    }

    public void mouseClickMove(final int mX, final int mY, final int button, final long timeSinceLastClick) {
        if (button == 0) {
            x = (float) mX - dragOffsetX;
            y = (float) mY - dragOffsetY;
        }
    }

    protected void mouseReleased(final int mX, final int mY, final int state) {
        if (state == 0) {
            dragging = false;
        }
    }

    public void onGuiClosed() {
        active = false;
        hud.x = x;
        hud.y = y;
        Config.save();
    }

    public boolean doesGuiPauseGame() {
        return true;
    }
}

