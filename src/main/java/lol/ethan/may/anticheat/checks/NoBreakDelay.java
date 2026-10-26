package lol.ethan.may.anticheat.checks;

import lol.ethan.may.Entry;
import lol.ethan.may.anticheat.AnticheatFlag;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.util.EnumChatFormatting;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;

public class NoBreakDelay extends AnticheatFlag {
    public NoBreakDelay() {
        super(5, EnumChatFormatting.GOLD);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Receive event) {
        if (!Config.noBreakDelayFlags.enabled) return;
        Packet<?> raw = event.getPacket();
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        if (raw instanceof S25PacketBlockBreakAnim) {
            S25PacketBlockBreakAnim packet = (S25PacketBlockBreakAnim) raw;
            if (packet.getProgress() != 0) return;
            Entity breakerEntity = world.getEntityByID(packet.getBreakerId());
            if (breakerEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) breakerEntity;
                AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
                if (data != null) {
                    Entry.debug(player.getName() + " is breaking a block: " + data.breakDelay + " ticks left on break");
                    if (data.breakDelay > 0) {
                        this.addFlag(player, 2);
                    } else {
                        this.removeFlag(player);
                    }

                }
            }
        }
    }
}