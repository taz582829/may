package lol.ethan.may.anticheat;

import lol.ethan.may.Entry;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

public abstract class AnticheatFlag {
    protected final Minecraft mc = Minecraft.getMinecraft();
    public String name;
    public final EnumChatFormatting color;
    public int threshold;

    public AnticheatFlag(final String name, final int threshold, final EnumChatFormatting color) {
        this.name = name;
        this.threshold = threshold;
        this.color = color;
    }

    public AnticheatFlag(final int threshold, final EnumChatFormatting color) {
        name = getClass().getSimpleName();
        this.threshold = threshold;
        this.color = color;
    }

    protected int getFlags(final EntityPlayer player) {
        final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        return data.flags.getOrDefault(this, 0);
    }
    protected void addFlag(final EntityPlayer player, final int count) {
        final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        final int total = data.flags.getOrDefault(this, 0) + count;

        data.flags.put(this, total);

        if (total >= this.threshold) {
            this.sendFlagMessage(player);
            data.violations.put(this, data.violations.getOrDefault(this, 0) + 1);
        }
    }
    protected void addFlag(final EntityPlayer player) {
        this.addFlag(player, 1);
    }
    protected void removeFlag(final EntityPlayer player, final int count) {
        final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        data.flags.put(this, Math.max(data.flags.getOrDefault(this, 0) - count, 0));
    }
    protected void removeFlag(final EntityPlayer player) {
        this.removeFlag(player, 1);
    }

    protected void sendFlagMessage(final EntityPlayer player) {
        final AnticheatData.Entry data = AnticheatData.getEntry(player.getUniqueID());
        data.flags.remove(this);
        final String display = ScorePlayerTeam.formatPlayerName(
                player.getTeam(),
                player.getName()
        );
        Entry.chat("§bFrost §8» §r" + display + " §rflags §3" + this.name);
        if (Config.flagPlaySound.enabled) Entry.playSound("note.pling", 1, 2);
    }
}
