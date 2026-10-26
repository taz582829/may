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
    private static boolean registeredShutdown = false;
    private boolean hasDoubleClicked = false;

    public ItemStorage() {
        super(Minecraft.getMinecraft().thePlayer.inventory, storage);
        ItemStorage.load();
        this.inventorySlots.windowId = -125;
        Minecraft.getMinecraft().thePlayer.openContainer = this.inventorySlots;
        if (!registeredShutdown) {
            EventBus.subscribe(new Object() {

                @SubscribeEvent
                public void onShutdown(ShutdownEvent event) {
                    ItemStorage.save();
                }
            });
            registeredShutdown = true;
        }
    }

    public static void addItem(ItemStack item) {
        for (int i = 0; i < 54; i++) {
            if (items.get(i) != null) continue;
            items.set(i, item);
            storage.setInventorySlotContents(i, item);
            break;
        }
    }

    private static File getSaveFile() {
        return new File(Entry.FILE_PATH, "items.dat");
    }

    public static void load() {
        File file = ItemStorage.getSaveFile();
        if (!file.exists()) {
            return;
        }
        try (FileInputStream is = new FileInputStream(file)) {
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);
            NBTTagList items = nbt.getTagList("Items", 10);
            for (int i = 0; i < items.tagCount(); i++) {
                NBTTagCompound item = items.getCompoundTagAt(i);
                int slot = item.getByte("Slot") & 0xFF;
                if (slot >= storage.getSizeInventory()) continue;
                storage.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
            }
        } catch (Exception e) {
            Entry.chat("§cFailed to load item storage!");
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            NBTTagCompound root = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            for (int i = 0; i < storage.getSizeInventory(); i++) {
                ItemStack stack = storage.getStackInSlot(i);
                if (stack == null) continue;
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
            root.setTag("Items", items);
            File file = ItemStorage.getSaveFile();
            try (FileOutputStream os = new FileOutputStream(file)) {
                CompressedStreamTools.writeCompressed((NBTTagCompound) root, (OutputStream) os);
            } catch (Exception e) {
                e.printStackTrace();
                Entry.chat("§cFailed to save item storage!");
            }
        } catch (Exception e) {
            Entry.chat("§cFailed to save item storage!");
        }
    }

    protected void handleMouseClick(Slot slot, int slotId, int mouseButton, int clickType) {
        if (slot == null) {
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
                    if (clickType == 1) {
                        items.set(slotId, null);
                        storage.removeStackFromSlot(slotId);
                        return;
                    }
                    if (this.hasDoubleClicked) {
                        items.set(slotId, null);
                        storage.removeStackFromSlot(slotId);
                        break;
                    }
                    this.hasDoubleClicked = true;
                    TimeUtils.setTimeout(() -> {
                        this.hasDoubleClicked = false;
                    }, 200);
                    break;
                }
                default: {
                    if (clickType == 2) {
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(mouseButton + 37, slot.getStack()));
                        break;
                    }
                    if (clickType == 3) {
                        ItemStack newStack = slot.getStack().copy();
                        newStack.stackSize = 64;
                        HousingUtils.spawnItem(newStack);
                        break;
                    } else {
                        break;
                    }
                }
            }
        } else if (clickType == 1 && mouseButton == 0) {
            ItemStorage.addItem(slot.getStack());
        }
    }

    private static class StorageInventory
            implements IInventory {
        private StorageInventory() {
        }

        public int getSizeInventory() {
            return 54;
        }

        public ItemStack getStackInSlot(int i) {
            return items.size() < i ? null : items.get(i);
        }

        public ItemStack decrStackSize(int slot, int count) {
            if (slot < 0 || slot >= items.size()) {
                return null;
            }
            ItemStack stack = items.get(slot);
            if (stack == null) {
                return null;
            }
            if (stack.stackSize <= count) {
                items.set(slot, null);
                return stack;
            }
            ItemStack result = stack.splitStack(count);
            if (stack.stackSize <= 0) {
                items.set(slot, null);
            }
            return result;
        }

        public ItemStack removeStackFromSlot(int i) {
            ItemStack original = items.get(i);
            items.set(i, null);
            return original;
        }

        public void setInventorySlotContents(int i, ItemStack item) {
            items.set(i, item);
            if (item != null && item.stackSize > this.getInventoryStackLimit()) {
                item.stackSize = this.getInventoryStackLimit();
            }
        }

        public int getInventoryStackLimit() {
            return 64;
        }

        public void markDirty() {
        }

        public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
            return true;
        }

        public void openInventory(EntityPlayer entityPlayer) {
        }

        public void closeInventory(EntityPlayer entityPlayer) {
            ItemStorage.save();
        }

        public boolean isItemValidForSlot(int i, ItemStack itemStack) {
            return true;
        }

        public int getField(int i) {
            return 0;
        }

        public void setField(int i, int i1) {
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
            return new ChatComponentText(this.getName());
        }
    }
}

