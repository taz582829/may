package lol.ethan.may.hypixel;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class DenickGUI extends GuiScreen {
    @Override
    public void initGui() {
        drawDefaultBackground();
        int strWidth = mc.fontRendererObj.getStringWidth("noob");
        int centeredX = width / 2 - strWidth;
        int centeredY = height / 2 - 9;
        buttonList.add(new GuiButton(
                0,
                centeredX,
                centeredY,
                "noob"
        ));
    }
}
