package lol.ethan.may.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ItemUtils {

    public static List<String> getLore(final ItemStack item) {
        if (item == null) {
            return Collections.emptyList();
        }
        return item.getTooltip(Minecraft.getMinecraft().thePlayer, true);
    }

    public static List<String> getLore(final ItemStack item, final boolean advancedTooltips) {
        if (item == null) {
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
    public static String getGiveCode(ItemStack itemStack) {
        boolean a = itemStack.getTagCompound() != null && !itemStack.getTagCompound().hasNoTags();
        boolean b = itemStack.getMetadata() == 0 && !a;
        return Item.itemRegistry.getNameForObject(itemStack.getItem()).toString()
          + ((itemStack.stackSize == 1 || itemStack.stackSize == 0) && b ? ""
             : " " + itemStack.stackSize
               + (b ? ""
                  : " " + itemStack.getMetadata()
                    + (a ? " " + itemStack.getTagCompound().toString() : "")));
    }
    public static ItemStack getFromGiveCode(String code) {
        if (code == null || code.isEmpty())
            return null;
        String[] args = code.split(" ", 4);
        Item item = Item.itemRegistry.getObject(new ResourceLocation(args[0]));
        if (item != null) {
            int i;
            try {
                i = args.length >= 2 ? CommandBase.parseInt(args[1]) : 1;
            } catch (NumberInvalidException e1) {
                i = 1;
            }
            int j;
            try {
                j = args.length >= 3 ? CommandBase.parseInt(args[2]) : 0;
            } catch (NumberInvalidException e1) {
                j = 0;
            }
            ItemStack itemstack = new ItemStack(item, i, j);

            if (args.length == 4) {
                try {
                    itemstack.setTagCompound(
                            ItemUtils.toJson(args[3].replace("&",  "§")));
                } catch (NBTException e) {
                }
            }
            return itemstack;
        }
        return null;
    }

    public static NBTTagCompound toJson(String json) throws NBTException {
        return validate(JsonToNBT.getTagFromJson(json));
    }

    /**
     * Remove " of keys
     */
    public static NBTTagCompound validate(NBTTagCompound tag) throws NBTException {
        NBTTagCompound newTag = new NBTTagCompound();
        tag.getKeySet().forEach(key -> {
            NBTBase base = tag.getTag(key);
            if (base instanceof NBTTagCompound)
                try {
                    base = validate((NBTTagCompound) base);
                } catch (NBTException e) {
                }
            else if (base instanceof NBTTagList) {
                base = validate((NBTTagList) base);
            }
            newTag.setTag(key.startsWith("\"") && key.endsWith("\"") ? key.substring(1, key.length() - 1) : key, base);
        });
        return newTag;
    }

    private static NBTTagList validate(NBTTagList tag) {
        if (tag.getTagType() == 9) { // list
            NBTTagList nlist = new NBTTagList();
            forEachInNBTTagList(tag, base -> nlist.appendTag(validate((NBTTagList) base)));
            return nlist;
        } else if (tag.getTagType() == 10) { // tag
            NBTTagList nlist = new NBTTagList();
            forEachInNBTTagList(tag, base -> {
                try {
                    nlist.appendTag(validate((NBTTagCompound) base));
                } catch (NBTException e) {
                    nlist.appendTag(base);
                }
            });
            return nlist;
        } else
            return tag;
    }
    public static void forEachInNBTTagList(NBTTagList list, Consumer<NBTBase> consumer) {
        for (int i = 0; i < list.tagCount(); i++)
            consumer.accept(list.get(i));
    }
}

