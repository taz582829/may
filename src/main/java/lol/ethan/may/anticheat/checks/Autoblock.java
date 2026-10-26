package lol.ethan.may.anticheat.checks;

import lol.ethan.may.Entry;
import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;

public class Autoblock extends AnticheatFlag {
    public Autoblock() {
        super(30, EnumChatFormatting.RED);
    }

    @SubscribeEvent
    public void onTick(TickEvent.Post event) {
        if (!Config.autoblockFlags.enabled) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        if (world == null) {
            return;
        }
        for (EntityPlayer player : world.playerEntities) {
            AnticheatData.Entry data;
            ItemStack heldItem;
            if (player == null || player == mc.thePlayer || (heldItem = player.getHeldItem()) == null || !(heldItem.getItem() instanceof ItemSword) || (data = AnticheatData.getEntry(player.getUniqueID())) == null)
                continue;
            int useItemTicks = data.useItemTicks;
            if (useItemTicks > 5 && player.isBlocking() && player.isSwingInProgress) {
                this.addFlag(player, 5);
                Entry.debug(player.getName() + ": " + this.getFlags(player) + "/" + this.threshold + " flags");
                continue;
            }
            if (useItemTicks != 0) continue;
            this.removeFlag(player, 2);
        }
    }
}

