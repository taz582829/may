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
    public void onTick(TickEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;
            AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());

            if (data == null) continue;

            boolean lookingDown = player.rotationPitch >= 45;
            boolean moving = data.moveSpeed > 0.001;
            boolean isPlacing = player.swingProgress > 0 && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock;
            boolean isJumping = System.currentTimeMillis() - data.lastAirTime < 1000 && System.currentTimeMillis() - data.lastAirTime > 2000;
            if (moving) {
                boolean isGoingFast = data.moveSpeed > (isJumping ? 2.5 : 4) && System.currentTimeMillis() - data.lastShiftEvent > 1000;
                if (lookingDown && isPlacing && isGoingFast) {
                    addFlag(player);
                } else {
                    removeFlag(player);
                }
            }
        }
    }
}
