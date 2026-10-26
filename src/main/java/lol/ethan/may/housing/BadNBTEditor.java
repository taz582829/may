package lol.ethan.may.housing;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;

public class BadNBTEditor extends GuiScreen {
    private final ScaledResolution sr;
    private final ItemStack currentItem;
    private GuiButton loadFromName;
    private GuiTextField loadFromName_player;

    public BadNBTEditor(ItemStack currentItem) {
        this.sr = new ScaledResolution(this.mc);
        this.currentItem = currentItem;
    }

    public void drawScreen(int i, int i1, float v) {
        if (this.currentItem == null) {
            return;
        }
        if (this.currentItem.getItem() == Items.skull) {
            ItemSkull skull = (ItemSkull) this.currentItem.getItem();
            this.loadFromName = new GuiButton(1, this.sr.getScaledWidth() / 2 - 50, (int) ((double) this.sr.getScaledHeight() / 1.5), 50, 20, "Load");
            this.buttonList.add(this.loadFromName);
        }
    }

    private enum SpecialItems {
        HEAD,
        POTION

    }
}

