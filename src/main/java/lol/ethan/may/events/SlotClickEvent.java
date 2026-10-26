package lol.ethan.may.events;

import net.minecraft.inventory.Slot;

public class SlotClickEvent extends Event {
    private final Slot slot;
    private int clickedButton;
    private int clickType;

    public SlotClickEvent(final Slot slot, final int clickedButton, final int clickType) {
        this.slot = slot;
        this.clickedButton = clickedButton;
        this.clickType = clickType;
    }

    public Slot getSlot() {
        return slot;
    }

    public int getClickedButton() {
        return clickedButton;
    }

    public void setClickedButton(final int clickedButton) {
        this.clickedButton = clickedButton;
    }

    public int getClickType() {
        return clickType;
    }

    public void setClickType(final int clickType) {
        this.clickType = clickType;
    }
}

