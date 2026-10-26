/*
package lol.ethan.may.hypixel;

import lol.ethan.may.events.render.DrawScreenEvent;
import lol.ethan.may.events.render.DrawSlotEvent;
import lol.ethan.may.mixin.accessors.GuiContainerAccessor;
import lol.ethan.may.utils.ColorUtil;
import lol.ethan.may.utils.ColorUtils;
import lol.ethan.may.utils.GuiUtils;
import lol.ethan.may.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import lol.ethan.may.events.SubscribeEvent;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplayHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern BED_PATTERN = Pattern.compile("^(\\w+)\\sBed\\sDestroyed$");

    @SubscribeEvent
    public void onDrawScreen(DrawScreenEvent.Post event) {
        if (!HypixelUtils.isOnHypixel()) return;
        GuiScreen screen = event.getScreen();

        if (!(screen instanceof GuiContainer)) return;

        GuiContainer container = (GuiContainer) screen;
        if (!(container.inventorySlots instanceof ContainerChest)) return;
        String title = GuiUtils.getTitle();
        if (!title.equalsIgnoreCase("Bookmarks")) return;

        final GuiContainerAccessor accessor = (GuiContainerAccessor) container;
        final int guiLeft = accessor.getGuiLeft();
        final int guiTop = accessor.getGuiTop();

        for (Slot slot : container.inventorySlots.inventorySlots) {
            if (slot == null) continue;

            final ItemStack item = slot.getStack();
            if (item == null) continue;
            final String itemName = StringUtils.stripControlCodes(item.getDisplayName());

            final int x = guiLeft + slot.xDisplayPosition;
            final int y = guiTop + slot.yDisplayPosition;
            Matcher matcher = BED_PATTERN.matcher(itemName);
            if (matcher.find()) {
                String teamName = matcher.group(1);
                int packedColor = getTeamColor(teamName);
                Color color = new Color(ColorUtils.setAlpha(packedColor, 200));
                renderSmallSquare(x, y, guiLeft, guiTop, color);
            }
        }


    }
    @SubscribeEvent
    public void onDrawSlot(DrawSlotEvent.Pre event) {
        if (!HypixelUtils.isOnHypixel()) return;
        Slot slot = event.slot;
        if (slot == null) return;

        if (!(slot.inventory instanceof ContainerChest)) return;
        String title = StringUtils.stripControlCodes(GuiUtils.getTitle());
        if (!title.equalsIgnoreCase("Bookmarks")) return;

        final ItemStack item = slot.getStack();
        if (item == null) return;
        final String itemName = StringUtils.stripControlCodes(item.getDisplayName());

        Matcher matcher = BED_PATTERN.matcher(itemName);
        if (matcher.find()) {
            ItemStack copy = item.copy();
            ItemStack bed = new ItemStack(Items.bed);
            bed.setTagInfo("display", copy.getTagCompound().getCompoundTag("display"));
            slot.putStack(bed);
        }
    }
    private int getTeamColor(String team) {
        switch (team) {
            case "Aqua":
                return ColorUtil.getColorInt(EnumChatFormatting.AQUA);
            case "Blue":
                return ColorUtil.getColorInt(EnumChatFormatting.BLUE);
            case "Gray":
                return ColorUtil.getColorInt(EnumChatFormatting.GRAY);
            case "Green":
                return ColorUtil.getColorInt(EnumChatFormatting.GREEN);
            case "Pink":
                return ColorUtil.getColorInt(EnumChatFormatting.LIGHT_PURPLE);
            case "Red":
                return ColorUtil.getColorInt(EnumChatFormatting.RED);
            case "White":
                return ColorUtil.getColorInt(EnumChatFormatting.WHITE);
            case "Yellow":
                return ColorUtil.getColorInt(EnumChatFormatting.YELLOW);
        }
        return 0;
    }
    private NetworkPlayerInfo getPlayerFromItem(ItemStack item) {
        for (String l : ItemUtils.getLore(item, false)) {
            String line = StringUtils.stripControlCodes(l);
            if (line.contains("Player: ")) {
                String[] parts = line.split(" ");
                String name = parts[parts.length - 1];

                for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    if (StringUtils.stripControlCodes(playerInfo.getGameProfile().getName()).equalsIgnoreCase(name)) return playerInfo;
                }
                break;
            }
        }
        return null;
    }
    private void renderSmallSquare(int x, int y, int guiLeft, int guiTop, Color color) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();

        Gui.drawRect(
                x,
                y,
                x + 16,
                y + 16,
                color.getRGB()
        );
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

 */