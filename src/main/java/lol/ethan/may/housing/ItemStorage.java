package lol.ethan.may.housing;

import lol.ethan.may.Entry;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.ShutdownEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemStorage extends GuiChest {
    private static final StorageInventory storage = new StorageInventory();
    private static final List<ItemStack> items = new ArrayList<>(Collections.nCopies(54, null));
    private static boolean registeredShutdown;
    private boolean hasDoubleClicked;

    public ItemStorage() {
        super(Minecraft.getMinecraft().thePlayer.inventory, storage);
        ItemStorage.load();
        inventorySlots.windowId = -125;
        Minecraft.getMinecraft().thePlayer.openContainer = inventorySlots;
        if (!registeredShutdown) {
            EventBus.subscribe(new Object() {

                @SubscribeEvent
                public void onShutdown(final ShutdownEvent event) {
                    ItemStorage.save();
                }
            });
            registeredShutdown = true;
        }
    }

    public static void addItem(final ItemStack item) {
        for (int i = 0; 54 > i; i++) {
            if (null != items.get(i)) continue;
            items.set(i, item);
            storage.setInventorySlotContents(i, item);
            break;
        }
    }

    private static File getSaveFile() {
        return new File(Entry.FILE_PATH, "items.dat");
    }

    public static void load() {
        final File file = ItemStorage.getSaveFile();
        if (!file.exists()) {
            return;
        }
        try (final FileInputStream is = new FileInputStream(file)) {
            final NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);
            final NBTTagList items = nbt.getTagList("Items", 10);
            for (int i = 0; i < items.tagCount(); i++) {
                final NBTTagCompound item = items.getCompoundTagAt(i);
                final int slot = item.getByte("Slot") & 0xFF;
                if (slot >= storage.getSizeInventory()) continue;
                storage.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
            }
        } catch (final Exception e) {
            Entry.chat("§cFailed to load item storage!");
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            final NBTTagCompound root = new NBTTagCompound();
            final NBTTagList items = new NBTTagList();
            for (int i = 0; i < storage.getSizeInventory(); i++) {
                final ItemStack stack = storage.getStackInSlot(i);
                if (null == stack) continue;
                final NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
            root.setTag("Items", items);
            final File file = ItemStorage.getSaveFile();
            try (final FileOutputStream os = new FileOutputStream(file)) {
                CompressedStreamTools.writeCompressed((NBTTagCompound) root, (OutputStream) os);
            } catch (final Exception e) {
                e.printStackTrace();
                Entry.chat("§cFailed to save item storage!");
            }
        } catch (final Exception e) {
            Entry.chat("§cFailed to save item storage!");
        }
    }

    protected void handleMouseClick(final Slot slot, final int slotId, final int mouseButton, final int clickType) {
        if (null == slot) {
            return;
        }
        Entry.playClickSound();
        if (slot.inventory.equals(storage)) {
            switch (mouseButton) {
                case 0: {
                    HousingUtils.spawnItem(slot.getStack());
                    break;
                }
                case 1: {
                    if (1 == clickType) {
                        items.set(slotId, null);
                        storage.removeStackFromSlot(slotId);
                        return;
                    }
                    if (hasDoubleClicked) {
                        items.set(slotId, null);
                        storage.removeStackFromSlot(slotId);
                        break;
                    }
                    hasDoubleClicked = true;
                    TimeUtils.setTimeout(() -> hasDoubleClicked = false, 200);
                    break;
                }
                default: {
                    if (2 == clickType) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(mouseButton + 37, slot.getStack()));
                        break;
                    }
                    if (3 == clickType) {
                        final ItemStack newStack = slot.getStack().copy();
                        newStack.stackSize = 64;
                        HousingUtils.spawnItem(newStack);
                    }
                    break;
                }
            }
        } else if (1 == clickType && 0 == mouseButton) {
            ItemStorage.addItem(slot.getStack());
        }
    }

    private static class StorageInventory implements IInventory {
        private StorageInventory() {
        }

        public int getSizeInventory() {
            return 54;
        }

        public ItemStack getStackInSlot(final int i) {
            return items.size() < i ? null : items.get(i);
        }

        public ItemStack decrStackSize(final int slot, final int count) {
            if (0 > slot || slot >= items.size()) {
                return null;
            }
            final ItemStack stack = items.get(slot);
            if (null == stack) {
                return null;
            }
            if (stack.stackSize <= count) {
                items.set(slot, null);
                return stack;
            }
            final ItemStack result = stack.splitStack(count);
            if (0 >= stack.stackSize) {
                items.set(slot, null);
            }
            return result;
        }

        public ItemStack removeStackFromSlot(final int i) {
            final ItemStack original = items.get(i);
            items.set(i, null);
            return original;
        }

        public void setInventorySlotContents(final int i, final ItemStack item) {
            items.set(i, item);
            if (null != item && item.stackSize > getInventoryStackLimit()) {
                item.stackSize = getInventoryStackLimit();
            }
        }

        public int getInventoryStackLimit() {
            return 64;
        }

        public void markDirty() {
        }

        public boolean isUseableByPlayer(final EntityPlayer entityPlayer) {
            return true;
        }

        public void openInventory(final EntityPlayer entityPlayer) {
        }

        public void closeInventory(final EntityPlayer entityPlayer) {
            ItemStorage.save();
        }

        public boolean isItemValidForSlot(final int i, final ItemStack itemStack) {
            return true;
        }

        public int getField(final int i) {
            return 0;
        }

        public void setField(final int i, final int i1) {
        }

        public int getFieldCount() {
            return 0;
        }

        public void clear() {
            items.clear();
        }

        public String getName() {
            return "Item Storage";
        }

        public boolean hasCustomName() {
            return true;
        }

        public IChatComponent getDisplayName() {
            return new ChatComponentText(getName());
        }
    }
}

