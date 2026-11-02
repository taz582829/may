package lol.ethan.may.events.gui;

import lol.ethan.may.events.Event;
import net.minecraft.client.gui.GuiScreen;

public class GuiOpenEvent extends Event {
    private GuiScreen screen;

    public GuiOpenEvent(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }
}
