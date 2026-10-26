package lol.ethan.may.events;

import net.minecraft.inventory.Slot;

public class SlotClickEvent extends Event {
    private final Slot slot;
    private int clickedButton;
    private int clickType;

    public SlotClickEvent(Slot slot, int clickedButton, int clickType) {
        this.slot = slot;
        this.clickedButton = clickedButton;
        this.clickType = clickType;
    }

    public Slot getSlot() {
        return this.slot;
    }

    public int getClickedButton() {
        return this.clickedButton;
    }

    public void setClickedButton(int clickedButton) {
        this.clickedButton = clickedButton;
    }

    public int getClickType() {
        return this.clickType;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }
}

