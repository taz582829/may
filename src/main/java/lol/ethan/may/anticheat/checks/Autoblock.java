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
    public void onTick(final TickEvent.Post event) {
        if (!Config.autoblockFlags.enabled) return;
        final Minecraft mc = Minecraft.getMinecraft();
        final WorldClient world = mc.theWorld;
        if (null == world) return;

        for (final EntityPlayer player : world.playerEntities) {
            if (null == player) continue;
            if (player == mc.thePlayer) continue;
            final ItemStack heldItem = player.getHeldItem();
            if (null == heldItem) continue;
            if (heldItem.getItem() instanceof ItemSword) {
                final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
                if (null != data) {
                    final int useItemTicks = data.useItemTicks;
                    if (5 < useItemTicks && player.isBlocking() && player.isSwingInProgress) {

                        addFlag(player, 5);
                        Entry.debug(player.getName() + ": " + this.getFlags(player) + "/" + this.threshold + " flags");
                    } else if (0 == useItemTicks) {
                        removeFlag(player, 2);
                    }
                }
            }

        }
    }
}
