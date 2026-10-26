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
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            boolean isGoingFast;
            boolean isJumping;
            AnticheatData.Entry data;
            if (player == mc.thePlayer || (data = AnticheatData.getEntry(player.getUniqueID())) == null) continue;
            boolean lookingDown = player.rotationPitch >= 45.0f;
            boolean moving = data.moveSpeed > 0.001;
            boolean isPlacing = player.swingProgress > 0.0f && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock;
            boolean bl = isJumping = System.currentTimeMillis() - data.lastAirTime < 1000L && System.currentTimeMillis() - data.lastAirTime > 2000L;
            if (!moving) continue;
            boolean bl2 = isGoingFast = data.moveSpeed > (isJumping ? 2.5 : 4.0) && System.currentTimeMillis() - data.lastShiftEvent > 1000L;
            if (lookingDown && isPlacing && isGoingFast) {
                this.addFlag(player);
                continue;
            }
            this.removeFlag(player);
        }
    }
}

