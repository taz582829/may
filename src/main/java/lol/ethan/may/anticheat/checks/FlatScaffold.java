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
        if (mc.theWorld == null || mc.thePlayer == null) return;

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;
            final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());

            if (data == null) continue;

            final boolean lookingDown = player.rotationPitch >= 45;
            final boolean moving = data.moveSpeed > 0.001;
            final boolean isPlacing = player.swingProgress > 0 && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock;
            final boolean isJumping = System.currentTimeMillis() - data.lastAirTime < 1000 && System.currentTimeMillis() - data.lastAirTime > 2000;
            if (moving) {
                final boolean isGoingFast = data.moveSpeed > (isJumping ? 2.5 : 4) && System.currentTimeMillis() - data.lastShiftEvent > 1000;
                if (lookingDown && isPlacing && isGoingFast) {
                    this.addFlag(player);
                } else {
                    this.removeFlag(player);
                }
            }
        }
    }
}
