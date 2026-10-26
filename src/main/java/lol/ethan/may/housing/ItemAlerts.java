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
import net.minecraft.scoreboard.Team;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.UUID;

public class ItemAlerts {
    Minecraft mc = Minecraft.getMinecraft();

    public void alert(EntityPlayer player, String itemDisplayName, ItemStack item, boolean wearing) {
        String displayName = ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getName());
        List<String> lore = ItemUtils.getLore(item);
        UUID itemId = HousingUtils.addToItemMap(item);
        String internalCommand = Entry.addInternalCommand(() -> HousingUtils.spawnItem(itemId));
        IChatComponent button = ChatUtils.createChatButton("§dMay §8» §r" + displayName + " §fis " + (wearing ? "wearing" : "holding") + " " + itemDisplayName, String.join("\n", lore), ClickEvent.Action.RUN_COMMAND, internalCommand);
        IChatComponent copyNBTButton = ChatUtils.createChatButton("§b[Copy NBT]", ClickEvent.Action.RUN_COMMAND, Entry.addInternalCommand(() -> GuiScreen.setClipboardString(ItemUtils.getFullNBT(item).toString())));
        IChatComponent tpButton = ChatUtils.createChatButton("§5[Teleport]", ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName());
        button.appendText(" ").appendSibling(copyNBTButton).appendText(" ").appendSibling(tpButton);
        Entry.chat(button);
    }

    public void alert(Entity user, String itemDisplayName, ItemStack item) {
        List<String> lore = ItemUtils.getLore(item);
        UUID itemId = HousingUtils.addToItemMap(item);
        String internalCommand = Entry.addInternalCommand(() -> HousingUtils.spawnItem(itemId));
        IChatComponent button = ChatUtils.createChatButton("§dMay §8» §r§6Item Frame §fhas " + itemDisplayName, String.join("\n", lore), ClickEvent.Action.RUN_COMMAND, internalCommand);
        IChatComponent copyNBTButton = ChatUtils.createChatButton("§b[Copy NBT]", ClickEvent.Action.RUN_COMMAND, Entry.addInternalCommand(() -> GuiScreen.setClipboardString(ItemUtils.getFullNBT(item).toString())));
        IChatComponent tpButton = ChatUtils.createChatButton("§5[Teleport]", ClickEvent.Action.RUN_COMMAND, "/tp " + user.posX + " " + user.posY + " " + user.posZ);
        button.appendText(" ").appendSibling(copyNBTButton).appendText(" ").appendSibling(tpButton);
        Entry.chat(button);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (!HousingUtils.isInHousing()) {
            return;
        }
        if (event.getPacket() instanceof S04PacketEntityEquipment) {
            S04PacketEntityEquipment packet = (S04PacketEntityEquipment) event.getPacket();
            ItemStack item = packet.getItemStack();
            if (item == null) {
                return;
            }
            Entity entity = this.mc.theWorld.getEntityByID(packet.getEntityID());
            if (!(entity instanceof EntityPlayer)) {
                return;
            }
            EntityPlayer player = (EntityPlayer) entity;
            Item i = item.getItem();
            if (i instanceof ItemPotion) {
                if (ItemPotion.isSplash(item.getItemDamage())) {
                    this.alert(player, "§cSplash Potion", item, packet.getEquipmentSlot() != 0);
                } else {
                    this.alert(player, "§aPotion", item, packet.getEquipmentSlot() != 0);
                }
            } else if (Block.getBlockFromItem(i) == Blocks.barrier) {
                this.alert(player, "§cBarrier", item, packet.getEquipmentSlot() != 0);
            } else if (i == Items.writable_book) {
                this.alert(player, "§4Signed Book", item, packet.getEquipmentSlot() != 0);
            } else if (Block.getBlockFromItem(i) == Blocks.dispenser) {
                this.alert(player, "§eDispenser", item, packet.getEquipmentSlot() != 0);
            } else if (Block.getBlockFromItem(i) == Blocks.skull) {
                this.alert(player, "§4Skull", item, packet.getEquipmentSlot() != 0);
            } else if (Config.itemLogging.enabled) {
                this.alert(player, item.getDisplayName(), item, packet.getEquipmentSlot() != 0);
            }
        } else if (event.getPacket() instanceof S1CPacketEntityMetadata) {
            S1CPacketEntityMetadata packet = (S1CPacketEntityMetadata) event.getPacket();
            this.mc.addScheduledTask(() -> {
                Entity entity = this.mc.theWorld.getEntityByID(packet.getEntityId());
                if (entity instanceof EntityItemFrame) {
                    EntityItemFrame frame = (EntityItemFrame) entity;
                    ItemStack item = null;
                    if (packet.func_149376_c().size() == 0) {
                        return;
                    }
                    for (DataWatcher.WatchableObject obj : packet.func_149376_c()) {
                        Object val = obj.getObject();
                        if (!(val instanceof ItemStack)) continue;
                        item = (ItemStack) val;
                        break;
                    }
                    if (item == null) {
                        return;
                    }
                    this.alert(frame, item.getDisplayName(), item);
                }
            });
        }
    }

    @SubscribeEvent
    public void onChat(ChatEvent.Received event) {
        if (event.getMessage().getUnformattedText().startsWith("Unable to locate sign at ")) {
            event.setCancelled(true);
        } else if (event.getMessage().getUnformattedText().trim().equals("* First person to type /collect will earn free coins!")) {
            int randomTime = RandomUtils.nextInt(100, 500);
            TimeUtils.setTimeout(() -> this.mc.addScheduledTask(() -> this.mc.thePlayer.sendChatMessage("/collect")), randomTime);
        }
    }
}

