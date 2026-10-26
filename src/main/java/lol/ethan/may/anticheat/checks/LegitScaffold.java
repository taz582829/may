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
        if (null == mc.theWorld || null == mc.thePlayer) return;

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;
            final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());

            if (null != data) {
                final boolean lookingDown = 45 <= player.rotationPitch;
                final boolean moving = 0.001 < data.moveSpeed;
                final boolean isPlacing = 0 < player.swingProgress && null != player.getHeldItem() && player.getHeldItem().getItem() instanceof ItemBlock;

                if (moving) {

                    final double speed = data.moveSpeed;
                    final long time = System.currentTimeMillis();
                    final List<AnticheatData.ShiftEvent> recentShifts = data.shiftEvents.stream().filter(shiftEvent ->
                            2000 > time - shiftEvent.timestamp && AnticheatData.ShiftEvent.Type.START == shiftEvent.type
                    ).collect(Collectors.toList());
                    final List<Double> shiftDurations = data.shiftEvents.stream()
                            .filter(s -> AnticheatData.ShiftEvent.Type.STOP == s.type && 2000 > time - s.timestamp)
                            .map(s -> s.duration / 50)
                            .collect(Collectors.toList());
                    final int durationSize = shiftDurations.size();
                    final int shiftCount = recentShifts.size();

                    final boolean consistent = 3 <= durationSize
                            && 2 >= shiftDurations.get(durationSize - 1)
                            && 2 >= shiftDurations.get(durationSize - 2)
                            && 2 >= shiftDurations.get(durationSize - 3);
                    final boolean shiftingTooMuch = 3 < shiftCount && 2.5 < speed;
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
