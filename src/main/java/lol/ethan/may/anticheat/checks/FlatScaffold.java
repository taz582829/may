package lol.ethan.may.anticheat.checks;

import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumChatFormatting;

public class FlatScaffold extends AnticheatFlag {
    public FlatScaffold() {
        super(5, EnumChatFormatting.GREEN);
    }

    @SubscribeEvent
    public void onTick(final TickEvent.Post event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (null == mc.theWorld || null == mc.thePlayer) return;

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;
            final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());

            if (null == data) continue;

            final boolean lookingDown = 45 <= player.rotationPitch;
            final boolean moving = 0.001 < data.moveSpeed;
            final boolean isPlacing = 0 < player.swingProgress && null != player.getHeldItem() && player.getHeldItem().getItem() instanceof ItemBlock;
            final boolean isJumping = 1000 > System.currentTimeMillis() - data.lastAirTime && 2000 < System.currentTimeMillis() - data.lastAirTime;
            if (moving) {
                final boolean isGoingFast = data.moveSpeed > (isJumping ? 2.5 : 4) && 1000 < System.currentTimeMillis() - data.lastShiftEvent;
                if (lookingDown && isPlacing && isGoingFast) {
                    this.addFlag(player);
                } else {
                    this.removeFlag(player);
                }
            }
        }
    }
}
