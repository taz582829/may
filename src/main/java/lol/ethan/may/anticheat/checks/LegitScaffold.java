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
        if (!Config.legitScaffFlags.enabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;
            AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());

            if (data != null) {
                boolean lookingDown = player.rotationPitch >= 45;
                boolean moving = data.moveSpeed > 0.001;
                boolean isPlacing = player.swingProgress > 0 && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlock;

                if (moving) {

                    double speed = data.moveSpeed;
                    long time = System.currentTimeMillis();
                    List<AnticheatData.ShiftEvent> recentShifts = data.shiftEvents.stream().filter(shiftEvent ->
                            time - shiftEvent.timestamp < 2000 && shiftEvent.type == AnticheatData.ShiftEvent.Type.START
                    ).collect(Collectors.toList());
                    List<Double> shiftDurations = data.shiftEvents.stream()
                            .filter(s -> s.type == AnticheatData.ShiftEvent.Type.STOP && time - s.timestamp < 2000)
                            .map(s -> s.duration / 50)
                            .collect(Collectors.toList());
                    int durationSize = shiftDurations.size();
                    int shiftCount = recentShifts.size();

                    boolean consistent = durationSize >= 3
                            && shiftDurations.get(durationSize - 1) <= 2
                            && shiftDurations.get(durationSize - 2) <= 2
                            && shiftDurations.get(durationSize - 3) <= 2;
                    boolean shiftingTooMuch = shiftCount > 3 && speed > 2.5;
                    boolean isLegitScaff = shiftingTooMuch && isPlacing && lookingDown && consistent;

                    if (isLegitScaff) {
                        this.addFlag(player, 2);
                    } else {
                        this.removeFlag(player, 3);
                    }
                }
            }
        }
    }
}
