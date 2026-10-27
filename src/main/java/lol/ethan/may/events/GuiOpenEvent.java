package lol.ethan.may.events;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;

public class GuiOpenEvent extends Event {
    private GuiScreen screen;

    public GuiOpenEvent(final GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }

    public void setScreen(final GuiScreen screen) {
        this.screen = screen;
    }

    public String getTitle() {
        if (!(screen instanceof GuiContainer)) {
            return null;
        }
        final ContainerChest containerChest = (ContainerChest) ((GuiContainer) screen).inventorySlots;
        return containerChest.getLowerChestInventory().getDisplayName().getFormattedText();
    }
}

