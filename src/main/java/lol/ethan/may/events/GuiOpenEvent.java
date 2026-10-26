package lol.ethan.may.events;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;

public class GuiOpenEvent extends Event {
    private GuiScreen screen;

    public GuiOpenEvent(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return this.screen;
    }

    public void setScreen(GuiScreen screen) {
        this.screen = screen;
    }

    public String getTitle() {
        if (!(this.screen instanceof GuiContainer)) {
            return null;
        }
        ContainerChest containerChest = (ContainerChest) ((GuiContainer) this.screen).inventorySlots;
        return containerChest.getLowerChestInventory().getDisplayName().getFormattedText();
    }
}

