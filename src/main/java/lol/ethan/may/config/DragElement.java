package lol.ethan.may.config;

import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.render.huds.HUDHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class DragElement extends GuiScreen {
    public static boolean active = false;
    BasicHud hud;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean dragging = false;
    private String text = "§aExample Text";
    private float x;
    private float y;

    public DragElement(String text, float x, float y, BasicHud hud) {
        active = true;
        this.text = text;
        this.x = x;
        this.y = y;
        this.hud = hud;
    }

    public void drawScreen(int mX, int mY, float pt) {
        this.drawDefaultBackground();
        if (this.hud.background) {
            HUDHelper.drawTextWithBackground(this.text, this.x, this.y, this.hud.getBackgroundColor());
        } else {
            HUDHelper.drawText(this.text, this.x, this.y);
        }
        int size = this.mc.fontRendererObj.getStringWidth("§8Dragging text!");
        ScaledResolution sr = new ScaledResolution(this.mc);
        HUDHelper.drawText("§8Dragging text!", sr.getScaledWidth() - size - 5, 15.0f);
    }

    public void mouseClicked(int mX, int mY, int button) {
        if (button == 0) {
            this.dragOffsetX = (float) mX - this.x;
            this.dragOffsetY = (float) mY - this.y;
            this.dragging = true;
        }
    }

    public void mouseClickMove(int mX, int mY, int button, long timeSinceLastClick) {
        if (button == 0) {
            this.x = (float) mX - this.dragOffsetX;
            this.y = (float) mY - this.dragOffsetY;
        }
    }

    protected void mouseReleased(int mX, int mY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
    }

    public void onGuiClosed() {
        active = false;
        this.hud.x = this.x;
        this.hud.y = this.y;
        Config.save();
    }

    public boolean doesGuiPauseGame() {
        return true;
    }
}

