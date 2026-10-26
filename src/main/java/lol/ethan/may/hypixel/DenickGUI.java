package lol.ethan.may.hypixel;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class DenickGUI extends GuiScreen {
    @Override
    public void initGui() {
        this.drawDefaultBackground();
        final int strWidth = this.mc.fontRendererObj.getStringWidth("noob");
        final int centeredX = this.width / 2 - strWidth;
        final int centeredY = this.height / 2 - 9;
        this.buttonList.add(new GuiButton(
                0,
                centeredX,
                centeredY,
                "noob"
        ));
    }
}
