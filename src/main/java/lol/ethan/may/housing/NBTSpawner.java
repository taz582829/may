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
        int fieldWidth = 200;
        int fieldHeight = 11;
        this.textField = new GuiTextField(0, this.fontRendererObj, (this.width - fieldWidth) / 2, (this.height - fieldHeight) / 2, fieldWidth, fieldHeight);
        this.textField.setMaxStringLength(Integer.MAX_VALUE);
        this.textField.setFocused(true);
        if (this.mc.thePlayer.getHeldItem() != null) {
            NBTTagCompound nbt = ItemUtils.getFullNBT(this.mc.thePlayer.getHeldItem());
            this.textField.setText(nbt.toString());
        }
    }

    public void updateScreen() {
        this.textField.updateCursorCounter();
    }

    public void drawScreen(int x, int y, float pt) {
        this.drawDefaultBackground();
        this.textField.drawTextBox();
        super.drawScreen(x, y, pt);
    }

    protected void keyTyped(char c, int keyCode) {
        if (keyCode == 1 || keyCode == 28) {
            Entry.playClickSound();
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        this.textField.textboxKeyTyped(c, keyCode);
    }

    protected void mouseClicked(int i, int i1, int i2) throws IOException {
        super.mouseClicked(i, i1, i2);
        this.textField.mouseClicked(i, i1, i2);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed() {
        try {
            NBTTagCompound nbt = JsonToNBT.getTagFromJson(this.textField.getText().replaceAll("(?i)&(\\w)", "§$1"));
            HousingUtils.spawnItem(ItemStack.loadItemStackFromNBT(nbt));
        } catch (NBTException nBTException) {
        }
    }
}

