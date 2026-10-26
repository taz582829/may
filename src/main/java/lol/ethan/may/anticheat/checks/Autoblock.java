package lol.ethan.may.anticheat.checks;

import lol.ethan.may.Entry;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;

public class Autoblock extends AnticheatFlag {
    public Autoblock() {
        super(30, EnumChatFormatting.RED);
    }

    @SubscribeEvent
    public void onTick(TickEvent.Post event) {
        if (!Config.autoblockFlags.enabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        if (world == null) return;

        for (EntityPlayer player : world.playerEntities) {
            if (player == null) continue;
            if (player == mc.thePlayer) continue;
            ItemStack heldItem = player.getHeldItem();
            if (heldItem == null) continue;
            if (heldItem.getItem() instanceof ItemSword) {
                AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
                if (data != null) {
                    int useItemTicks = data.useItemTicks;
                    if (useItemTicks > 5 && player.isBlocking() && player.isSwingInProgress) {

                        this.addFlag(player, 5);
                        Entry.debug(player.getName() + ": " + getFlags(player) + "/" + threshold + " flags");
                    } else if (useItemTicks == 0) {
                        this.removeFlag(player, 2);
                    }
                }
            }

        }
    }
}
