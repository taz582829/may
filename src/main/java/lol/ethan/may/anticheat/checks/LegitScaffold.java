package lol.ethan.may.anticheat.checks;

import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.stream.Collectors;

public class LegitScaffold extends AnticheatFlag {
    public LegitScaffold() {
        super("Legit Scaffold", 10, EnumChatFormatting.YELLOW);
    }

    @SubscribeEvent
    public void onTick(TickEvent.Post event) {
        if (!Config.legitScaffFlags.enabled) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            boolean isLegitScaff;
            boolean isPlacing;
            AnticheatData.Entry data;
            if (player == mc.thePlayer || (data = AnticheatData.getEntry(player.getUniqueID())) == null) continue;
            boolean lookingDown = player.rotationPitch >= 45.0f;
            boolean moving = data.moveSpeed > 0.001;
            boolean bl = isPlacing = player.swingProgress > 0.0f && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock;
            if (!moving) continue;
            double speed = data.moveSpeed;
            long time = System.currentTimeMillis();
            List recentShifts = data.shiftEvents.stream().filter(shiftEvent -> time - shiftEvent.timestamp < 2000L && shiftEvent.type == AnticheatData.ShiftEvent.Type.START).collect(Collectors.toList());
            List shiftDurations = data.shiftEvents.stream().filter(s -> s.type == AnticheatData.ShiftEvent.Type.STOP && time - s.timestamp < 2000L).map(s -> s.duration / 50.0).collect(Collectors.toList());
            int durationSize = shiftDurations.size();
            int shiftCount = recentShifts.size();
            boolean consistent = durationSize >= 3 && (Double) shiftDurations.get(durationSize - 1) <= 2.0 && (Double) shiftDurations.get(durationSize - 2) <= 2.0 && (Double) shiftDurations.get(durationSize - 3) <= 2.0;
            boolean shiftingTooMuch = shiftCount > 3 && speed > 2.5;
            boolean bl2 = isLegitScaff = shiftingTooMuch && isPlacing && lookingDown && consistent;
            if (isLegitScaff) {
                this.addFlag(player, 2);
                continue;
            }
            this.removeFlag(player, 3);
        }
    }
}

