package lol.ethan.may.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.Collections;
import java.util.List;

public class ItemUtils {

    public static List<String> getLore(final ItemStack item) {
        if (null == item) {
            return Collections.emptyList();
        }
        return item.getTooltip(Minecraft.getMinecraft().thePlayer, true);
    }

    public static List<String> getLore(final ItemStack item, final boolean advancedTooltips) {
        if (null == item) {
            return Collections.emptyList();
        }
        return item.getTooltip(Minecraft.getMinecraft().thePlayer, advancedTooltips);
    }

    public static NBTTagCompound getFullNBT(final ItemStack item) {
        final NBTTagCompound base = new NBTTagCompound();
        item.writeToNBT(base);
        return base;
    }

    public static void setLore(final ItemStack item, final String line) {
        final NBTTagCompound display = new NBTTagCompound();
        final NBTTagList lore = new NBTTagList();
        for (final String part : line.split("\\n")) {
            lore.appendTag(new NBTTagString(part));
        }
        display.setTag("Lore", lore);
        item.setTagInfo("display", display);
    }

    public static void setName(final ItemStack item, final String name) {
        final NBTTagCompound display = new NBTTagCompound();
        final NBTTagString nbtName = new NBTTagString(name);
        display.setTag("Name", nbtName);
        item.setTagInfo("display", display);
    }
}

