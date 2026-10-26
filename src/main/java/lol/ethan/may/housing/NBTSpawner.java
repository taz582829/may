package lol.ethan.may.housing;

import lol.ethan.may.Entry;
import lol.ethan.may.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class NBTSpawner extends GuiScreen {
    private GuiTextField textField;

    public void initGui() {
        final int fieldWidth = 200;
        final int fieldHeight = 11;
        textField = new GuiTextField(0, fontRendererObj, (width - fieldWidth) / 2, (height - fieldHeight) / 2, fieldWidth, fieldHeight);
        textField.setMaxStringLength(Integer.MAX_VALUE);
        textField.setFocused(true);
        if (null != this.mc.thePlayer.getHeldItem()) {
            final NBTTagCompound nbt = ItemUtils.getFullNBT(mc.thePlayer.getHeldItem());
            textField.setText(nbt.toString());
        }
    }

    public void updateScreen() {
        textField.updateCursorCounter();
    }

    public void drawScreen(final int x, final int y, final float pt) {
        drawDefaultBackground();
        textField.drawTextBox();
        super.drawScreen(x, y, pt);
    }

    protected void keyTyped(final char c, final int keyCode) {
        if (1 == keyCode || 28 == keyCode) {
            Entry.playClickSound();
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        textField.textboxKeyTyped(c, keyCode);
    }

    protected void mouseClicked(final int i, final int i1, final int i2) throws IOException {
        super.mouseClicked(i, i1, i2);
        textField.mouseClicked(i, i1, i2);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed() {
        try {
            final NBTTagCompound nbt = JsonToNBT.getTagFromJson(textField.getText().replaceAll("(?i)&(\\w)", "§$1"));
            HousingUtils.spawnItem(ItemStack.loadItemStackFromNBT(nbt));
        } catch (final NBTException nBTException) {
        }
    }
}

