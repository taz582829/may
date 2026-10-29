package lol.ethan.may.anticheat.checks;

import lol.ethan.may.Entry;
import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class Timer extends AnticheatFlag {
    public Timer() {
        super("Timer", 10, EnumChatFormatting.LIGHT_PURPLE);
    }

    @SubscribeEvent
    public void onTick(TickEvent.Post event) {
        if (mc.theWorld == null) return;
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
            if (data.verticalMoveSpeed < 0 && data.verticalMoveSpeed > -1.) {
                Entry.debug(player.getName() + " is moving at " + data.verticalMoveSpeed + " bps");
            }
        }
    }
}
