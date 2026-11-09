package lol.ethan.may.events.gui;

import lol.ethan.may.events.Event;
import net.minecraft.client.gui.GuiScreen;

public class GuiOpenEvent extends Event {
    private final GuiScreen screen;

    public GuiOpenEvent(final GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }
}
