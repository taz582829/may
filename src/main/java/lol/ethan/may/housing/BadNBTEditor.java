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

    public BadNBTEditor(final ItemStack currentItem) {
        sr = new ScaledResolution(mc);
        this.currentItem = currentItem;
    }

    public void drawScreen(final int i, final int i1, final float v) {
        if (null == this.currentItem) {
            return;
        }
        if (currentItem.getItem() == Items.skull) {
            final ItemSkull skull = (ItemSkull) currentItem.getItem();
            loadFromName = new GuiButton(1, sr.getScaledWidth() / 2 - 50, (int) ((double) sr.getScaledHeight() / 1.5), 50, 20, "Load");
            buttonList.add(loadFromName);
        }
    }

    private enum SpecialItems {
        HEAD,
        POTION

    }
}

