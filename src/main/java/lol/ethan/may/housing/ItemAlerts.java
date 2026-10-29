package lol.ethan.may.housing;

import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.ChatEvent;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.utils.ChatUtils;
import lol.ethan.may.utils.ItemUtils;
import lol.ethan.may.utils.TimeUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.UUID;

public class ItemAlerts {
    Minecraft mc = Minecraft.getMinecraft();

    public void alert(final EntityPlayer player, final String itemDisplayName, final ItemStack item, final boolean wearing) {
        final String displayName = ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getName());
        final List<String> lore = ItemUtils.getLore(item);
        final UUID itemId = HousingUtils.addToItemMap(item);
        final String internalCommand = Entry.addInternalCommand(() -> HousingUtils.spawnItem(itemId));
        final IChatComponent button = ChatUtils.createChatButton(Entry.PREFIX + displayName + " §fis " + (wearing ? "wearing" : "holding") + " " + itemDisplayName, String.join("\n", lore), ClickEvent.Action.RUN_COMMAND, internalCommand);
        final IChatComponent copyNBTButton = ChatUtils.createChatButton("§b[Copy NBT]", ClickEvent.Action.RUN_COMMAND, Entry.addInternalCommand(() -> GuiScreen.setClipboardString(ItemUtils.getFullNBT(item).toString())));
        final IChatComponent tpButton = ChatUtils.createChatButton("§5[Teleport]", ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName());
        button.appendText(" ").appendSibling(copyNBTButton).appendText(" ").appendSibling(tpButton);
        Entry.chat(button);
    }

    public void alert(final Entity user, final String itemDisplayName, final ItemStack item) {
        final List<String> lore = ItemUtils.getLore(item);
        final UUID itemId = HousingUtils.addToItemMap(item);
        final String internalCommand = Entry.addInternalCommand(() -> HousingUtils.spawnItem(itemId));
        final IChatComponent button = ChatUtils.createChatButton(Entry.PREFIX + "§6Item Frame §fhas " + itemDisplayName, String.join("\n", lore), ClickEvent.Action.RUN_COMMAND, internalCommand);
        final IChatComponent copyNBTButton = ChatUtils.createChatButton("§b[Copy NBT]", ClickEvent.Action.RUN_COMMAND, Entry.addInternalCommand(() -> GuiScreen.setClipboardString(ItemUtils.getFullNBT(item).toString())));
        final IChatComponent tpButton = ChatUtils.createChatButton("§5[Teleport]", ClickEvent.Action.RUN_COMMAND, "/tp " + user.posX + " " + user.posY + " " + user.posZ);
        button.appendText(" ").appendSibling(copyNBTButton).appendText(" ").appendSibling(tpButton);
        Entry.chat(button);
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent.Receive event) {
        if (!HousingUtils.isInHousing()) {
            return;
        }
        if (event.getPacket() instanceof S04PacketEntityEquipment) {
            final S04PacketEntityEquipment packet = (S04PacketEntityEquipment) event.getPacket();
            final ItemStack item = packet.getItemStack();
            if (item == null) {
                return;
            }
            final Entity entity = mc.theWorld.getEntityByID(packet.getEntityID());
            if (!(entity instanceof EntityPlayer)) {
                return;
            }
            final EntityPlayer player = (EntityPlayer) entity;
            final Item i = item.getItem();
            if (i instanceof ItemPotion) {
                if (ItemPotion.isSplash(item.getItemDamage())) {
                    alert(player, "§cSplash Potion", item, packet.getEquipmentSlot() != 0);
                } else {
                    alert(player, "§aPotion", item, packet.getEquipmentSlot() != 0);
                }
            } else if (Block.getBlockFromItem(i) == Blocks.barrier) {
                alert(player, "§cBarrier", item, packet.getEquipmentSlot() != 0);
            } else if (i == Items.writable_book) {
                alert(player, "§4Signed Book", item, packet.getEquipmentSlot() != 0);
            } else if (Block.getBlockFromItem(i) == Blocks.dispenser) {
                alert(player, "§eDispenser", item, packet.getEquipmentSlot() != 0);
            } else if (Block.getBlockFromItem(i) == Blocks.skull) {
                alert(player, "§4Skull", item, packet.getEquipmentSlot() != 0);
            } else if (Config.itemLogging.enabled) {
                alert(player, item.getDisplayName(), item, packet.getEquipmentSlot() != 0);
            }
        } else if (event.getPacket() instanceof S1CPacketEntityMetadata) {
            final S1CPacketEntityMetadata packet = (S1CPacketEntityMetadata) event.getPacket();
            mc.addScheduledTask(() -> {
                final Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());
                if (entity instanceof EntityItemFrame) {
                    final EntityItemFrame frame = (EntityItemFrame) entity;
                    ItemStack item = null;
                    if (packet.func_149376_c().size() == 0) {
                        return;
                    }
                    for (final DataWatcher.WatchableObject obj : packet.func_149376_c()) {
                        final Object val = obj.getObject();
                        if (!(val instanceof ItemStack)) continue;
                        item = (ItemStack) val;
                        break;
                    }
                    if (item == null) {
                        return;
                    }
                    alert(frame, item.getDisplayName(), item);
                }
            });
        }
    }

    @SubscribeEvent
    public void onChat(final ChatEvent.Received event) {
        if (event.getMessage().getUnformattedText().startsWith("Unable to locate sign at ")) {
            event.setCancelled(true);
        } else if (event.getMessage().getUnformattedText().trim().equals("* First person to type /collect will earn free coins!")) {
            final int randomTime = RandomUtils.nextInt(100, 500);
            TimeUtils.setTimeout(() -> mc.addScheduledTask(() -> mc.thePlayer.sendChatMessage("/collect")), randomTime);
        }
    }
}

