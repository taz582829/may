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
    public void onTick(final TickEvent.Post event) {
        if (!Config.legitScaffFlags.enabled) return;
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;
            final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());

            if (data != null) {
                final boolean lookingDown = player.rotationPitch >= 45;
                final boolean moving = data.moveSpeed > 0.001;
                final boolean isPlacing = player.swingProgress > 0 && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock;

                if (moving) {

                    final double speed = data.moveSpeed;
                    final long time = System.currentTimeMillis();
                    final List<AnticheatData.ShiftEvent> recentShifts = data.shiftEvents.stream().filter(shiftEvent ->
                            time - shiftEvent.timestamp < 2000 && shiftEvent.type == AnticheatData.ShiftEvent.Type.START
                    ).collect(Collectors.toList());
                    final List<Double> shiftDurations = data.shiftEvents.stream()
                            .filter(s -> s.type == AnticheatData.ShiftEvent.Type.STOP && time - s.timestamp < 2000)
                            .map(s -> s.duration / 50)
                            .collect(Collectors.toList());
                    final int durationSize = shiftDurations.size();
                    final int shiftCount = recentShifts.size();

                    final boolean consistent = durationSize >= 3
                            && shiftDurations.get(durationSize - 1) <= 2
                            && shiftDurations.get(durationSize - 2) <= 2
                            && shiftDurations.get(durationSize - 3) <= 2;
                    final boolean shiftingTooMuch = shiftCount > 3 && speed > 2.5;
                    final boolean isLegitScaff = shiftingTooMuch && isPlacing && lookingDown && consistent;

                    if (isLegitScaff) {
                        addFlag(player, 2);
                    } else {
                        removeFlag(player, 3);
                    }
                }
            }
        }
    }
}
