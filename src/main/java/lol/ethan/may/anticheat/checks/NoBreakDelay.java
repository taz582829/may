package lol.ethan.may.anticheat.checks;

import lol.ethan.may.Entry;
import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.util.EnumChatFormatting;

public class NoBreakDelay extends AnticheatFlag {
    public NoBreakDelay() {
        super(5, EnumChatFormatting.GOLD);
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent.Receive event) {
        if (!Config.noBreakDelayFlags.enabled) return;
        final Packet<?> raw = event.getPacket();
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (null == world) return;
        if (raw instanceof S25PacketBlockBreakAnim) {
            final S25PacketBlockBreakAnim packet = (S25PacketBlockBreakAnim) raw;
            if (0 != packet.getProgress()) return;
            final Entity breakerEntity = world.getEntityByID(packet.getBreakerId());
            if (breakerEntity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) breakerEntity;
                final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
                if (null != data) {
                    Entry.debug(player.getName() + " is breaking a block: " + data.breakDelay + " ticks left on break");
                    if (0 < data.breakDelay) {
                        addFlag(player, 2);
                    } else {
                        removeFlag(player);
                    }

                }
            }
        }
    }
}