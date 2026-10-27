package lol.ethan.may.utils;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.List;
import java.util.function.Predicate;

public class GuiUtils {
    public static boolean cancelMovement;

    public static String getTitle(final Container container) {
        if (!(container instanceof ContainerChest)) {
            return null;
        }
        final ContainerChest containerChest = (ContainerChest) container;
        return containerChest.getLowerChestInventory().getDisplayName().getFormattedText();
    }

    public static String getTitle() {
        return GuiUtils.getTitle(Minecraft.getMinecraft().thePlayer.openContainer);
    }

    public static void openInventory() {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
    }

    public static void openInventoryPacket() {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
    }

    public static void clickSlot(final int windowId, final int slot, final int mouseButton, final int mode) {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.playerController.windowClick(windowId, slot, mouseButton, mode, mc.thePlayer);
    }

    public static void clickSlot(final int slot, final int mouseButton, final int mode) {
        final Minecraft mc = Minecraft.getMinecraft();
        GuiUtils.clickSlot(mc.thePlayer.openContainer.windowId, slot, mouseButton, mode);
    }

    public static void clickSlot(final int slot, final int mouseButton) {
        GuiUtils.clickSlot(slot, mouseButton, 0);
    }

    public static void clickSlot(final int slot) {
        GuiUtils.clickSlot(slot, 0);
    }

    public static int searchContainer(final GuiContainer container, final Predicate<ItemStack> p) {
        final List<Slot> slots = container.inventorySlots.inventorySlots;
        for (final Slot slot : slots) {
            if (slot.inventory == Minecraft.getMinecraft().thePlayer.inventory || !p.test(slot.getStack())) continue;
            return slot.slotNumber;
        }
        return -1;
    }

    public static int searchInventory(final Predicate<ItemStack> p) {
        final InventoryPlayer playerInventory = Minecraft.getMinecraft().thePlayer.inventory;
        for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
            final ItemStack item = playerInventory.getStackInSlot(i);
            if (!p.test(item)) continue;
            return i;
        }
        return -1;
    }

    public static void openCustomScreen(final GuiScreen screen) {
        Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(null));
        EventBus.subscribe(new Object() {

            @SubscribeEvent
            public void onTick(final TickEvent.Post event) {
                Minecraft.getMinecraft().displayGuiScreen(screen);
                EventBus.unsubscribe(this);
            }
        });
    }
}

